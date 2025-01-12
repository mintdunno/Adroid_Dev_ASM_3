package com.minh.payday.ui.balance;

import static com.minh.payday.ui.groups.AddExpenseActivity.OWNER_IDENTIFIER;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.data.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalancesViewModel extends ViewModel {

    private static final String TAG = "BalancesViewModel";
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private MutableLiveData<Map<String, Double>> balancesLiveData = new MutableLiveData<>();

    public BalancesViewModel() {
        expenseRepository = new ExpenseRepository();
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    public LiveData<Map<String, Double>> getBalances(String groupId) {
        loadBalances(groupId);
        return balancesLiveData;
    }

    private void loadBalances(String groupId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not logged in");
            return;
        }
        String currentUserId = currentUser.getUid();

        groupRepository.getGroupById(groupId).observeForever(group -> {
            if (group != null) {
                String groupOwnerId = group.getOwnerId();
                expenseRepository.getExpensesByGroup(groupId).observeForever(expenses -> {
                    Map<String, Double> balances = calculateBalances(expenses, currentUserId, groupOwnerId);
                    balancesLiveData.setValue(balances);
                });
            }
        });
    }

    private Map<String, Double> calculateBalances(List<Expense> expenses, String currentUserId, String groupOwnerId) {
        Map<String, Double> balances = new HashMap<>();
        for (Expense expense : expenses) {
            // Check if the current user is the owner of this expense
            if (expense.getOwnerId().equals(currentUserId)) {
                // If the current user is the owner
                for (Map.Entry<String, Double> entry : expense.getMemberAmounts().entrySet()) {
                    String memberId = entry.getKey();
                    double amount = entry.getValue();

                    // Update balance for the owner (excluding the OWNER_IDENTIFIER entry)
                    if (!memberId.equals(OWNER_IDENTIFIER)) {
                        balances.put(memberId, balances.getOrDefault(memberId, 0.0) + amount);
                    }
                }
            } else {
                // If the current user is not the owner
                for (Map.Entry<String, Double> entry : expense.getMemberAmounts().entrySet()) {
                    String memberId = entry.getKey();
                    double amount = entry.getValue();

                    // Update balance for the current user if they are a participant
                    if (memberId.equals(currentUserId)) {
                        balances.put(currentUserId, balances.getOrDefault(currentUserId, 0.0) - amount);
                    }
                }
            }
        }
        return balances;
    }
}