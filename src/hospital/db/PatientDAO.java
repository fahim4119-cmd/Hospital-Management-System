package hospital.db;

import hospital.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapPatient(rs));
        } catch (SQLException e) {
            System.err.println("Get patients error: " + e.getMessage());
        }
        return list;
    }

    public List<Patient> searchPatients(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR disease LIKE ? OR phone LIKE ? ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPatient(rs));
        } catch (SQLException e) {
            System.err.println("Search patients error: " + e.getMessage());
        }
        return list;
    }

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, gender, blood_group, phone, address, disease) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getBloodGroup());
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());
            ps.setString(7, patient.getDisease());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add patient error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name=?, age=?, gender=?, blood_group=?, phone=?, address=?, disease=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getBloodGroup());
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());
            ps.setString(7, patient.getDisease());
            ps.setInt(8, patient.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update patient error: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete patient error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalPatients() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Patient mapPatient(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("blood_group"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("disease")
        );
    }
}
