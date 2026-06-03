package hospital.db;

import hospital.models.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO extends BaseDAO<Staff> {
    static {
        SchemaBootstrap.ensureSchema();
    }
    @Override
    public List<Staff> getAll() {
        List<Staff> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM staff ORDER BY name")) {
            while (rs.next()) list.add(mapStaff(rs));
        } catch (SQLException e) {
            System.err.println("Get staff error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Staff> search(String keyword) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE name LIKE ? OR role LIKE ? OR department LIKE ? OR shift LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k); ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapStaff(rs));
        } catch (SQLException e) {
            System.err.println("Search staff error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean add(Staff s) {
        String sql = "INSERT INTO staff (name, role, department, phone, shift, salary) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, s);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("staff");
            return ok;
        } catch (SQLException e) {
            System.err.println("Add staff error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Staff s) {
        String sql = "UPDATE staff SET name=?, role=?, department=?, phone=?, shift=?, salary=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, s);
            ps.setInt(7, s.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("staff");
            return ok;
        } catch (SQLException e) {
            System.err.println("Update staff error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE id=?")) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("staff");
            return ok;
        } catch (SQLException e) {
            System.err.println("Delete staff error: " + e.getMessage());
            return false;
        }
    }

    private void fill(PreparedStatement ps, Staff s) throws SQLException {
        ps.setString(1, s.getName());
        ps.setString(2, s.getRole());
        ps.setString(3, s.getDepartment());
        ps.setString(4, s.getPhone());
        ps.setString(5, s.getShift());
        ps.setDouble(6, s.getSalary());
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        return new Staff(rs.getInt("id"), rs.getString("name"), rs.getString("role"),
                rs.getString("department"), rs.getString("phone"), rs.getString("shift"),
                rs.getDouble("salary"));
    }
}
