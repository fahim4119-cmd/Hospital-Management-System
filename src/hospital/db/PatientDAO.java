package hospital.db;

import hospital.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO extends BaseDAO<Patient> {

    static {
        ensurePatientSchema();
    }

    @Override
    public List<Patient> getAll() { return getAllPatients(); }

    @Override
    public List<Patient> search(String keyword) { return searchPatients(keyword); }

    @Override
    public boolean add(Patient entity) { return addPatient(entity); }

    @Override
    public boolean update(Patient entity) { return updatePatient(entity); }

    @Override
    public boolean delete(int id) { return deletePatient(id); }

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

    private static void ensurePatientSchema() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, "patients", "photo_path")) {
                if (!rs.next()) {
                    try (Statement st = conn.createStatement()) {
                        st.executeUpdate("ALTER TABLE patients ADD COLUMN photo_path VARCHAR(300)");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Patient schema migration skipped: " + e.getMessage());
        }
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
        String sql = "INSERT INTO patients (name, age, gender, blood_group, phone, address, disease, photo_path) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getBloodGroup());
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());
            ps.setString(7, patient.getDisease());
            ps.setString(8, patient.getPhotoPath());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("patients");
            return ok;
        } catch (SQLException e) {
            System.err.println("Add patient error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name=?, age=?, gender=?, blood_group=?, phone=?, address=?, disease=?, photo_path=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getBloodGroup());
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());
            ps.setString(7, patient.getDisease());
            ps.setString(8, patient.getPhotoPath());
            ps.setInt(9, patient.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("patients");
            return ok;
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
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("patients");
            return ok;
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
            rs.getString("disease"),
            rs.getString("photo_path")
        );
    }

    public int getVisitCount(int patientId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE patient_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public String getLastAppointmentDate(int patientId) {
        String sql = "SELECT MAX(appointment_date) FROM appointments WHERE patient_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "-";
    }
}
