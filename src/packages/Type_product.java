package packages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import packages.DataBase.DatabaseConnection;

public class Type_product {

    private int    id;
    private String nom;

    public Type_product(int id, String nom) { this.id = id; this.nom = nom; }
    public Type_product(String nom)         { this.nom = nom; }

    public int    getId()            { return id; }
    public String getNom()           { return nom; }
    public void   setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return String.format("  [%d] %s", id, nom);
    }

    // ── DB ────────────────────────────────────────────────────────────────────

    public boolean save() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO type_product (nom) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nom);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) this.id = rs.getInt(1);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE type_product SET nom=? WHERE id=?")) {
            ps.setString(1, nom); ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM type_product WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public static List<Type_product> findAll() {
        List<Type_product> list = new ArrayList<>();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM type_product ORDER BY id")) {
            while (rs.next())
                list.add(new Type_product(rs.getInt("id"), rs.getString("nom")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Type_product findById(int id) {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "SELECT * FROM type_product WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Type_product(rs.getInt("id"), rs.getString("nom"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}