package packages;

public class User {

    private String userId;
    private String userName;
    private String login;
    private String password;
    private String role;
    private boolean isActive;  

    public User(String userId, String userName, String login, String password, String role, boolean isActive) {
        this.userId = userId;
        this.userName = userName;
        this.login = login;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
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

    public boolean isActive() {return isActive; }
    public void setActive(boolean active) {isActive = active; }

    public void activate(){
        this.isActive = true;
        System.out.println("Account " + userName + " activated");
    }

    public void desactivate(){
        this.isActive = false;
        System.out.println("Account " + userName + " deactivated");
    }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username=" + userName + ", login=" + login + ", role=" + role + ",activate=" +(isActive ? "yes" : "no") + "}";
    }
}