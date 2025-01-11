package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.adapters.MemberSplitAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText amountEditText;
    private Spinner paidBySpinner;
    private TextView dateTextView;
    private TextView splitTypeTextView;
    private RecyclerView membersRecyclerView;
    private MemberSplitAdapter memberSplitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        amountEditText = findViewById(R.id.amountEditText);
        paidBySpinner = findViewById(R.id.paidBySpinner);
        dateTextView = findViewById(R.id.dateTextView);
        splitTypeTextView = findViewById(R.id.splitTypeTextView);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);

        // Set up the RecyclerView
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberSplitAdapter = new MemberSplitAdapter(new ArrayList<>());
        membersRecyclerView.setAdapter(memberSplitAdapter);

        // TODO: Fetch group details and update UI
        // For example, you might get group members from an intent or a ViewModel
        // and then populate the paidBySpinner and memberSplitAdapter

        // Example for setting up the paidBySpinner (replace with actual data)
        List<String> memberNames = getGroupMembers(); // Replace with your method to fetch member names
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paidBySpinner.setAdapter(spinnerAdapter);
    }

    private List<String> getGroupMembers() {
        // TODO: Implement the logic to retrieve the members of the group
        // This is a placeholder. Replace with actual data fetching.
        List<String> members = new ArrayList<>();
        members.add("Long Phung (Me)");
        members.add("Minh C.");
        members.add("Minh N.");
        members.add("Khoi");
        return members;
    }

    // ... other methods for handling expense saving, date picking, etc. ...
}