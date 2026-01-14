package app;

import java.time.LocalDate;

public class Transaction {
    private String category;
    private double amount;
    private boolean income;
    private LocalDate date;
    private String description;

    public Transaction() { }

    public Transaction(String category, double amount, boolean income, LocalDate date, String description) {
        this.category = category;
        this.amount = amount;
        this.income = income;
        this.date = date;
        this.description = description == null ? "" : description;
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public boolean isIncome() { return income; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
}