package app;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MoreStorageServiceTest {

    @Test
    void loadWalletЕслиJsonБитыйВозвращаетПустойКошелек() throws Exception {
        Path dir = Files.createTempDirectory("st-bad");
        StorageService storage = new StorageService(dir.toString());

        Path file = dir.resolve("wallet_bad.json");
        Files.writeString(file, "{ это не json }");

        Wallet w = storage.loadWallet("bad");
        assertNotNull(w);
        assertNotNull(w.getTransactions());
        assertNotNull(w.getBudgets());
        assertNotNull(w.getCategories());
    }

    @Test
    void importWalletJsonБитыйФайлКидаетИсключение() throws Exception {
        Path dir = Files.createTempDirectory("st-bad2");
        StorageService storage = new StorageService(dir.toString());

        Path file = dir.resolve("x.json");
        Files.writeString(file, "{ not_json ");

        assertThrows(Exception.class, () -> storage.importWalletJson(file));
    }
}