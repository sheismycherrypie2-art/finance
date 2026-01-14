package app;

public class User {
    private String login;
    private String password;
    private Wallet wallet;

    public User(String login, String password, Wallet wallet) {
        this.login = login;
        this.password = password;
        this.wallet = wallet;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
}