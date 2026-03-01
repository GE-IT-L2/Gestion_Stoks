package packages;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {

    private List<User> users = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addUser() {
        System.out.print("Entrez l'ID : ");
        String userId = scanner.nextLine();

        System.out.print("Entrez le nom : ");
        String userName = scanner.nextLine();

        System.out.print("Entrez le login : ");
        String login = scanner.nextLine();

        System.out.print("Entrez le mot de passe : ");
        String password = scanner.nextLine();

        System.out.print("Entrez le rôle : ");
        String role = scanner.nextLine();

        User user = new User(userId, userName, login, password, role);
        users.add(user);
        System.out.println("Utilisateur ajouté : " + user.getUserName());
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("Aucun utilisateur.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }
}