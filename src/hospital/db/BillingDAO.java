package hospital.db;

import hospital.models.Bill;
import hospital.models.BillItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO extends BaseDAO<Bill> {
    static {
        SchemaBootstrap.ensureSchema();
    }
    @Override
    public List<Bill> getAll() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, p.name patient_name, d.name doctor_name FROM bills b " +
                "JOIN patients p ON b.patient_id=p.id JOIN doctors d ON b.doctor_id=d.id ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapBill(rs));
        } catch (SQLException e) {
            System.err.println("Get bills error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Bill> search(String keyword) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT b.*, p.name patient_name, d.name doctor_name FROM bills b " +
                "JOIN patients p ON b.patient_id=p.id JOIN doctors d ON b.doctor_id=d.id " +
                "WHERE p.name LIKE ? OR d.name LIKE ? ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBill(rs));
        } catch (SQLException e) {
            System.err.println("Search bills error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean add(Bill bill) {
        String billSql = "INSERT INTO bills (patient_id, doctor_id, total_amount) VALUES (?,?,?)";
        String itemSql = "INSERT INTO bill_items (bill_id, medicine_id, quantity, unit_price, subtotal) VALUES (?,?,?,?,?)";
        String stockSql = "UPDATE medicines SET quantity = quantity - ? WHERE id=? AND quantity >= ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement billPs = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS)) {
                billPs.setInt(1, bill.getPatientId());
                billPs.setInt(2, bill.getDoctorId());
                billPs.setDouble(3, bill.getTotalAmount());
                billPs.executeUpdate();
                ResultSet keys = billPs.getGeneratedKeys();
                if (!keys.next()) throw new SQLException("Bill id was not generated.");
                int billId = keys.getInt(1);
                try (PreparedStatement itemPs = conn.prepareStatement(itemSql);
                     PreparedStatement stockPs = conn.prepareStatement(stockSql)) {
                    for (BillItem item : bill.getItems()) {
                        itemPs.setInt(1, billId);
                        itemPs.setInt(2, item.getMedicineId());
                        itemPs.setInt(3, item.getQuantity());
                        itemPs.setDouble(4, item.getUnitPrice());
                        itemPs.setDouble(5, item.getSubtotal());
                        itemPs.addBatch();

                        stockPs.setInt(1, item.getQuantity());
                        stockPs.setInt(2, item.getMedicineId());
                        stockPs.setInt(3, item.getQuantity());
                        stockPs.addBatch();
                    }
                    itemPs.executeBatch();
                    stockPs.executeBatch();
                }
            }
            conn.commit();
            notifyChanged("billing");
            return true;
        } catch (SQLException e) {
            System.err.println("Add bill error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) conn.rollback();
            } catch (SQLException ignored) {}
            return false;
        } finally {
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    @Override
    public boolean update(Bill entity) { return false; }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bills WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) notifyChanged("billing");
            return ok;
        } catch (SQLException e) {
            System.err.println("Delete bill error: " + e.getMessage());
            return false;
        }
    }

    public List<BillItem> getItems(int billId) {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT bi.*, m.name medicine_name FROM bill_items bi JOIN medicines m ON bi.medicine_id=m.id WHERE bi.bill_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BillItem(rs.getInt("id"), rs.getInt("bill_id"), rs.getInt("medicine_id"),
                        rs.getString("medicine_name"), rs.getInt("quantity"), rs.getDouble("unit_price"),
                        rs.getDouble("subtotal")));
            }
        } catch (SQLException e) {
            System.err.println("Get bill items error: " + e.getMessage());
        }
        return list;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM bills";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Bill mapBill(ResultSet rs) throws SQLException {
        return new Bill(rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                rs.getString("patient_name"), rs.getString("doctor_name"), rs.getDouble("total_amount"),
                rs.getString("created_at"));
    }
}
