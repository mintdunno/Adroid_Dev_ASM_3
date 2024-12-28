package com.minh.payday.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.minh.payday.R;
import com.minh.payday.ui.chat.ChatFragment;
import com.minh.payday.ui.groups.GroupsFragment;
import com.minh.payday.ui.insight.InsightFragment;
import com.minh.payday.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavView = findViewById(R.id.bottomNavView);

        // Load default fragment (Groups, for instance)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerMain, new GroupsFragment())
                    .commit();
        }

        // Handle item selection with if-else blocks instead of switch/case
        bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_groups) {
                selectedFragment = new GroupsFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_insight) {
                selectedFragment = new InsightFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerMain, selectedFragment)
                        .commit();
                return true; // indicates handled
            }
            return false;
        });
    }
}
