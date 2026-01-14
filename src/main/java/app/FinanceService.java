package app;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {

    public void addCategory(Wallet wallet, String category) {
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Категория пустая");
        wallet.getCategories().add(category);
    }

    public void setBudget(Wallet wallet, String category, double amount) {
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Категория пустая");
        if (!wallet.getCategories().contains(category)) throw new IllegalArgumentException("Категория не найдена");
        if (amount <= 0) throw new IllegalArgumentException("Бюджет должен быть > 0");
        wallet.getBudgets().put(category, amount);
    }

    public List<String> addIncome(Wallet wallet, String category, double amount, LocalDate date, String desc) {
        validateOperation(wallet, category, amount, date);
        wallet.getTransactions().add(new Transaction(category, amount, true, date, desc));
        return buildAlerts(wallet, category);
    }

    public List<String> addExpense(Wallet wallet, String category, double amount, LocalDate date, String desc) {
        validateOperation(wallet, category, amount, date);
        wallet.getTransactions().add(new Transaction(category, amount, false, date, desc));
        return buildAlerts(wallet, category);
    }

    public double getTotalIncome(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getIncomeByCategory(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(Transaction::isIncome)
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }

    public Map<String, Double> getExpenseByCategory(Wallet wallet) {
        return wallet.getTransactions().stream()
                .filter(t -> !t.isIncome())
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
    }

    public FilterResult filteredStats(Wallet wallet, LocalDate from, LocalDate to, Set<String> categories) {
        Set<String> missing = new LinkedHashSet<>();
        if (categories != null) {
            for (String c : categories) {
                boolean exists = wallet.getTransactions().stream().anyMatch(t -> t.getCategory().equals(c));
                if (!exists) missing.add(c);
            }
        }

        double inc = 0;
        double exp = 0;
        int cnt = 0;

        for (Transaction t : wallet.getTransactions()) {
            if (from != null && t.getDate().isBefore(from)) continue;
            if (to != null && t.getDate().isAfter(to)) continue;
            if (categories != null && !categories.contains(t.getCategory())) continue;

            cnt++;
            if (t.isIncome()) inc += t.getAmount();
            else exp += t.getAmount();
        }

        return new FilterResult(cnt, inc, exp, inc - exp, missing);
    }

    public List<String> transfer(User from, User to, double amount, String category) {
        if (from == null || to == null) throw new IllegalArgumentException("Пользователь пустой");
        if (from.getLogin().equals(to.getLogin())) throw new IllegalArgumentException("Нельзя перевести самому себе");
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");
        if (category == null || category.isBlank()) category = "Перевод";

        if (!from.getWallet().getCategories().contains(category)) from.getWallet().getCategories().add(category);
        if (!to.getWallet().getCategories().contains(category)) to.getWallet().getCategories().add(category);

        from.getWallet().getTransactions().add(new Transaction(category, amount, false, LocalDate.now(),
                "перевод пользователю " + to.getLogin()));
        to.getWallet().getTransactions().add(new Transaction(category, amount, true, LocalDate.now(),
                "перевод от пользователя " + from.getLogin()));

        return buildAlerts(from.getWallet(), category);
    }

    private void validateOperation(Wallet wallet, String category, double amount, LocalDate date) {
        if (wallet == null) throw new IllegalArgumentException("Кошелёк пустой");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Категория пустая");
        if (!wallet.getCategories().contains(category)) throw new IllegalArgumentException("Категория не найдена (сначала добавь категорию)");
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");
        if (date == null) throw new IllegalArgumentException("Дата пустая");
    }

    private List<String> buildAlerts(Wallet wallet, String changedCategory) {
        List<String> alerts = new ArrayList<>();

        double income = getTotalIncome(wallet);
        double expense = getTotalExpense(wallet);
        double balance = income - expense;

        if (expense > income) alerts.add("Расходы превысили доходы");
        if (balance <= 0) alerts.add("Баланс <= 0");

        Double limit = wallet.getBudgets().get(changedCategory);
        if (limit != null) {
            double spent = getExpenseByCategory(wallet).getOrDefault(changedCategory, 0.0);
            double left = limit - spent;

            if (spent >= limit) alerts.add("Превышен бюджет по категории '" + changedCategory + "'. Остаток: " + fmt(left));
            else if (spent >= limit * 0.8) alerts.add("Потрачено 80% бюджета по '" + changedCategory + "'. Остаток: " + fmt(left));
        }

        return alerts;
    }

    private String fmt(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    public record FilterResult(int operationsCount,
                               double totalIncome,
                               double totalExpense,
                               double balance,
                               Set<String> missingCategories) {}
}