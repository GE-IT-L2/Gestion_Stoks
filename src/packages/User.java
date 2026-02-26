package packages;

public class User {

    private String userId;
    private String userName;
    private String login;
    private String password;
    private String role;

    public User(String userId, String userName, String login, String password, String role) {
        this.userId = userId;
        this.userName = userName;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username=" + userName + ", login=" + login + ", role=" + role + "}";
    }
}