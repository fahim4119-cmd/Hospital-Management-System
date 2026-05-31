package hospital.ui;

import hospital.db.AppointmentDAO;
import hospital.models.Appointment;
import hospital.ui.dialogs.AppointmentDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private AppointmentDAO appointmentDAO;
    private JTextField searchField;
    private List<Appointment> appointments;

    public AppointmentsPanel() {
        appointmentDAO = new AppointmentDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.BACKGROUND);

        JPanel topBar = UITheme.createCard(null);
        topBar.setLayout(new BorderLayout(18, 0));
        topBar.add(UITheme.createSectionHeader("Appointments", "Schedule visits and track their status."), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, 38));
        JButton searchBtn = UITheme.createPrimaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(92, 38));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(searchBtn);

        JButton addBtn = UITheme.createSuccessButton("+ Schedule");
        addBtn.setPreferredSize(new Dimension(126, 38));
        searchPanel.add(Box.createHorizontalStrut(12));
        searchPanel.add(addBtn);

        topBar.add(searchPanel, BorderLayout.EAST);

        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);

        // Color status column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                if (!isSel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                if (col == 5 && val != null) {
                    String status = val.toString();
                    if (!isSel) {
                        if (status.equalsIgnoreCase("Confirmed")) setForeground(new Color(39, 174, 96));
                        else if (status.equalsIgnoreCase("Pending")) setForeground(new Color(243, 156, 18));
                        else if (status.equalsIgnoreCase("Cancelled")) setForeground(new Color(192, 57, 43));
                        setFont(UITheme.FONT_BUTTON);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(UITheme.BACKGROUND);
        JButton editBtn = UITheme.createPrimaryButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton refreshBtn = UITheme.createAccentButton("Refresh");
        editBtn.setPreferredSize(new Dimension(100, 34));
        deleteBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        btnRow.add(editBtn);
        btnRow.add(deleteBtn);
        btnRow.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnRow, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            AppointmentDialog dialog = new AppointmentDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (appointmentDAO.addAppointment(dialog.getAppointment())) {
                    JOptionPane.showMessageDialog(this, "Appointment scheduled!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an appointment to edit."); return; }
            Appointment appt = appointments.get(row);
            AppointmentDialog dialog = new AppointmentDialog((JFrame) SwingUtilities.getWindowAncestor(this), appt);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (appointmentDAO.updateAppointment(dialog.getAppointment())) {
                    JOptionPane.showMessageDialog(this, "Appointment updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an appointment to delete."); return; }
            Appointment appt = appointments.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete this appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION && appointmentDAO.deleteAppointment(appt.getId())) {
                JOptionPane.showMessageDialog(this, "Appointment deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) { loadData(); return; }
            appointments = appointmentDAO.searchAppointments(keyword);
            populateTable(appointments);
        });

        refreshBtn.addActionListener(e -> { searchField.setText(""); loadData(); });
    }

    private void loadData() {
        appointments = appointmentDAO.getAllAppointments();
        populateTable(appointments);
    }

    private void populateTable(List<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getId(), a.getPatientName(), a.getDoctorName(),
                a.getAppointmentDate(), a.getAppointmentTime(),
                a.getStatus(), a.getNotes()
            });
        }
    }
}
