package packages;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {

    private List<Client> clients = new ArrayList<>();



    public void loadFromDB() {
        clients = Client.findAll();
        System.out.println("  [ClientManager] " + clients.size() + " client(s) loaded.");
    }



    public boolean addClient(Client c) {
        if (c.save()) {
            clients.add(c);
            System.out.println("  ✔ Client added: " + c);
            return true;
        }
        System.out.println("  ✘ Failed to add client.");
        return false;
    }

    public boolean modifyClient(int code, String newName, String newPhone, double newDebt) {
        Client c = findByCode(code);
        if (c == null) {
            System.out.println("  ✘ Client not found (code=" + code + ").");
            return false;
        }
        c.setName(newName);
        c.setPhone(newPhone);
        c.setDebt(newDebt);
        if (c.update()) {
            System.out.println("  ✔ Client updated: " + c);
            return true;
        }
        System.out.println("  ✘ Failed to update in database.");
        return false;
    }

    public boolean deleteClient(int code) {
        Client c = findByCode(code);
        if (c != null && c.delete()) {
            clients.removeIf(x -> x.getCode() == code);
            System.out.println("  ✔ Client deleted (code=" + code + ").");
            return true;
        }
        System.out.println("  ✘ Cannot delete (code=" + code + ").");
        return false;
    }



    public void displayClients() {
        if (clients.isEmpty()) { System.out.println("  No clients registered."); return; }
        System.out.println("  ──── CLIENT LIST ────");
        clients.forEach(System.out::println);
    }



    public Client findByCode(int code) {
        return clients.stream().filter(c -> c.getCode() == code).findFirst().orElse(null);
    }



    public boolean payClientDebt(int code, double amount) {
        Client c = findByCode(code);
        if (c == null) {
            System.out.println("  ✘ Client not found (code=" + code + ").");
            return false;
        }
        System.out.printf("  Current debt of %s: %.2f Ar%n", c.getName(), c.getDebt());
        if (c.payDebt(amount)) {
            System.out.printf("  ✔ Payment of %.2f Ar recorded for %s.%n", amount, c.getName());
            System.out.printf("  ✔ Remaining balance: %.2f Ar%n", c.getDebt());
            return true;
        }
        System.out.println("  ✘ Payment failed.");
        return false;
    }

    public List<Client> getClients() { return clients; }
}