package com.ddhuy4298.testworker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.activities.LoginActivity;
import com.ddhuy4298.testworker.databinding.FragmentAccountBinding;
import com.ddhuy4298.testworker.listener.AccountItemListener;
import com.ddhuy4298.testworker.utils.Const;
import com.ddhuy4298.testworker.utils.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class AccountFragment extends BaseFragment<FragmentAccountBinding> implements AccountItemListener, ValueEventListener {

    private SharedPreferencesUtils preferences;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setListener(this);
    }

    @Override
    public void onLogoutClicked() {
        preferences = new SharedPreferencesUtils(getContext());
        preferences.remove(Const.KEY_EMAIL);
        preferences.remove(Const.KEY_PASSWORD);

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("job");
        reference.addListenerForSingleValueEvent(this);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String s = snapshot.getKey();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
            Toast.makeText(getActivity(), "Unsub", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
