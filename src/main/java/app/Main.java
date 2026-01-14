package app;

public class Main {
    public static void main(String[] args) {
        StorageService storageService = new StorageService("data");
        UserService userService = new UserService(storageService);
        FinanceService financeService = new FinanceService();
        CsvUtil csvUtil = new CsvUtil();
        ConsoleApp app = new ConsoleApp(userService, financeService, storageService, csvUtil);
        app.run();
    }
}