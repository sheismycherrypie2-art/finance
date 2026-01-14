package app;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MoreUserServiceTest {

    @Test
    void регистрацияПустойЛогинОшибка() throws Exception {
        Path dir = Files.createTempDirectory("us-more");
        UserService us = new UserService(new StorageService(dir.toString()));
        assertThrows(IllegalArgumentException.class, () -> us.register("   ", "p"));
    }

    @Test
    void регистрацияПустойПарольОшибка() throws Exception {
        Path dir = Files.createTempDirectory("us-more2");
        UserService us = new UserService(new StorageService(dir.toString()));
        assertThrows(IllegalArgumentException.class, () -> us.register("u", "   "));
    }

    @Test
    void logoutБезВходаНеПадает() throws Exception {
        Path dir = Files.createTempDirectory("us-more3");
        UserService us = new UserService(new StorageService(dir.toString()));
        assertDoesNotThrow(us::logout);
    }
}