package hospital.db;

import hospital.models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO extends BaseDAO<Medicine> {

    @Override
    public List<Medicine> getAll() { return getAllMedicines(); }

    @Override
    public List<Medicine> search(String keyword) { return searchMedicines(keyword); }

    @Override
    public boolean add(Medicine entity) { return addMedicine(entity); }

    @Override
    public boolean update(Medicine entity) { return updateMedicine(entity); }

    @Override
    public boolean delete(int id) { return deleteMedicine(id); }

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapMedicine(rs));
        } catch (SQLException e) {
            System.err.println("Get medicines error: " + e.getMessage());
        }
        return list;
    }

    public List<Medicine> searchMedicines(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ? OR category LIKE ? OR manufacturer LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMedicine(rs));
        } catch (SQLException e) {
            System.err.println("Search medicines error: " + e.getMessage());
        }
        return list;
    }

    public boolean addMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines (name, category, manufacturer, price, quantity, expiry_date, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicine.getName());
            ps.setString(2, medicine.getCategory());
            ps.setString(3, medicine.getManufacturer());
            ps.setDouble(4, medicine.getPrice());
            ps.setInt(5, medicine.getQuantity());
            ps.setString(6, medicine.getExpiryDate());
            ps.setString(7, medicine.getDescription());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("medicines");
            return ok;
        } catch (SQLException e) {
            System.err.println("Add medicine error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name=?, category=?, manufacturer=?, price=?, quantity=?, expiry_date=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicine.getName());
            ps.setString(2, medicine.getCategory());
            ps.setString(3, medicine.getManufacturer());
            ps.setDouble(4, medicine.getPrice());
            ps.setInt(5, medicine.getQuantity());
            ps.setString(6, medicine.getExpiryDate());
            ps.setString(7, medicine.getDescription());
            ps.setInt(8, medicine.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("medicines");
            return ok;
        } catch (SQLException e) {
            System.err.println("Update medicine error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("medicines");
            return ok;
        } catch (SQLException e) {
            System.err.println("Delete medicine error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalMedicines() {
        String sql = "SELECT COUNT(*) FROM medicines";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Medicine> getLowStockMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE quantity < 10 ORDER BY quantity ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapMedicine(rs));
        } catch (SQLException e) {
            System.err.println("Low stock error: " + e.getMessage());
        }
        return list;
    }

    public boolean adjustStock(int id, int delta) {
        String sql = "UPDATE medicines SET quantity = GREATEST(quantity + ?, 0) WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("medicines");
            return ok;
        } catch (SQLException e) {
            System.err.println("Adjust stock error: " + e.getMessage());
            return false;
        }
    }

    private Medicine mapMedicine(ResultSet rs) throws SQLException {
        return new Medicine(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("manufacturer"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getString("expiry_date"),
            rs.getString("description")
        );
    }
}
