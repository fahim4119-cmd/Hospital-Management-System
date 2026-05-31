package hospital.ui;

import hospital.db.PatientDAO;
import hospital.models.Patient;
import hospital.ui.dialogs.PatientDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PatientDAO patientDAO;
    private JTextField searchField;
    private List<Patient> patients;

    public PatientsPanel() {
        patientDAO = new PatientDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.BACKGROUND);

        JPanel topBar = UITheme.createCard(null);
        topBar.setLayout(new BorderLayout(18, 0));
        topBar.add(UITheme.createSectionHeader("Patients", "Manage patient records and clinical notes."), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, 38));
        JButton searchBtn = UITheme.createPrimaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(92, 38));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(searchBtn);

        JButton addBtn = UITheme.createSuccessButton("+ Add Patient");
        addBtn.setPreferredSize(new Dimension(138, 38));
        searchPanel.add(Box.createHorizontalStrut(12));
        searchPanel.add(addBtn);

        topBar.add(searchPanel, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Age", "Gender", "Blood Group", "Phone", "Address", "Disease"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(150);
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
            PatientDialog dialog = new PatientDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (patientDAO.addPatient(dialog.getPatient())) {
                    JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a patient to edit."); return; }
            Patient patient = patients.get(row);
            PatientDialog dialog = new PatientDialog((JFrame) SwingUtilities.getWindowAncestor(this), patient);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (patientDAO.updatePatient(dialog.getPatient())) {
                    JOptionPane.showMessageDialog(this, "Patient updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a patient to delete."); return; }
            Patient p = patients.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete patient: " + p.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION && patientDAO.deletePatient(p.getId())) {
                JOptionPane.showMessageDialog(this, "Patient deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) { loadData(); return; }
            patients = patientDAO.searchPatients(keyword);
            populateTable(patients);
        });

        refreshBtn.addActionListener(e -> { searchField.setText(""); loadData(); });
    }

    private void loadData() {
        patients = patientDAO.getAllPatients();
        populateTable(patients);
    }

    private void populateTable(List<Patient> list) {
        tableModel.setRowCount(0);
        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getAge(), p.getGender(),
                p.getBloodGroup(), p.getPhone(), p.getAddress(), p.getDisease()
            });
        }
    }
}
