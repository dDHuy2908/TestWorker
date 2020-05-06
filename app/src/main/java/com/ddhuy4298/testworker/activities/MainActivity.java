package com.ddhuy4298.testworker.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.ddhuy4298.testworker.R;
import com.ddhuy4298.testworker.databinding.ActivityMainBinding;
import com.ddhuy4298.testworker.fragments.AccountFragment;
import com.ddhuy4298.testworker.fragments.BaseFragment;
import com.ddhuy4298.testworker.fragments.NewJobFragment;
import com.ddhuy4298.testworker.fragments.NotificationFragment;
import com.ddhuy4298.testworker.fragments.ReceivedJobFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private NewJobFragment fmNewJob = new NewJobFragment();
    private ReceivedJobFragment fmReceivedJob = new ReceivedJobFragment();
    private NotificationFragment fmNotification = new NotificationFragment();
    private AccountFragment fmAccount = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(4298);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.e(getClass().getName(), token);
            }
        });

        initFragment();
        binding.bottomNav.setOnNavigationItemSelectedListener(MainActivity.this);
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

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fmNewJob);
        transaction.add(R.id.container, fmReceivedJob);
        transaction.add(R.id.container, fmNotification);
        transaction.add(R.id.container, fmAccount);
        transaction.commit();
        showFragment(fmNewJob);
        binding.bottomNav.getMenu().getItem(0).setEnabled(false);
    }

    private void showFragment(BaseFragment fmShow) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.hide(fmNewJob);
        transaction.hide(fmReceivedJob);
        transaction.hide(fmNotification);
        transaction.hide(fmAccount);
        transaction.show(fmShow);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_nav_new_job:
                showFragment(fmNewJob);
                binding.bottomNav.getMenu().getItem(0).setChecked(true);
                binding.bottomNav.getMenu().getItem(0).setEnabled(false);
                binding.bottomNav.getMenu().getItem(1).setEnabled(true);
                binding.bottomNav.getMenu().getItem(2).setEnabled(true);
                binding.bottomNav.getMenu().getItem(3).setEnabled(true);
                break;
            case R.id.bottom_nav_received_job:
                showFragment(fmReceivedJob);
                binding.bottomNav.getMenu().getItem(1).setChecked(true);
                binding.bottomNav.getMenu().getItem(1).setEnabled(false);
                binding.bottomNav.getMenu().getItem(0).setEnabled(true);
                binding.bottomNav.getMenu().getItem(2).setEnabled(true);
                binding.bottomNav.getMenu().getItem(3).setEnabled(true);
                break;
            case R.id.bottom_nav_notification:
                showFragment(fmNotification);
                binding.bottomNav.getMenu().getItem(2).setChecked(true);
                binding.bottomNav.getMenu().getItem(2).setEnabled(false);
                binding.bottomNav.getMenu().getItem(1).setEnabled(true);
                binding.bottomNav.getMenu().getItem(0).setEnabled(true);
                binding.bottomNav.getMenu().getItem(3).setEnabled(true);
                break;
            case R.id.bottom_nav_account:
                showFragment(fmAccount);
                binding.bottomNav.getMenu().getItem(3).setChecked(true);
                binding.bottomNav.getMenu().getItem(3).setEnabled(false);
                binding.bottomNav.getMenu().getItem(1).setEnabled(true);
                binding.bottomNav.getMenu().getItem(2).setEnabled(true);
                binding.bottomNav.getMenu().getItem(0).setEnabled(true);
                break;
        }
        return true;
    }
}
