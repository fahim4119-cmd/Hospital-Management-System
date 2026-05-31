package hospital.db;

import hospital.models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get doctors error: " + e.getMessage());
        }
        return list;
    }

    public List<Doctor> searchDoctors(String keyword) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE name LIKE ? OR specialization LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapDoctor(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search doctors error: " + e.getMessage());
        }
        return list;
    }

    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization, phone, email, qualification, gender, experience) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            ps.setString(5, doctor.getQualification());
            ps.setString(6, doctor.getGender());
            ps.setInt(7, doctor.getExperience());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add doctor error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name=?, specialization=?, phone=?, email=?, qualification=?, gender=?, experience=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setString(4, doctor.getEmail());
            ps.setString(5, doctor.getQualification());
            ps.setString(6, doctor.getGender());
            ps.setInt(7, doctor.getExperience());
            ps.setInt(8, doctor.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update doctor error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete doctor error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalDoctors() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("specialization"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("qualification"),
            rs.getString("gender"),
            rs.getInt("experience")
        );
    }
}
