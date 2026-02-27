package packages;

import java.util.ArrayList;

public class ClientManager {
    private ArrayList<Client> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public void addClient(Client c) {
        clients.add(c);
    }

    public void displayClients() {
        for (Client c : clients) {
            System.out.println(c);
        }
    }

    public boolean modifyClient(int code, String newName, String newPhone, double newDebt) {
        for (Client c : clients) {
            if (c.getCode() == code) {
                c.setName(newName);
                c.setPhone(newPhone);
                c.setDebt(newDebt);
                return true;
            }
        }
        return false;
    }

    public boolean deleteClient(int code) {
        return clients.removeIf(c -> c.getCode() == code);
    }
}