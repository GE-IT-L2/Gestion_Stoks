package packages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Product {

    private int          id;
    private String       designation;
    private double       prixUnitaire;
    private String       unite;
    private int          stock;
    private Type_product typeProduct;

    public Product(int id, int stock, String designation,
                   double prixUnitaire, String unite, Type_product typeProduct) {
        this.id = id; this.stock = stock; this.designation = designation;
        this.prixUnitaire = prixUnitaire; this.unite = unite; this.typeProduct = typeProduct;
    }

    public Product(int stock, String designation,
                   double prixUnitaire, String unite, Type_product typeProduct) {
        this.stock = stock; this.designation = designation;
        this.prixUnitaire = prixUnitaire; this.unite = unite; this.typeProduct = typeProduct;
    }

    public int          getId()           { return id; }
    public int          getStock()        { return stock; }
    public double       getPrixUnitaire() { return prixUnitaire; }
    public String       getDesignation()  { return designation; }
    public String       getUnite()        { return unite; }
    public Type_product getTypeProduct()  { return typeProduct; }

    public void setDesignation(String d)       { this.designation  = d; }
    public void setPrixUnitaire(double p)      { this.prixUnitaire = p; }
    public void setUnite(String u)             { this.unite        = u; }
    public void setTypeProduct(Type_product t) { this.typeProduct  = t; }

    public void    ajouterStock(int q) { this.stock += q; }
    public boolean retirerStock(int q) {
        if (q <= stock) { this.stock -= q; return true; }
        return false;
    }

    public void afficher() {
        System.out.println("  ┌─────────────────────────────────────");
        System.out.println("  │ ID          : " + id);
        System.out.println("  │ Name        : " + designation);
        System.out.printf ("  │ Price       : %.2f Ar%n", prixUnitaire);
        System.out.println("  │ Unit        : " + unite);
        System.out.println("  │ Stock       : " + stock);
        System.out.println("  │ Type        : " + (typeProduct != null ? typeProduct.getNom() : "N/A"));
        System.out.println("  └─────────────────────────────────────");
    }

    @Override
    public String toString() {
        return String.format("  [%d] %-30s | Price: %10.2f Ar | Stock: %5d | Unit: %-8s | Type: %s",
                id, designation, prixUnitaire, stock, unite,
                typeProduct != null ? typeProduct.getNom() : "N/A");
    }

    // ── DB ────────────────────────────────────────────────────────────────────

    public boolean save() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO products (designation,prix_unitaire,unite,stock,type_product_id) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, designation); ps.setDouble(2, prixUnitaire);
            ps.setString(3, unite);       ps.setInt(4, stock);
            if (typeProduct != null) ps.setInt(5, typeProduct.getId());
            else                     ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) this.id = rs.getInt(1);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean modify(String newDes, double newPrix, String newUnite, Type_product newType) {
        this.designation  = newDes;
        this.prixUnitaire = newPrix;
        this.unite        = newUnite;
        this.typeProduct  = newType;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE products SET designation=?,prix_unitaire=?,unite=?,type_product_id=? WHERE id=?")) {
            ps.setString(1, designation); ps.setDouble(2, prixUnitaire); ps.setString(3, unite);
            if (newType != null) ps.setInt(4, newType.getId());
            else                 ps.setNull(4, Types.INTEGER);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStockDB() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE products SET stock=? WHERE id=?")) {
            ps.setInt(1, stock); ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM products WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, t.nom AS tn FROM products p "
                   + "LEFT JOIN type_product t ON p.type_product_id=t.id ORDER BY p.id";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Type_product tp = rs.getString("tn") != null
                        ? new Type_product(rs.getInt("type_product_id"), rs.getString("tn")) : null;
                list.add(new Product(rs.getInt("id"), rs.getInt("stock"),
                        rs.getString("designation"), rs.getDouble("prix_unitaire"),
                        rs.getString("unite"), tp));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Product findById(int id) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT p.*, t.nom AS tn FROM products p "
              + "LEFT JOIN type_product t ON p.type_product_id=t.id WHERE p.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Type_product tp = rs.getString("tn") != null
                        ? new Type_product(rs.getInt("type_product_id"), rs.getString("tn")) : null;
                return new Product(rs.getInt("id"), rs.getInt("stock"),
                        rs.getString("designation"), rs.getDouble("prix_unitaire"),
                        rs.getString("unite"), tp);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}