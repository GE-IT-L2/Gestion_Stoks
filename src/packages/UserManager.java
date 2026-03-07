package packages;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {

    private List<User> users = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addUser() {
        System.out.print("Enter ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter name: ");
        String userName = scanner.nextLine();

        System.out.print("Enter login: ");
        String login = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter role: ");
        String role = scanner.nextLine();

        User user = new User(userId, userName, login, password, role, true);
        users.add(user);
        System.out.println("User added: " + user.getUserName());
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    public List<User> getActiveUsers() {
        List<User> activeUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isActive()) {
                activeUsers.add(user);
            }
        }
        return activeUsers;
    }

    public void displayActiveUsers() {
        List<User> activeUsers = getActiveUsers();
        if (activeUsers.isEmpty()) {
            System.out.println("No active users.");
        } else {
            System.out.println("=== Active users ===");
            for (User user : activeUsers) {
                System.out.println(user);
            }
        }
    }

     public List<User> getInactiveUsers() {
        List<User> inactiveUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isActive()) {
                inactiveUsers.add(user);
            }
        }
        return inactiveUsers;
    }

     public void displayInactiveUsers() {
        List<User> inactiveUsers = getInactiveUsers();
        if (inactiveUsers.isEmpty()) {
            System.out.println("No inactive users.");
        } else {
            System.out.println("=== Inactive users ===");
            for (User user : inactiveUsers) {
                System.out.println(user);
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }
}