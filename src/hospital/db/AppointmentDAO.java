package hospital.db;

import hospital.models.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) {
            System.err.println("Get appointments error: " + e.getMessage());
        }
        return list;
    }

    public List<Appointment> searchAppointments(String keyword) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE p.name LIKE ? OR d.name LIKE ? OR a.status LIKE ? " +
                     "ORDER BY a.appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) {
            System.err.println("Search appointments error: " + e.getMessage());
        }
        return list;
    }

    public boolean addAppointment(Appointment appt) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appt.getPatientId());
            ps.setInt(2, appt.getDoctorId());
            ps.setString(3, appt.getAppointmentDate());
            ps.setString(4, appt.getAppointmentTime());
            ps.setString(5, appt.getStatus());
            ps.setString(6, appt.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add appointment error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAppointment(Appointment appt) {
        String sql = "UPDATE appointments SET patient_id=?, doctor_id=?, appointment_date=?, appointment_time=?, status=?, notes=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appt.getPatientId());
            ps.setInt(2, appt.getDoctorId());
            ps.setString(3, appt.getAppointmentDate());
            ps.setString(4, appt.getAppointmentTime());
            ps.setString(5, appt.getStatus());
            ps.setString(6, appt.getNotes());
            ps.setInt(7, appt.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update appointment error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete appointment error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTodayAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getString("patient_name"),
            rs.getString("doctor_name"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getString("status"),
            rs.getString("notes")
        );
    }
}
