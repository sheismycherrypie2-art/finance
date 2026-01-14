package app;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        Path dir = Files.createTempDirectory("finance-test");
        StorageService storage = new StorageService(dir.toString());
        userService = new UserService(storage);
    }

    @Test
    void регистрацияОк() {
        userService.register("u1", "p1");
        assertNotNull(userService.findUser("u1"));
    }

    @Test
    void регистрацияДубликатОшибка() {
        userService.register("u1", "p1");
        assertThrows(IllegalArgumentException.class, () -> userService.register("u1", "p2"));
    }

    @Test
    void логинОк() {
        userService.register("u1", "p1");
        userService.login("u1", "p1");
        assertTrue(userService.isLoggedIn());
        assertEquals("u1", userService.getCurrentUser().getLogin());
    }

    @Test
    void логинНеверныйПарольОшибка() {
        userService.register("u1", "p1");
        assertThrows(IllegalArgumentException.class, () -> userService.login("u1", "bad"));
    }
}