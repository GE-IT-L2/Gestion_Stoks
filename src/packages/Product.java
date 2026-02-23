package packages;

public class Product {
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


    //Pour lire les variables private il faut des accesseur (getteurs)
    public int getStock(){
        return stock;
    }
    public double getPrixUnitaire(){
        return prixUnitaire;
    }
    public String getDesignation(){
        return designation;
    }


    //Methode
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
}
