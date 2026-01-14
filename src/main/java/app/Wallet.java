package app;

import java.util.*;

public class Wallet {
    private java.util.List<Transaction> transactions = new ArrayList<>();
    private java.util.Set<String> categories = new LinkedHashSet<>();
    private java.util.Map<String, Double> budgets = new LinkedHashMap<>();

    public java.util.List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(java.util.List<Transaction> transactions) { this.transactions = transactions; }

    public java.util.Set<String> getCategories() { return categories; }
    public void setCategories(java.util.Set<String> categories) { this.categories = categories; }

    public java.util.Map<String, Double> getBudgets() { return budgets; }
    public void setBudgets(java.util.Map<String, Double> budgets) { this.budgets = budgets; }
}