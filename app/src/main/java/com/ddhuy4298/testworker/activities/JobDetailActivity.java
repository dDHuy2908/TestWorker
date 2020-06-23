package com.ddhuy4298.testworker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.api.ApiBuilder;
import com.ddhuy4298.testworker.databinding.ActivityJobDetailBinding;
import com.ddhuy4298.testworker.listener.JobDetailClickedListener;
import com.ddhuy4298.testworker.models.Notification;
import com.ddhuy4298.testworker.models.Receiver;
import com.ddhuy4298.testworker.models.Request;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ddhuy4298.testworker.fragments.RequestFragment.REQUEST_ID;

public class JobDetailActivity extends AppCompatActivity {

    private ActivityJobDetailBinding binding;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child("User");
    private DatabaseReference workerReference = FirebaseDatabase.getInstance().getReference("Users").child("Worker")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests").child("doing");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_detail);
        Intent intent = getIntent();
        final Request request = (Request) intent.getSerializableExtra(REQUEST_ID);
        binding.tvJobName.setText(request.getJob());
        binding.tvAddress.setText(request.getAddress());
        binding.tvTime.setText(request.getTime());
        binding.tvDate.setText(request.getDate());
        final String[] userToken = {""};
        userReference.child(request.getUserId()).child("token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userToken[0] = dataSnapshot.getValue(String.class);
                Log.e("userToken", userToken[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.setListener(new JobDetailClickedListener() {
            @Override
            public void onAcceptClick() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                reference.child(request.getRequestId()).removeValue();
                userReference.child(request.getUserId()).child("requests").child(request.getRequestId()).child("status").setValue("Doing");
                copyFirebaseData(userReference.child(request.getUserId()).child("requests").child(request.getRequestId()),
                        workerReference.child(request.getRequestId()));

                Notification notification = new Notification();
                notification.setTitle("Request accepted!");
                notification.setBody("A request has just been accepted. Check it!");
                Receiver receiver = new Receiver();
                receiver.setTo(userToken[0]);
                receiver.setNotification(notification);

                ApiBuilder.getInstance()
                        .sendNotification(receiver)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    Snackbar.make(binding.jobDetailLayout, "Success", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(binding.jobDetailLayout, "Fail", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                finish();
            }

            @Override
            public void onCallUserClick() {

            }
        });
    }

    public void copyFirebaseData(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                        if (databaseError == null) {
//                            fromPath.removeValue();
//                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
