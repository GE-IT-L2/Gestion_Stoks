import packages.Client;
import packages.ClientManager;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        ClientManager clientManager = new ClientManager(); // <-- use ClientManager
        int choice;

        do {
            System.out.println("================ Your choice? ================");
            System.out.println("1. Add a client");
            System.out.println("2. Display clients");
            System.out.println("3. Modify a client");
            System.out.println("4. Delete a client");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Client code (integer): ");
                    int clientCode = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Client name: ");
                    String clientName = sc.nextLine();

                    System.out.print("Phone number: ");
                    String phone = sc.nextLine();

                    System.out.print("Initial client debt: ");
                    double clientDebt = sc.nextDouble();
                    sc.nextLine();

                    Client newClient = new Client(clientCode, clientName, phone, clientDebt);
                    clientManager.addClient(newClient);
                    break;  // <-- you forgot this break in your original code!

                case 2:
                    System.out.println("==== List of clients ====");
                    clientManager.displayClients();
                    break;

                case 3:
                    System.out.print("Client code to modify: ");
                    int codeToModify = sc.nextInt();
                    sc.nextLine();

                    System.out.print("New name: ");
                    String newName = sc.nextLine();

                    System.out.print("New phone: ");
                    String newPhone = sc.nextLine();

                    System.out.print("New debt: ");
                    double newDebt = sc.nextDouble();
                    sc.nextLine();

                    if (clientManager.modifyClient(codeToModify, newName, newPhone, newDebt)) {
                        System.out.println("Client modified successfully!");
                    } else {
                        System.out.println("Client not found!");
                    }
                    break;

                case 4:
                    System.out.print("Client code to delete: ");
                    int codeToDelete = sc.nextInt();
                    sc.nextLine();

                    if (clientManager.deleteClient(codeToDelete)) {
                        System.out.println("Client deleted successfully!");
                    } else {
                        System.out.println("Client not found!");
                    }
                    break;

                case 5:
                    System.out.println("=== Goodbye! ===");
                    break;

                default:
                    System.out.println("==== Invalid choice, please try again. ====");
            }
        } while (choice != 5);

        sc.close();
    }
}