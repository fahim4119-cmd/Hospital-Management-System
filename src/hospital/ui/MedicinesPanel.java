package hospital.ui;

import hospital.db.MedicineDAO;
import hospital.models.Medicine;
import hospital.ui.dialogs.MedicineDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicinesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private MedicineDAO medicineDAO;
    private JTextField searchField;
    private List<Medicine> medicines;

    public MedicinesPanel() {
        medicineDAO = new MedicineDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(UITheme.BACKGROUND);

        JPanel topBar = UITheme.createCard(null);
        topBar.setLayout(new BorderLayout(18, 0));
        topBar.add(UITheme.createSectionHeader("Medicines", "Monitor inventory, prices, quantities, and expiry dates."), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(260, 38));
        JButton searchBtn = UITheme.createPrimaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(92, 38));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(8));
        searchPanel.add(searchBtn);

        JButton addBtn = UITheme.createSuccessButton("+ Add Medicine");
        addBtn.setPreferredSize(new Dimension(148, 38));
        searchPanel.add(Box.createHorizontalStrut(12));
        searchPanel.add(addBtn);

        topBar.add(searchPanel, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Category", "Manufacturer", "Price (Rs)", "Quantity", "Expiry Date", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);
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
            MedicineDialog dialog = new MedicineDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (medicineDAO.addMedicine(dialog.getMedicine())) {
                    JOptionPane.showMessageDialog(this, "Medicine added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a medicine to edit."); return; }
            Medicine med = medicines.get(row);
            MedicineDialog dialog = new MedicineDialog((JFrame) SwingUtilities.getWindowAncestor(this), med);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                if (medicineDAO.updateMedicine(dialog.getMedicine())) {
                    JOptionPane.showMessageDialog(this, "Medicine updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a medicine to delete."); return; }
            Medicine med = medicines.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete medicine: " + med.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION && medicineDAO.deleteMedicine(med.getId())) {
                JOptionPane.showMessageDialog(this, "Medicine deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) { loadData(); return; }
            medicines = medicineDAO.searchMedicines(keyword);
            populateTable(medicines);
        });

        refreshBtn.addActionListener(e -> { searchField.setText(""); loadData(); });
    }

    private void loadData() {
        medicines = medicineDAO.getAllMedicines();
        populateTable(medicines);
    }

    private void populateTable(List<Medicine> list) {
        tableModel.setRowCount(0);
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getName(), m.getCategory(), m.getManufacturer(),
                String.format("%.2f", m.getPrice()), m.getQuantity(),
                m.getExpiryDate(), m.getDescription()
            });
        }
    }
}
