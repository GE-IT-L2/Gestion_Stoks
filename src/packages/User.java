package packages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class User {

    private String  userId;
    private String  userName;
    private String  login;
    private String  password;
    private String  role;
    private boolean isActive;

    public User(String userId, String userName, String login,
                String password, String role, boolean isActive) {
        this.userId = userId; this.userName = userName; this.login = login;
        this.password = password; this.role = role; this.isActive = isActive;
    }

    public String  getUserId()   { return userId; }
    public String  getUserName() { return userName; }
    public String  getLogin()    { return login; }
    public String  getPassword() { return password; }
    public String  getRole()     { return role; }
    public boolean isActive()    { return isActive; }

    public void setUserId(String id)  { this.userId   = id; }
    public void setUserName(String n) { this.userName = n; }
    public void setLogin(String l)    { this.login    = l; }
    public void setPassword(String p) { this.password = p; }
    public void setRole(String r)     { this.role     = r; }
    public void setActive(boolean a)  { this.isActive = a; }

    public void activate()    { this.isActive = true;  System.out.println("  Account '" + userName + "' activated."); }
    public void desactivate() { this.isActive = false; System.out.println("  Account '" + userName + "' deactivated."); }

    @Override
    public String toString() {
        return String.format("  [%s] %-20s | Login: %-15s | Role: %-10s | Active: %s",
                userId, userName, login, role, isActive ? "Yes" : "No");
    }

    // ── DB ────────────────────────────────────────────────────────────────────

    public boolean save() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO users (user_id,user_name,login,password,role,is_active) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, userId); ps.setString(2, userName); ps.setString(3, login);
            ps.setString(4, password); ps.setString(5, role); ps.setBoolean(6, isActive);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE users SET user_name=?,login=?,password=?,role=?,is_active=? WHERE user_id=?")) {
            ps.setString(1, userName); ps.setString(2, login); ps.setString(3, password);
            ps.setString(4, role); ps.setBoolean(5, isActive); ps.setString(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM users WHERE user_id=?")) {
            ps.setString(1, userId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY user_id")) {
            while (rs.next())
                list.add(new User(rs.getString("user_id"), rs.getString("user_name"),
                        rs.getString("login"), rs.getString("password"),
                        rs.getString("role"), rs.getBoolean("is_active")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static User findByLogin(String login) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT * FROM users WHERE login=?")) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new User(rs.getString("user_id"), rs.getString("user_name"),
                    rs.getString("login"), rs.getString("password"),
                    rs.getString("role"), rs.getBoolean("is_active"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}