package packages;

public class Product {
    private int id;
    private String designation;
    private double prixUnitaire;
    private String unite; //gros na detail
    private int stock;
    private Type_product type_product;

    public Product (int id, int stock, String designation, double prixUnitaire, String unite, Type_product type_product){
        this.id = id;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite;
        this.stock = stock;
        this.type_product = type_product;
    }


    public int getId(){
        return id;
    }

    public int getStock(){
        return stock;
    }
    public double getPrixUnitaire(){
        return prixUnitaire;
    }
    public String getDesignation(){
        return designation;
    }


    public void ajouterStock(int quantite){
        this.stock += quantite;
    }

    public void retirerStock(int quantite){
        if (quantite <= stock){
            this.stock -= quantite;
        }else{
            System.out.println("Stock insuffisant");
        }
    }

    public void afficher() {
        System.out.println("ID: " + id);
        System.out.println("Nom: " + designation);
        System.out.println("Prix: " + prixUnitaire);
        System.out.println("Stock: " + stock);
        System.out.println("Type: " + type_product.getNom());
        System.out.println("------------------------");
    }
}
