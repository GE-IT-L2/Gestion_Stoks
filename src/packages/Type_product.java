package packages;

public class Type_product {
    private int id;
    private String nom;

    public Type_product(int id, String nom){
        this.id = id;
        this.nom = nom;
    }

    public String getNom(){
        return nom;
    }
}
