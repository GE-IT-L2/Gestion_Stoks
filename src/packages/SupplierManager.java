package packages;
import java.util.ArrayList;
import java.util.List;

public class SupplierManager {
    private List<Supplier> suppliers = new ArrayList<>();

    public void addSupplier(Supplier f){
        suppliers.add(f);
        System.out.println("Supplier " + f.getSupplierName() + " added successfully");
    }

    
    public void afficherSuppliers(){
            for (Supplier f : suppliers) {
                System.out.println(f.getSupplierCode() + " - " + f.getSupplierName() + " - " + f.getTelephone() + " - " + f.getAddress() + " - " + f.getDebtSupplier());
            }
        }
}
