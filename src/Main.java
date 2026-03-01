import packages.*;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Type_product t1 = new Type_product(1, "Alimentaire");
        Type_product t2 = new Type_product(2, "Boisson");
        Type_product t3 = new Type_product(3, "Hygiène");
        Type_product t4 = new Type_product(4, "Autres");


        List<Product> listProducts = new ArrayList<>();

        
        Product riz = new Product(1, 100, "Riz", 2000.0, "kg", t1);
        Product sucre = new Product(2, 50, "Sucre", 1200.0, "kg", t1);
        Product eau = new Product(3, 200, "Eau", 2200.0, "L", t2);
        Product savon = new Product(4, 30, "Savon", 2000.0, "pièce", t3);
        Product papier = new Product(5, 20, "Papier", 200.0, "paquet", t4);


        listProducts.add(riz);
        listProducts.add(sucre);
        listProducts.add(eau);
        listProducts.add(savon);
        listProducts.add(papier);


        System.out.println("===== LISTE DES PRODUITS =====");
        for (Product p : listProducts) {
            p.afficher();
        }
    }
}
