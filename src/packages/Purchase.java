package packages;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Purchase {

    private int           id;
    private Supplier      supplier;
    private Product       product;
    private int           quantity;
    private double        unitPrice;
    private double        total;
    private LocalDateTime purchaseDate;

    public Purchase(int id, Supplier supplier, Product product,
                    int quantity, double unitPrice, LocalDateTime purchaseDate) {
        this.id = id; this.supplier = supplier; this.product = product;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.total = quantity * unitPrice; this.purchaseDate = purchaseDate;
    }

    public Purchase(Supplier supplier, Product product, int quantity, double unitPrice) {
        this.supplier = supplier; this.product = product;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.total = quantity * unitPrice; this.purchaseDate = LocalDateTime.now();
    }

    public int           getId()           { return id; }
    public Supplier      getSupplier()     { return supplier; }
    public Product       getProduct()      { return product; }
    public int           getQuantity()     { return quantity; }
    public double        getUnitPrice()    { return unitPrice; }
    public double        getTotal()        { return total; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    /** Saves the purchase to the DB and updates the stock (atomic transaction). */
    public boolean process() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO purchases (supplier_code,product_id,quantity,unit_price,total) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, supplier.getSupplierCode()); ps.setInt(2, product.getId());
                ps.setInt(3, quantity); ps.setDouble(4, unitPrice); ps.setDouble(5, total);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.id = rs.getInt(1);
            }

            product.ajouterStock(quantity);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE products SET stock=? WHERE id=?")) {
                ps.setInt(1, product.getStock()); ps.setInt(2, product.getId());
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public void afficher() {
        System.out.println("  ┌── Purchase #" + id);
        System.out.println("  │ Date       : " + purchaseDate);
        System.out.println("  │ Supplier   : " + supplier.getSupplierName());
        System.out.println("  │ Product    : " + product.getDesignation());
        System.out.printf ("  │ Quantity   : %d %s%n", quantity, product.getUnite());
        System.out.printf ("  │ Unit price : %.2f Ar%n", unitPrice);
        System.out.printf ("  │ Total      : %.2f Ar%n", total);
        System.out.println("  └──────────────────────────────────────");
    }

    public static List<Purchase> findAll() {
        List<Purchase> list = new ArrayList<>();
        String sql = "SELECT pu.*, s.supplier_name, s.telephone, s.address, s.debt_supplier, "
                + "p.designation, p.prix_unitaire, p.unite, p.stock, p.type_product_id "
                + "FROM purchases pu "
                + "JOIN suppliers s ON pu.supplier_code=s.supplier_code "
                + "JOIN products  p ON pu.product_id=p.id "
                + "ORDER BY pu.purchase_date DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Supplier s = new Supplier(rs.getInt("supplier_code"), rs.getString("supplier_name"),
                        rs.getString("telephone"), rs.getString("address"), rs.getDouble("debt_supplier"));
                Product p = new Product(rs.getInt("product_id"), rs.getInt("stock"),
                        rs.getString("designation"), rs.getDouble("prix_unitaire"),
                        rs.getString("unite"), Type_product.findById(rs.getInt("type_product_id")));
                list.add(new Purchase(rs.getInt("id"), s, p, rs.getInt("quantity"),
                        rs.getDouble("unit_price"), rs.getTimestamp("purchase_date").toLocalDateTime()));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Purchase> findBySupplier(int code) {
        List<Purchase> r = new ArrayList<>();
        for (Purchase pu : findAll()) if (pu.getSupplier().getSupplierCode() == code) r.add(pu);
        return r;
    }

    public static List<Purchase> findByProduct(int pid) {
        List<Purchase> r = new ArrayList<>();
        for (Purchase pu : findAll()) if (pu.getProduct().getId() == pid) r.add(pu);
        return r;
    }
}