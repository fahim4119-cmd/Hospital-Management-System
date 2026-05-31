package hospital.ui;

import hospital.db.DoctorDAO;
import hospital.models.Doctor;
import hospital.ui.dialogs.DoctorDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField searchField;
    private List<Doctor> doctors;

    public DoctorsPanel() {
        doctorDAO = new DoctorDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(UITheme.BACKGROUND);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(UITheme.BACKGROUND);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(UITheme.BACKGROUND);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name or specialization...");
        JButton searchBtn = UITheme.createPrimaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(90, 36));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(searchBtn);

        JButton addBtn = UITheme.createSuccessButton("+ Add Doctor");
        addBtn.setPreferredSize(new Dimension(130, 36));

        topBar.add(searchPanel, BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Name", "Specialization", "Phone", "Email", "Qualification", "Gender", "Exp (yrs)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(70);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(UITheme.BACKGROUND);
        JButton editBtn = UITheme.createPrimaryButton("✏ Edit");
        JButton deleteBtn = UITheme.createDangerButton("🗑 Delete");
        JButton refreshBtn = UITheme.createSuccessButton("↺ Refresh");
        editBtn.setPreferredSize(new Dimension(100, 34));
        deleteBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        btnRow.add(editBtn);
        btnRow.add(deleteBtn);
        btnRow.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnRow, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(e -> {
            DoctorDialog dialog = new DoctorDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (doctorDAO.addDoctor(dialog.getDoctor())) {
                    JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a doctor to edit."); return; }
            Doctor doctor = doctors.get(row);
            DoctorDialog dialog = new DoctorDialog((JFrame) SwingUtilities.getWindowAncestor(this), doctor);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (doctorDAO.updateDoctor(dialog.getDoctor())) {
                    JOptionPane.showMessageDialog(this, "Doctor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a doctor to delete."); return; }
            Doctor doctor = doctors.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete doctor: " + doctor.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                if (doctorDAO.deleteDoctor(doctor.getId())) {
                    JOptionPane.showMessageDialog(this, "Doctor deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) { loadData(); return; }
            doctors = doctorDAO.searchDoctors(keyword);
            populateTable(doctors);
        });

        refreshBtn.addActionListener(e -> { searchField.setText(""); loadData(); });
    }

    private void loadData() {
        doctors = doctorDAO.getAllDoctors();
        populateTable(doctors);
    }

    private void populateTable(List<Doctor> list) {
        tableModel.setRowCount(0);
        for (Doctor d : list) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getSpecialization(), d.getPhone(),
                d.getEmail(), d.getQualification(), d.getGender(), d.getExperience()
            });
        }
    }
}
