package com.ddhuy4298.testworker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ddhuy4298.testworker.models.Job;
import com.ddhuy4298.testworker.adapters.JobAdapter;
import com.ddhuy4298.testworker.listener.JobClickedListener;
import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.databinding.ActivityJobBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class JobActivity extends AppCompatActivity implements View.OnClickListener, JobClickedListener {

    private ActivityJobBinding binding;
    private JobAdapter adapter;
    private ArrayList<Job> data = new ArrayList<>();
    private int checked = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job);

        binding.rvJob.setLayoutManager(new GridLayoutManager(this, 2));
        binding.fab.setOnClickListener(this);
        binding.fab.setVisibility(View.GONE);
        initJob();
    }

    private void initJob() {
        adapter = new JobAdapter(getLayoutInflater());
        data.clear();
        data.add(new Job(R.drawable.ic_house_cleaning, "Dọn nhà", "HouseCleaning"));
        data.add(new Job(R.drawable.ic_house_transfer, "Chuyển nhà", "HouseTransfer"));
        data.add(new Job(R.drawable.ic_cooking, "Nấu ăn", "Cooking"));
        data.add(new Job(R.drawable.ic_washing, "Giặt là", "Washing"));
        data.add(new Job(R.drawable.ic_electronic_repair, "Sửa chữa đồ điện", "Electronic"));
        data.add(new Job(R.drawable.ic_computer_repair, "Sửa chữa máy tính", "Computer"));
        data.add(new Job(R.drawable.ic_water_repair, "Sửa chữa đường nước", "Water"));
        data.add(new Job(R.drawable.ic_repaint, "Sơn sửa nhà cửa", "Repaint"));
        if (adapter != null) {
            adapter.setData(data);
            adapter.setListener(this);
        }
        binding.rvJob.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        ArrayList<Job> checkedJob = adapter.getCheckedJob();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child("Worker").child(firebaseAuth.getCurrentUser().getUid()).child("job");
        for (Job job : checkedJob) {
            reference.child(job.getEnglishService()).setValue(true);

            FirebaseMessaging.getInstance().subscribeToTopic(job.getEnglishService())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(JobActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JobActivity.this, "Sub", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onJobClick(Job job) {
        job.setChecked(true);
        adapter.setMultipleCheck(true);
        binding.fab.setVisibility(View.VISIBLE);
        checked = 1;
        adapter.setListener(new JobClickedListener() {
            @Override
            public void onJobClick(Job job) {
                job.setChecked(!job.isChecked());

                if (job.isChecked()) {
                    checked++;
                } else {
                    checked--;
                }
                if (checked > 0) {
                    binding.fab.setVisibility(View.VISIBLE);
                } else {
                    binding.fab.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
