package com.ddhuy4298.testworker.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.databinding.ActivityJobDetailBinding;
import com.ddhuy4298.testworker.listener.JobDetailClickedListener;
import com.ddhuy4298.testworker.models.NewJob;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.ddhuy4298.testworker.fragments.NewJobFragment.REQUEST_ID;

public class JobDetailActivity extends AppCompatActivity implements JobDetailClickedListener {

    private ActivityJobDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_detail);
        binding.setListener(this);
        Intent intent = getIntent();
        NewJob newJob = (NewJob) intent.getSerializableExtra(REQUEST_ID);
        binding.tvJobName.setText(newJob.getJob());
    }

    @Override
    public void onAcceptClick() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCallUserClick() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
