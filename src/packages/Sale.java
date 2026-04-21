package packages;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Sale {

    private int           id;
    private Client        client;
    private Product       product;
    private int           quantity;
    private double        unitPrice;
    private double        total;
    private LocalDateTime saleDate;

    public Sale(int id, Client client, Product product,
                int quantity, double unitPrice, LocalDateTime saleDate) {
        this.id = id; this.client = client; this.product = product;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.total = quantity * unitPrice; this.saleDate = saleDate;
    }

    public Sale(Client client, Product product, int quantity, double unitPrice) {
        this.client = client; this.product = product;
        this.quantity = quantity; this.unitPrice = unitPrice;
        this.total = quantity * unitPrice; this.saleDate = LocalDateTime.now();
    }

    public int           getId()        { return id; }
    public Client        getClient()    { return client; }
    public Product       getProduct()   { return product; }
    public int           getQuantity()  { return quantity; }
    public double        getUnitPrice() { return unitPrice; }
    public double        getTotal()     { return total; }
    public LocalDateTime getSaleDate()  { return saleDate; }


    public boolean process() {
        if (product.getStock() < quantity) {
            System.out.println("  ✘ Insufficient stock! (available=" + product.getStock()
                    + ", requested=" + quantity + ")");
            return false;
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sales (client_code,product_id,quantity,unit_price,total) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, client.getCode()); ps.setInt(2, product.getId());
                ps.setInt(3, quantity); ps.setDouble(4, unitPrice); ps.setDouble(5, total);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) this.id = rs.getInt(1);
            }

            product.retirerStock(quantity);
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
        System.out.println("  ┌── Sale #" + id);
        System.out.println("  │ Date       : " + saleDate);
        System.out.println("  │ Client     : " + client.getName());
        System.out.println("  │ Product    : " + product.getDesignation());
        System.out.printf ("  │ Quantity   : %d %s%n", quantity, product.getUnite());
        System.out.printf ("  │ Unit price : %.2f Ar%n", unitPrice);
        System.out.printf ("  │ Total      : %.2f Ar%n", total);
        System.out.println("  └──────────────────────────────────────");
    }

    public static List<Sale> findAll() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT sa.*, c.name, c.phone, c.debt, "
                + "p.designation, p.prix_unitaire, p.unite, p.stock, p.type_product_id "
                + "FROM sales sa "
                + "JOIN clients  c ON sa.client_code=c.code "
                + "JOIN products p ON sa.product_id=p.id "
                + "ORDER BY sa.sale_date DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Client c = new Client(rs.getInt("client_code"), rs.getString("name"),
                        rs.getString("phone"), rs.getDouble("debt"));
                Product p = new Product(rs.getInt("product_id"), rs.getInt("stock"),
                        rs.getString("designation"), rs.getDouble("prix_unitaire"),
                        rs.getString("unite"), Type_product.findById(rs.getInt("type_product_id")));
                list.add(new Sale(rs.getInt("id"), c, p, rs.getInt("quantity"),
                        rs.getDouble("unit_price"), rs.getTimestamp("sale_date").toLocalDateTime()));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Sale> findByClient(int code) {
        List<Sale> r = new ArrayList<>();
        for (Sale s : findAll()) if (s.getClient().getCode() == code) r.add(s);
        return r;
    }

    public static List<Sale> findByProduct(int pid) {
        List<Sale> r = new ArrayList<>();
        for (Sale s : findAll()) if (s.getProduct().getId() == pid) r.add(s);
        return r;
    }
}