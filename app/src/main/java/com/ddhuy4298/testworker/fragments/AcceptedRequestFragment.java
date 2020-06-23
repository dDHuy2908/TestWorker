package com.ddhuy4298.testworker.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.adapters.RequestAdapter;
import com.ddhuy4298.testworker.databinding.FragmentAcceptedRequestBinding;
import com.ddhuy4298.testworker.listener.RequestClickedListener;
import com.ddhuy4298.testworker.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AcceptedRequestFragment extends BaseFragment<FragmentAcceptedRequestBinding> implements RequestClickedListener {

    private RequestAdapter adapter;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
            .child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requests").child("doing");

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_accepted_request;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RequestAdapter(getLayoutInflater());
        binding.rvAcceptedRequest.setAdapter(adapter);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Request> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Request request = snapshot.getValue(Request.class);
                    data.add(request);
                    Log.e("AcceptedRequest", data.size()+"");
                }
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.setListener(this);
    }

    @Override
    public void onRequestClick(Request request) {

    }
}
