package com.ddhuy4298.testworker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ddhuy4298.testworker.RequestListCallback;
import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.activities.JobDetailActivity;
import com.ddhuy4298.testworker.adapters.RequestAdapter;
import com.ddhuy4298.testworker.databinding.FragmentNewJobBinding;
import com.ddhuy4298.testworker.listener.RequestClickedListener;
import com.ddhuy4298.testworker.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.ddhuy4298.testworker.activities.LoginActivity.REQUEST_CODE;

public class RequestFragment extends BaseFragment<FragmentNewJobBinding> implements RequestClickedListener {

    public static final String REQUEST_ID = "request_id";
    private RequestAdapter adapter;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
    private DatabaseReference jobReference = FirebaseDatabase.getInstance()
            .getReference("Users").child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("job");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child("User");

    private ArrayList<String> jobList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_job;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new RequestAdapter(getLayoutInflater());
        binding.rvNewJob.setAdapter(adapter);

        jobReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String s = snapshot.getKey();
                    jobList.add(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getJobList(new RequestListCallback() {
            @Override
            public void onCallback(ArrayList<String> jobList) {
                for (int i = 0; i < jobList.size(); i++) {
                    Query query = reference.orderByChild("job").equalTo(jobList.get(i));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<Request> data = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Request job = snapshot.getValue(Request.class);
                                data.add(job);
                            }
                            adapter.setData(data);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        adapter.setListener(this);
        Log.e("FragmentRequest", "onActCreated");
    }

    public void getJobList(final RequestListCallback callback) {
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
    public void onRequestClick(Request request) {
        Intent intent = new Intent(getActivity(), JobDetailActivity.class);
        intent.putExtra(REQUEST_ID, request);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//            }
//        }
    }
}
