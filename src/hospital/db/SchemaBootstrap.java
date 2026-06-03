package hospital.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchemaBootstrap {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private SchemaBootstrap() {}

    public static void ensureSchema() {
        if (INITIALIZED.get()) return;
        synchronized (SchemaBootstrap.class) {
            if (INITIALIZED.get()) return;
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) return;
                try (Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS bills (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "patient_id INT NOT NULL," +
                        "doctor_id INT NOT NULL," +
                        "total_amount DECIMAL(10,2) DEFAULT 0.00," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS bill_items (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "bill_id INT NOT NULL," +
                        "medicine_id INT NOT NULL," +
                        "quantity INT NOT NULL," +
                        "unit_price DECIMAL(10,2) NOT NULL," +
                        "subtotal DECIMAL(10,2) NOT NULL," +
                        "FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "room_number VARCHAR(20) UNIQUE NOT NULL," +
                        "room_type VARCHAR(20) NOT NULL," +
                        "floor INT DEFAULT 1," +
                        "status VARCHAR(20) DEFAULT 'Available'" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS admissions (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "patient_id INT NOT NULL," +
                        "room_id INT NOT NULL," +
                        "admission_date DATE NOT NULL," +
                        "discharge_date DATE," +
                        "notes TEXT," +
                        "FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE" +
                        ")");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS staff (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "name VARCHAR(100) NOT NULL," +
                        "role VARCHAR(40)," +
                        "department VARCHAR(80)," +
                        "phone VARCHAR(20)," +
                        "shift VARCHAR(20)," +
                        "salary DECIMAL(10,2) DEFAULT 0.00" +
                        ")");
                INITIALIZED.set(true);
                }
            } catch (SQLException e) {
                System.err.println("Schema bootstrap skipped: " + e.getMessage());
            }
        }
    }
}
