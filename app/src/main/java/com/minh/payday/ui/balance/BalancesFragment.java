package com.minh.payday.ui.balance;

import static com.minh.payday.ui.groups.AddExpenseActivity.OWNER_IDENTIFIER;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.data.repository.UserRepository;
import com.minh.payday.ui.balance.adapters.BalancesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BalancesFragment extends Fragment {
    private static final String TAG = "BalancesFragment";
    private BalancesViewModel viewModel;
    private RecyclerView rvBalances;
    private TextView tvTotalBalance;
    private BalancesAdapter adapter;
    private String groupId;
    private String groupOwnerId;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public static BalancesFragment newInstance(String groupId) {
        BalancesFragment fragment = new BalancesFragment();
        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupRepository = new GroupRepository();
        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balances, container, false);
        rvBalances = view.findViewById(R.id.rvBalances);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);

        rvBalances.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BalancesAdapter(new ArrayList<>(), "");
        rvBalances.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(BalancesViewModel.class);
        observeGroupData();

        return view;
    }

    private void observeGroupData() {
        groupRepository.getGroupById(groupId).observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                groupOwnerId = group.getOwnerId();
                observeBalanceData();
            }
        });
    }

    private void observeBalanceData() {
        viewModel.getBalances(groupId).observe(getViewLifecycleOwner(), memberBalances -> {
            List<BalanceItem> balanceItems = new ArrayList<>();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = currentUser != null ? currentUser.getUid() : null;
            double totalBalance = 0.0;

            for (Map.Entry<String, Double> entry : memberBalances.entrySet()) {
                String memberId = entry.getKey();
                Double balance = entry.getValue();
                String displayName = memberId; // Default to member ID

                // Fetch user details only if the member is not the owner identifier
                if (!memberId.equals(OWNER_IDENTIFIER)) {
                    if (isUserId(memberId)) {
                        // Fetch and display user details
                        userRepository.fetchUserById(memberId).observe(getViewLifecycleOwner(), user -> {
                            if (user != null) {
                                String nameToDisplay = user.getFirstName();
                                if (memberId.equals(currentUserId)) {
                                    nameToDisplay += " (Me)";
                                }
                                updateBalanceItem(balanceItems, memberId, nameToDisplay, balance, memberBalances.size());
                            } else {
                                Log.e(TAG, "User data is null for participant ID: " + memberId);
                                updateBalanceItem(balanceItems, memberId, memberId, balance, memberBalances.size());
                            }
                        });
                    } else {
                        // For non-user (guest), use the participant ID as the display name
                        if (memberId.equals(currentUserId)) {
                            displayName += " (Me)";
                        }
                        updateBalanceItem(balanceItems, memberId, displayName, balance, memberBalances.size());
                    }
                } else {
                    // Handle the owner identifier
                    if (groupOwnerId != null && groupOwnerId.equals(currentUserId)) {
                        displayName = " (Me)"; // Mark the current user as the owner
                    }
                    updateBalanceItem(balanceItems, memberId, displayName, balance, memberBalances.size());
                }

                // Calculate total balance for the current user
                if (memberId.equals(currentUserId)) {
                    totalBalance += balance;
                }
            }

            updateUI(balanceItems, totalBalance);
        });
    }

    private void updateBalanceItem(List<BalanceItem> balanceItems, String memberId, String displayName, Double balance, int totalSize) {
        balanceItems.add(new BalanceItem(memberId, displayName, balance));
        if (balanceItems.size() == totalSize) {
            adapter.updateBalances(balanceItems);
        }
    }

    private void updateUI(List<BalanceItem> balanceItems, double totalBalance) {
        adapter.updateBalances(balanceItems);
        tvTotalBalance.setText(String.format(Locale.getDefault(), "$%.2f", totalBalance));
        if (totalBalance > 0) {
            tvTotalBalance.setTextColor(getResources().getColor(R.color.green_primary));
        } else if (totalBalance < 0) {
            tvTotalBalance.setTextColor(getResources().getColor(R.color.black));
        } else {
            tvTotalBalance.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private boolean isUserId(String id) {
        // Implement logic to check if the ID is a user ID or a guest identifier
        return id.length() == 28; // Assuming user IDs are 28 characters long
    }
}