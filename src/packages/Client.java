package packages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Client {

    private int    code;
    private String name;
    private String phone;
    private double debt;

    public Client(int code, String name, String phone, double debt) {
        this.code = code; this.name = name; this.phone = phone; this.debt = debt;
    }

    public Client(String name, String phone, double debt) {
        this.name = name; this.phone = phone; this.debt = debt;
    }

    public int    getCode()          { return code; }
    public String getName()          { return name; }
    public String getPhone()         { return phone; }
    public double getDebt()          { return debt; }
    public void   setName(String n)  { this.name  = n; }
    public void   setPhone(String p) { this.phone = p; }
    public void   setDebt(double d)  { this.debt  = d; }

    @Override
    public String toString() {
        return String.format("  [%d] %-25s | Phone: %-15s | Debt: %.2f Ar",
                code, name, phone, debt);
    }



    public boolean save() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO clients (name,phone,debt) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, phone); ps.setDouble(3, debt);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) this.code = rs.getInt(1);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE clients SET name=?,phone=?,debt=? WHERE code=?")) {
            ps.setString(1, name); ps.setString(2, phone);
            ps.setDouble(3, debt); ps.setInt(4, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM clients WHERE code=?")) {
            ps.setInt(1, code); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    
    public boolean payDebt(double amount) {
        if (amount <= 0) {
            System.out.println("  ✘ Amount must be positive.");
            return false;
        }
        if (amount > this.debt) {
            System.out.printf("  ✘ Amount (%.2f Ar) exceeds the current debt (%.2f Ar).%n", amount, this.debt);
            return false;
        }
        this.debt -= amount;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE clients SET debt=? WHERE code=?")) {
                ps.setDouble(1, this.debt);
                ps.setInt(2, this.code);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments (entity_type, entity_code, amount, payment_date) VALUES(?,?,?,NOW())")) {
                ps.setString(1, "CLIENT");
                ps.setInt(2, this.code);
                ps.setDouble(3, amount);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            this.debt += amount;
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static List<Client> findAll() {
        List<Client> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM clients ORDER BY code")) {
            while (rs.next())
                list.add(new Client(rs.getInt("code"), rs.getString("name"),
                        rs.getString("phone"), rs.getDouble("debt")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Client findByCode(int code) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT * FROM clients WHERE code=?")) {
            ps.setInt(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Client(rs.getInt("code"), rs.getString("name"),
                    rs.getString("phone"), rs.getDouble("debt"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}