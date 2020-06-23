package com.ddhuy4298.testworker.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.testworker.listener.LoginItemListener;
import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.databinding.ActivityLoginBinding;
import com.ddhuy4298.testworker.utils.Const;
import com.ddhuy4298.testworker.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements LoginItemListener {

    public static final int REQUEST_CODE = 1;
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AlertDialog dialog;
    private SharedPreferencesUtils preferences;
    private ArrayList<String> job = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setListener(this);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait ...")
                .setTheme(R.style.CustomSpotsDialog)
                .build();

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.e(getClass().getName(), token);
            }
        });

        preferences = new SharedPreferencesUtils(this);
        String email = preferences.getValue(Const.KEY_EMAIL);
        String password = preferences.getValue(Const.KEY_PASSWORD);
        if (email != null && password != null) {
            dialog.show();
            login(email, password);
            return;
        }

//        binding.edtEmail.setText("b@gmail.com");
//        binding.edtPassword.setText("123456");
    }

    /**
     * hide keyboard whem touch outside
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLoginClick() {
        dialog.show();
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Empty info", Toast.LENGTH_LONG).show();
        } else {
            login(email, password);
        }
    }

    private void login(final String email, final String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        dialog.dismiss();

                        final DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference("Users").child("Worker").child(firebaseAuth.getCurrentUser().getUid());

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                reference.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                if (dataSnapshot.hasChild("job")) {
                                    preferences.putString(Const.KEY_EMAIL, email);
                                    preferences.putString(Const.KEY_PASSWORD, password);
                                    final DatabaseReference jobReference = FirebaseDatabase.getInstance()
                                            .getReference("Users").child("Worker").child(firebaseAuth.getCurrentUser().getUid()).child("job");

                                    jobReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String s = snapshot.getKey();
                                                FirebaseMessaging.getInstance().subscribeToTopic(s);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                                    finish();
                                } else {
                                    preferences.putString(Const.KEY_EMAIL, email);
                                    preferences.putString(Const.KEY_PASSWORD, password);

                                    Intent intent = new Intent(LoginActivity.this, JobActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRegisterClick() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent,REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra(RegisterActivity.EXTRA_EMAIL);
                String password = data.getStringExtra(RegisterActivity.EXTRA_PASSWORD);
                binding.edtEmail.setText(email);
                binding.edtPassword.setText(password);
            }
        }
    }
}
