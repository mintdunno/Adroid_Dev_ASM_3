package com.minh.payday.ui.groups;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.minh.payday.R;

public class AddExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // You'll need to get the groupId from the intent and use it to add expenses to the correct group
    }
}