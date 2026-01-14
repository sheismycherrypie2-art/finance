package app;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final StorageService storageService;
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    public UserService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void register(String login, String password) {
        if (login == null || login.isBlank()) throw new IllegalArgumentException("Логин пустой");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Пароль пустой");
        if (users.containsKey(login)) throw new IllegalArgumentException("Пользователь уже существует");
        users.put(login, new User(login, password, new Wallet()));
    }

    public void login(String login, String password) {
        User user = users.get(login);
        if (user == null) throw new IllegalArgumentException("Пользователь не найден");
        if (!user.getPassword().equals(password)) throw new IllegalArgumentException("Неверный пароль");

        Wallet loaded = storageService.loadWallet(login);
        user.setWallet(loaded);

        currentUser = user;
    }

    public void logout() {
        if (currentUser != null) {
            storageService.saveWallet(currentUser.getLogin(), currentUser.getWallet());
        }
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User findUser(String login) {
        return users.get(login);
    }
}