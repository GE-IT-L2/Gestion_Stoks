package packages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Supplier {

    private int    supplierCode;
    private String supplierName;
    private String telephone;
    private String address;
    private double debtSupplier;

    public Supplier(int supplierCode, String supplierName,
                    String telephone, String address, double debtSupplier) {
        this.supplierCode = supplierCode; this.supplierName = supplierName;
        this.telephone = telephone;       this.address = address;
        this.debtSupplier = debtSupplier;
    }

    public Supplier(String supplierName, String telephone,
                    String address, double debtSupplier) {
        this.supplierName = supplierName; this.telephone = telephone;
        this.address = address;           this.debtSupplier = debtSupplier;
    }

    public int    getSupplierCode() { return supplierCode; }
    public String getSupplierName() { return supplierName; }
    public String getTelephone()    { return telephone; }
    public String getAddress()      { return address; }
    public double getDebtSupplier() { return debtSupplier; }

    public void setSupplierName(String n) { this.supplierName = n; }
    public void setTelephone(String t)    { this.telephone    = t; }
    public void setAddress(String a)      { this.address      = a; }
    public void setDebtSupplier(double d) { this.debtSupplier = d; }

    @Override
    public String toString() {
        return String.format("  [%d] %-25s | Phone: %-15s | Address: %-25s | Debt: %.2f Ar",
                supplierCode, supplierName, telephone, address, debtSupplier);
    }



    public boolean save() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO suppliers (supplier_name,telephone,address,debt_supplier) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplierName); ps.setString(2, telephone);
            ps.setString(3, address);      ps.setDouble(4, debtSupplier);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) this.supplierCode = rs.getInt(1);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE suppliers SET supplier_name=?,telephone=?,address=?,debt_supplier=? WHERE supplier_code=?")) {
            ps.setString(1, supplierName); ps.setString(2, telephone);
            ps.setString(3, address);      ps.setDouble(4, debtSupplier);
            ps.setInt(5, supplierCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM suppliers WHERE supplier_code=?")) {
            ps.setInt(1, supplierCode); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    
    public boolean payDebt(double amount) {
        if (amount <= 0) {
            System.out.println("  ✘ Amount must be positive.");
            return false;
        }
        if (amount > this.debtSupplier) {
            System.out.printf("  ✘ Amount (%.2f Ar) exceeds the current debt (%.2f Ar).%n", amount, this.debtSupplier);
            return false;
        }
        this.debtSupplier -= amount;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE suppliers SET debt_supplier=? WHERE supplier_code=?")) {
                ps.setDouble(1, this.debtSupplier);
                ps.setInt(2, this.supplierCode);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments (entity_type, entity_code, amount, payment_date) VALUES(?,?,?,NOW())")) {
                ps.setString(1, "SUPPLIER");
                ps.setInt(2, this.supplierCode);
                ps.setDouble(3, amount);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            this.debtSupplier += amount;
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static List<Supplier> findAll() {
        List<Supplier> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM suppliers ORDER BY supplier_code")) {
            while (rs.next())
                list.add(new Supplier(rs.getInt("supplier_code"), rs.getString("supplier_name"),
                        rs.getString("telephone"), rs.getString("address"),
                        rs.getDouble("debt_supplier")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Supplier findByCode(int code) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT * FROM suppliers WHERE supplier_code=?")) {
            ps.setInt(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Supplier(rs.getInt("supplier_code"), rs.getString("supplier_name"),
                    rs.getString("telephone"), rs.getString("address"), rs.getDouble("debt_supplier"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}