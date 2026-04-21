package packages;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private List<User> users = new ArrayList<>();

    public void loadFromDB() {
        users = User.findAll();
        System.out.println("  [UserManager] " + users.size() + " user(s) loaded.");
    }

    public boolean addUser(User u) {
        if (u.save()) {
            users.add(u);
            System.out.println("  ✔ User added: " + u);
            return true;
        }
        System.out.println("  ✘ Failed to add (ID or login already in use?).");
        return false;
    }

    public boolean modifyUser(String userId, String newName, String newLogin,
                              String newPassword, String newRole) {
        User u = findById(userId);
        if (u == null) {
            System.out.println("  ✘ User not found (id=" + userId + ").");
            return false;
        }
        u.setUserName(newName);
        u.setLogin(newLogin);
        u.setPassword(newPassword);
        u.setRole(newRole);
        if (u.update()) {
            System.out.println("  ✔ User updated: " + u);
            return true;
        }
        System.out.println("  ✘ Failed to update in database.");
        return false;
    }

    public boolean deleteUser(String userId) {
        User u = findById(userId);
        if (u != null && u.delete()) {
            users.removeIf(x -> x.getUserId().equals(userId));
            System.out.println("  ✔ User deleted (id=" + userId + ").");
            return true;
        }
        System.out.println("  ✘ Cannot delete (id=" + userId + ").");
        return false;
    }

    public boolean setActive(String userId, boolean active) {
        User u = findById(userId);
        if (u == null) {
            System.out.println("  ✘ User not found (id=" + userId + ").");
            return false;
        }
        if (active) u.activate(); else u.desactivate();
        if (u.update()) {
            System.out.println("  ✔ Status updated in database.");
            return true;
        }
        System.out.println("  ✘ Failed to update in database.");
        return false;
    }


    public void displayUsers() {
        if (users.isEmpty()) { System.out.println("  No users found."); return; }
        System.out.println("  ──── ALL USERS ────");
        users.forEach(System.out::println);
    }

    public void displayActiveUsers() {
        List<User> active = getActiveUsers();
        if (active.isEmpty()) { System.out.println("  No active users."); return; }
        System.out.println("  ──── ACTIVE USERS ────");
        active.forEach(System.out::println);
    }

    public void displayInactiveUsers() {
        List<User> inactive = getInactiveUsers();
        if (inactive.isEmpty()) { System.out.println("  No inactive users."); return; }
        System.out.println("  ──── INACTIVE USERS ────");
        inactive.forEach(System.out::println);
    }

    public User findById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst().orElse(null);
    }

    public List<User> getActiveUsers() {
        List<User> r = new ArrayList<>();
        for (User u : users) if (u.isActive()) r.add(u);
        return r;
    }

    public List<User> getInactiveUsers() {
        List<User> r = new ArrayList<>();
        for (User u : users) if (!u.isActive()) r.add(u);
        return r;
    }

    public List<User> getUsers() { return users; }
}