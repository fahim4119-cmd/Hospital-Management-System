package hospital.ui.dialogs;

import hospital.db.AppointmentDAO;
import hospital.db.BillingDAO;
import hospital.db.DBConnection;
import hospital.models.Appointment;
import hospital.models.Bill;
import hospital.models.Patient;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class PatientHistoryDialog extends JDialog {
    private final Patient patient;

    public PatientHistoryDialog(JFrame parent, Patient patient) {
        super(parent, "Patient History", true);
        this.patient = patient;
        setSize(820, 560);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PATIENT_GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel(patient.getName() + " - Medical History");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Appointments", appointmentsTable());
        tabs.addTab("Bills", billsTable());
        tabs.addTab("Diagnoses", diagnosesPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane appointmentsTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Time", "Doctor", "Status", "Notes"}, 0);
        List<Appointment> appointments = new AppointmentDAO().getAllAppointments();
        for (Appointment a : appointments) {
            if (a.getPatientId() == patient.getId()) {
                model.addRow(new Object[]{a.getAppointmentDate(), a.getAppointmentTime(), a.getDoctorName(), a.getStatus(), a.getNotes()});
            }
        }
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        return new JScrollPane(table);
    }

    private JScrollPane billsTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Invoice", "Doctor", "Total", "Created"}, 0);
        List<Bill> bills = new BillingDAO().getAll();
        for (Bill b : bills) {
            if (b.getPatientId() == patient.getId()) {
                model.addRow(new Object[]{b.getId(), b.getDoctorName(), String.format("%.2f", b.getTotalAmount()), b.getCreatedAt()});
            }
        }
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        return new JScrollPane(table);
    }

    private JScrollPane diagnosesPanel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Diagnosis / Notes"}, 0);
        String sql = "SELECT appointment_date, notes FROM appointments WHERE patient_id=? AND (notes IS NOT NULL OR status='Completed') ORDER BY appointment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patient.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException e) {
            model.addRow(new Object[]{"-", "Unable to load diagnoses: " + e.getMessage()});
        }
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        return new JScrollPane(table);
    }
}
