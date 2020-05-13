package com.ddhuy4298.testworker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ddhuy4298.testworker.JobListCallback;
import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.activities.LoginActivity;
import com.ddhuy4298.testworker.adapters.NewJobAdapter;
import com.ddhuy4298.testworker.databinding.FragmentNewJobBinding;
import com.ddhuy4298.testworker.listener.NewJobClickedListener;
import com.ddhuy4298.testworker.models.NewJob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewJobFragment extends BaseFragment<FragmentNewJobBinding> implements NewJobClickedListener {

    private NewJobAdapter adapter;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
    private DatabaseReference jobReference = FirebaseDatabase.getInstance()
            .getReference("Users").child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("job");

    private ArrayList<String> jobList = new ArrayList<>();
    private ArrayList<String> jobList1 = new ArrayList<>();
    private ArrayList<NewJob> data = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_job;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        adapter = new NewJobAdapter(getLayoutInflater());

        jobReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String s = snapshot.getKey();
                    jobList.add(s);
                    Log.e("abc", jobList.size() +"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getJobList(new JobListCallback() {
            @Override
            public void onCallback(ArrayList<String> jobList) {
                for (int i = 0; i < jobList.size(); i++) {
                    Query query = reference.orderByChild("job").equalTo(jobList.get(i));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                NewJob job = snapshot.getValue(NewJob.class);
                                data.add(job);
                            }
                            adapter.setData(data);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        adapter.setData(data);
        binding.rvNewJob.setAdapter(adapter);
        adapter.setListener(this);
    }

    public void getJobList(final JobListCallback callback) {
        jobReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onCallback(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNewJobClick(NewJob newJob) {

    }
}
