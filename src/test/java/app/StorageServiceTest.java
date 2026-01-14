package app;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StorageServiceTest {
    private StorageService storage;
    private Path dir;

    @BeforeEach
    void setUp() throws Exception {
        dir = Files.createTempDirectory("finance-test");
        storage = new StorageService(dir.toString());
    }

    @Test
    void сохранитьИЗагрузитьКошелёк() {
        Wallet w = new Wallet();
        w.getCategories().add("Еда");
        w.getBudgets().put("Еда", 100.0);
        w.getTransactions().add(new Transaction("Еда", 10, false, LocalDate.now(), "x"));

        storage.saveWallet("u", w);
        Wallet w2 = storage.loadWallet("u");

        assertTrue(w2.getCategories().contains("Еда"));
        assertEquals(100.0, w2.getBudgets().get("Еда"), 0.0001);
        assertEquals(1, w2.getTransactions().size());
    }

    @Test
    void загрузкаНесуществующегоФайлаПустойКошелёк() {
        Wallet w = storage.loadWallet("nope");
        assertNotNull(w);
        assertTrue(w.getTransactions().isEmpty());
    }

    @Test
    void экспортИмпортJson() throws Exception {
        Wallet w = new Wallet();
        w.getCategories().add("Такси");
        Path file = dir.resolve("export.json");

        storage.exportWalletJson(file, w);
        Wallet w2 = storage.importWalletJson(file);

        assertTrue(w2.getCategories().contains("Такси"));
    }
}