package app;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ConsoleApp {
    private final UserService userService;
    private final FinanceService financeService;
    private final StorageService storageService;
    private final CsvUtil csvUtil;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(UserService userService, FinanceService financeService, StorageService storageService, CsvUtil csvUtil) {
        this.userService = userService;
        this.financeService = financeService;
        this.storageService = storageService;
        this.csvUtil = csvUtil;
    }

    public void run() {
        System.out.println("Добро пожаловать в приложение для управления личными финансами!");
        while (true) {
            if (!userService.isLoggedIn()) {
                showAuthMenu();
                handleAuthMenu();
            } else {
                showMainMenu();
                handleMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println();
        System.out.println("=== Меню авторизации ===");
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Выход из приложения");
        System.out.print("Выберите действие: ");
    }

    private void handleAuthMenu() {
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "3" -> handleExit();
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void showMainMenu() {
        System.out.println();
        System.out.println("=== Главное меню (" + userService.getCurrentUser().getLogin() + ") ===");
        System.out.println("1. Добавить категорию");
        System.out.println("2. Добавить доход");
        System.out.println("3. Добавить расход");
        System.out.println("4. Установить/изменить бюджет категории");
        System.out.println("5. Показать сводку");
        System.out.println("6. Фильтр статистики (период/категории)");
        System.out.println("7. Перевод другому пользователю");
        System.out.println("8. Экспорт CSV");
        System.out.println("9. Импорт CSV");
        System.out.println("10. Экспорт JSON");
        System.out.println("11. Импорт JSON");
        System.out.println("12. Выход из аккаунта");
        System.out.println("13. Выход из приложения");
        System.out.print("Выберите действие: ");
    }

    private void handleMainMenu() {
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> handleAddCategory();
            case "2" -> handleAddIncome();
            case "3" -> handleAddExpense();
            case "4" -> handleSetBudget();
            case "5" -> handleSummary();
            case "6" -> handleFilterStats();
            case "7" -> handleTransfer();
            case "8" -> handleExportCsv();
            case "9" -> handleImportCsv();
            case "10" -> handleExportJson();
            case "11" -> handleImportJson();
            case "12" -> handleLogout();
            case "13" -> handleExit();
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private void handleRegister() {
        try {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine().trim();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();

            userService.register(login, password);
            System.out.println("Регистрация успешна.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine().trim();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();

            userService.login(login, password);
            System.out.println("Вход выполнен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleAddCategory() {
        try {
            System.out.print("Введите название категории: ");
            String category = scanner.nextLine().trim();
            financeService.addCategory(userService.getCurrentUser().getWallet(), category);
            System.out.println("Категория добавлена.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleAddIncome() {
        try {
            System.out.print("Введите категорию дохода: ");
            String category = scanner.nextLine().trim();
            System.out.print("Введите сумму дохода: ");
            double amount = parseMoney(scanner.nextLine().trim());

            LocalDate date = readDateOrToday();
            System.out.print("Описание (Enter если пусто): ");
            String desc = scanner.nextLine();

            var alerts = financeService.addIncome(userService.getCurrentUser().getWallet(), category, amount, date, desc);
            System.out.println("Доход добавлен.");
            printAlerts(alerts);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleAddExpense() {
        try {
            System.out.print("Введите категорию расхода: ");
            String category = scanner.nextLine().trim();
            System.out.print("Введите сумму расхода: ");
            double amount = parseMoney(scanner.nextLine().trim());

            LocalDate date = readDateOrToday();
            System.out.print("Описание (Enter если пусто): ");
            String desc = scanner.nextLine();

            var alerts = financeService.addExpense(userService.getCurrentUser().getWallet(), category, amount, date, desc);
            System.out.println("Расход добавлен.");
            printAlerts(alerts);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleSetBudget() {
        try {
            System.out.print("Введите категорию: ");
            String category = scanner.nextLine().trim();
            System.out.print("Введите бюджет: ");
            double amount = parseMoney(scanner.nextLine().trim());

            financeService.setBudget(userService.getCurrentUser().getWallet(), category, amount);
            System.out.println("Бюджет установлен/изменён.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleSummary() {
        Wallet w = userService.getCurrentUser().getWallet();

        double income = financeService.getTotalIncome(w);
        double expense = financeService.getTotalExpense(w);
        double balance = income - expense;

        System.out.println();
        System.out.println("=== Сводка ===");
        System.out.println("Общий доход: " + money(income));
        System.out.println("Общий расход: " + money(expense));
        System.out.println("Баланс: " + money(balance));

        System.out.println();
        System.out.println("Доходы по категориям:");
        printMap(financeService.getIncomeByCategory(w));

        System.out.println();
        System.out.println("Расходы по категориям:");
        printMap(financeService.getExpenseByCategory(w));

        System.out.println();
        System.out.println("Бюджеты:");
        Map<String, Double> budgets = w.getBudgets();
        if (budgets.isEmpty()) {
            System.out.println("Нет бюджетов.");
        } else {
            for (var e : budgets.entrySet()) {
                String cat = e.getKey();
                double limit = e.getValue();
                double spent = financeService.getExpenseByCategory(w).getOrDefault(cat, 0.0);
                double left = limit - spent;
                System.out.println(cat + ": лимит " + money(limit) + ", потрачено " + money(spent) + ", остаток " + money(left));
            }
        }

        System.out.println();
        if (expense > income) {
            System.out.println("Внимание: расходы превысили доходы!");
        }
        if (balance <= 0) {
            System.out.println("Внимание: баланс <= 0!");
        }
    }

    private void handleFilterStats() {
        try {
            System.out.print("Дата от (yyyy-MM-dd или Enter): ");
            String sFrom = scanner.nextLine().trim();
            System.out.print("Дата до (yyyy-MM-dd или Enter): ");
            String sTo = scanner.nextLine().trim();
            System.out.print("Категории через запятую (или Enter): ");
            String sCats = scanner.nextLine().trim();

            LocalDate from = sFrom.isEmpty() ? null : LocalDate.parse(sFrom);
            LocalDate to = sTo.isEmpty() ? null : LocalDate.parse(sTo);

            Set<String> cats = null;
            if (!sCats.isEmpty()) {
                cats = new LinkedHashSet<>();
                for (String c : sCats.split(",")) {
                    String cc = c.trim();
                    if (!cc.isEmpty()) cats.add(cc);
                }
            }

            var res = financeService.filteredStats(userService.getCurrentUser().getWallet(), from, to, cats);

            if (!res.missingCategories().isEmpty()) {
                System.out.println("Категории не найдены: " + String.join(", ", res.missingCategories()));
            }
            if (res.operationsCount() == 0) {
                System.out.println("Нет данных по фильтру.");
                return;
            }

            System.out.println("Операций: " + res.operationsCount());
            System.out.println("Доход: " + money(res.totalIncome()));
            System.out.println("Расход: " + money(res.totalExpense()));
            System.out.println("Баланс: " + money(res.balance()));
        } catch (Exception e) {
            System.out.println("Ошибка: неправильный ввод фильтра.");
        }
    }

    private void handleTransfer() {
        try {
            System.out.print("Кому (логин): ");
            String toLogin = scanner.nextLine().trim();

            User toUser = userService.findUser(toLogin);
            if (toUser == null) {
                System.out.println("Ошибка: пользователь не найден.");
                return;
            }

            System.out.print("Категория (Enter = Перевод): ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = "Перевод";

            System.out.print("Сумма: ");
            double amount = parseMoney(scanner.nextLine().trim());

            var alerts = financeService.transfer(userService.getCurrentUser(), toUser, amount, category);

            storageService.saveWallet(toUser.getLogin(), toUser.getWallet());
            storageService.saveWallet(userService.getCurrentUser().getLogin(), userService.getCurrentUser().getWallet());

            System.out.println("Перевод выполнен.");
            printAlerts(alerts);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void handleExportCsv() {
        try {
            System.out.print("Файл (например report.csv): ");
            String file = scanner.nextLine().trim();

            if (!file.toLowerCase().endsWith(".csv")) {
                file = file + ".csv";
            }

            csvUtil.exportCsv(Path.of(file), userService.getCurrentUser().getWallet());
            System.out.println("Экспорт CSV выполнен.");
        } catch (Exception e) {
            System.out.println("Ошибка экспорта CSV.");
        }
    }

    private void handleImportCsv() {
        try {
            System.out.print("Файл (например report.csv): ");
            String file = scanner.nextLine().trim();

            if (!file.toLowerCase().endsWith(".csv")) {
                System.out.println("Ошибка: файл должен иметь расширение .csv");
                return;
            }

            Wallet w = csvUtil.importCsv(Path.of(file));
            userService.getCurrentUser().setWallet(w);
            System.out.println("Импорт CSV выполнен.");
        } catch (Exception e) {
            System.out.println("Ошибка импорта CSV.");
        }
    }

    private void handleExportJson() {
        try {
            System.out.print("Файл (например wallet.json): ");
            String file = scanner.nextLine().trim();

            if (!file.toLowerCase().endsWith(".json")) {
                file = file + ".json";
            }

            storageService.exportWalletJson(Path.of(file), userService.getCurrentUser().getWallet());
            System.out.println("Экспорт JSON выполнен.");
        } catch (Exception e) {
            System.out.println("Ошибка экспорта JSON.");
        }
    }

    private void handleImportJson() {
        try {
            System.out.print("Файл (например wallet.json): ");
            String file = scanner.nextLine().trim();

            if (!file.toLowerCase().endsWith(".json")) {
                System.out.println("Ошибка: файл должен иметь расширение .json");
                return;
            }

            Wallet w = storageService.importWalletJson(Path.of(file));
            userService.getCurrentUser().setWallet(w);
            System.out.println("Импорт JSON выполнен.");
        } catch (Exception e) {
            System.out.println("Ошибка импорта JSON.");
        }
    }

    private void handleLogout() {
        userService.logout();
        System.out.println("Вы вышли из аккаунта.");
    }

    private void handleExit() {
        if (userService.isLoggedIn()) userService.logout();
        System.out.println("До свидания!");
        System.exit(0);
    }

    private void printAlerts(java.util.List<String> alerts) {
        for (String a : alerts) {
            System.out.println("Внимание: " + a);
        }
    }

    private double parseMoney(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Пустая сумма");
        try {
            double v = Double.parseDouble(s.replace(",", "."));
            if (v <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное число");
        }
    }

    private LocalDate readDateOrToday() {
        System.out.print("Дата (yyyy-MM-dd или Enter = сегодня): ");
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) return LocalDate.now();
        return LocalDate.parse(s);
    }

    private String money(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private void printMap(Map<String, Double> map) {
        if (map.isEmpty()) {
            System.out.println("Нет данных.");
            return;
        }
        for (var e : map.entrySet()) {
            System.out.println(e.getKey() + ": " + money(e.getValue()));
        }
    }
}