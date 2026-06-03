package hospital.db;

import hospital.models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO extends BaseDAO<Room> {
    static {
        SchemaBootstrap.ensureSchema();
    }
    @Override
    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM rooms ORDER BY floor, room_number")) {
            while (rs.next()) list.add(mapRoom(rs));
        } catch (SQLException e) {
            System.err.println("Get rooms error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Room> search(String keyword) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ? OR room_type LIKE ? OR status LIKE ? ORDER BY floor, room_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRoom(rs));
        } catch (SQLException e) {
            System.err.println("Search rooms error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean add(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, floor, status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getStatus());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("rooms");
            return ok;
        } catch (SQLException e) {
            System.err.println("Add room error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, floor=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("rooms");
            return ok;
        } catch (SQLException e) {
            System.err.println("Update room error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE id=?")) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("rooms");
            return ok;
        } catch (SQLException e) {
            System.err.println("Delete room error: " + e.getMessage());
            return false;
        }
    }

    public boolean assignPatient(int patientId, int roomId, String notes) {
        String admissionSql = "INSERT INTO admissions (patient_id, room_id, admission_date, notes) VALUES (?,?,CURDATE(),?)";
        String roomSql = "UPDATE rooms SET status='Occupied' WHERE id=?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement aps = conn.prepareStatement(admissionSql);
                 PreparedStatement rps = conn.prepareStatement(roomSql)) {
                aps.setInt(1, patientId);
                aps.setInt(2, roomId);
                aps.setString(3, notes);
                aps.executeUpdate();
                rps.setInt(1, roomId);
                rps.executeUpdate();
            }
            conn.commit();
            notifyChanged("rooms");
            return true;
        } catch (SQLException e) {
            System.err.println("Assign room error: " + e.getMessage());
            try { DBConnection.getConnection().rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { DBConnection.getConnection().setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(rs.getInt("id"), rs.getString("room_number"), rs.getString("room_type"),
                rs.getInt("floor"), rs.getString("status"));
    }
}
