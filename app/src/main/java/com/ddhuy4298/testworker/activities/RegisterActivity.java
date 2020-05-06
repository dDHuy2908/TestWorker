package com.ddhuy4298.testworker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.databinding.ActivityRegisterBinding;
import com.ddhuy4298.testworker.listener.RegisterItemListener;
import com.ddhuy4298.testworker.models.Worker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements RegisterItemListener {

    public static final String EXTRA_EMAIL = "extra.email";
    public static final String EXTRA_PASSWORD = "extra.password";
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        binding.setListener(this);

        binding.edtEmail.setText("b@gmail.com");
        binding.edtName.setText("B");
        binding.edtPassword.setText("123456");
        binding.edtPhoneNumber.setText("0987654321");
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
    public void onRegisterClick() {
        final String email = binding.edtEmail.getText().toString();
        final String password = binding.edtPassword.getText().toString();
        final String name = binding.edtName.getText().toString();
        final String phone = binding.edtPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(RegisterActivity.this, "Empty info", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password requires at least 6 characters", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Worker worker = new Worker();
                            worker.setName(name);
                            worker.setEmail(email);
                            worker.setPassword(password);
                            worker.setPhoneNum(phone);
                            String workerId = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                    .getReference("Users").child("Worker").child(workerId);
                            reference.setValue(worker)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent();
                                            intent.putExtra(EXTRA_EMAIL, email);
                                            intent.putExtra(EXTRA_PASSWORD, password);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onBackClick() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
