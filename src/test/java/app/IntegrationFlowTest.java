package app;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationFlowTest {
    private Path dir;
    private StorageService storage;
    private UserService userService;
    private FinanceService finance;

    @BeforeEach
    void setUp() throws Exception {
        dir = Files.createTempDirectory("finance-it");
        storage = new StorageService(dir.toString());
        userService = new UserService(storage);
        finance = new FinanceService();
    }

    @Test
    void сценарийЛогинДобавлениеСохранениеЗагрузка() {
        userService.register("a", "p");
        userService.login("a", "p");

        Wallet w = userService.getCurrentUser().getWallet();
        finance.addCategory(w, "Еда");
        finance.addIncome(w, "Еда", 100, LocalDate.now(), "");
        userService.logout();

        Wallet loaded = storage.loadWallet("a");
        assertEquals(1, loaded.getTransactions().size());
    }

    @Test
    void переводМеждуПользователями() {
        userService.register("a", "p");
        userService.register("b", "p");

        userService.login("a", "p");
        finance.addCategory(userService.getCurrentUser().getWallet(), "Перевод");

        User b = userService.findUser("b");
        b.setWallet(storage.loadWallet("b"));

        var alerts = finance.transfer(userService.getCurrentUser(), b, 50, "Перевод");
        assertNotNull(alerts);

        assertEquals(1, userService.getCurrentUser().getWallet().getTransactions().size());
        assertEquals(1, b.getWallet().getTransactions().size());
    }
}