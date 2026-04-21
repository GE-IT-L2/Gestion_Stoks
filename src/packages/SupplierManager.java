package packages;

import java.util.ArrayList;
import java.util.List;

public class SupplierManager {

    private List<Supplier> suppliers = new ArrayList<>();



    public void loadFromDB() {
        suppliers = Supplier.findAll();
        System.out.println("  [SupplierManager] " + suppliers.size() + " supplier(s) loaded.");
    }



    public boolean addSupplier(Supplier s) {
        if (s.save()) {
            suppliers.add(s);
            System.out.println("  ✔ Supplier added: " + s);
            return true;
        }
        System.out.println("  ✘ Failed to add supplier.");
        return false;
    }

    public boolean modifySupplier(int code, String newName, String newTel,
                                  String newAddress, double newDebt) {
        Supplier s = findByCode(code);
        if (s == null) {
            System.out.println("  ✘ Supplier not found (code=" + code + ").");
            return false;
        }
        s.setSupplierName(newName);
        s.setTelephone(newTel);
        s.setAddress(newAddress);
        s.setDebtSupplier(newDebt);
        if (s.update()) {
            System.out.println("  ✔ Supplier updated: " + s);
            return true;
        }
        System.out.println("  ✘ Failed to update in database.");
        return false;
    }

    public boolean deleteSupplier(int code) {
        Supplier s = findByCode(code);
        if (s != null && s.delete()) {
            suppliers.removeIf(x -> x.getSupplierCode() == code);
            System.out.println("  ✔ Supplier deleted (code=" + code + ").");
            return true;
        }
        System.out.println("  ✘ Cannot delete (code=" + code + ").");
        return false;
    }



    public void displaySuppliers() {
        if (suppliers.isEmpty()) { System.out.println("  No suppliers registered."); return; }
        System.out.println("  ──── SUPPLIER LIST ────");
        suppliers.forEach(System.out::println);
    }



    public Supplier findByCode(int code) {
        return suppliers.stream()
                .filter(s -> s.getSupplierCode() == code)
                .findFirst().orElse(null);
    }



    public boolean paySupplierDebt(int code, double amount) {
        Supplier s = findByCode(code);
        if (s == null) {
            System.out.println("  ✘ Supplier not found (code=" + code + ").");
            return false;
        }
        System.out.printf("  Current debt of %s: %.2f Ar%n", s.getSupplierName(), s.getDebtSupplier());
        if (s.payDebt(amount)) {
            System.out.printf("  ✔ Payment of %.2f Ar recorded for %s.%n", amount, s.getSupplierName());
            System.out.printf("  ✔ Remaining balance: %.2f Ar%n", s.getDebtSupplier());
            return true;
        }
        System.out.println("  ✘ Payment failed.");
        return false;
    }

    public List<Supplier> getSuppliers() { return suppliers; }
}