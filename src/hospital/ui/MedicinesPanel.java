package hospital.ui;

import hospital.db.MedicineDAO;
import hospital.models.Medicine;
import hospital.ui.dialogs.MedicineDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class MedicinesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private MedicineDAO medicineDAO;
    private JTextField searchField;
    private JPanel metricsPanel;
    private JPanel chartsPanel;
    private List<Medicine> medicines;

    public MedicinesPanel() {
        medicineDAO = new MedicineDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BACKGROUND);

        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setOpaque(false);
        page.add(createToolbar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(createNotice());
        body.add(Box.createVerticalStrut(12));

        metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setOpaque(false);
        body.add(metricsPanel);
        body.add(Box.createVerticalStrut(12));

        chartsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsPanel.setOpaque(false);
        body.add(chartsPanel);
        body.add(Box.createVerticalStrut(12));

        body.add(createTableCard());
        page.add(body, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Medicines & Pharmacy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Monitor medical supply, safety stocks, unit prices, and batch expiries.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UITheme.createSuccessButton("+ Add Medicine");
        addBtn.setPreferredSize(new Dimension(140, 36));
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

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
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(255, 247, 237)); // Light Orange
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 215, 170)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Medicines nearing expiry (within 30 days) are highlighted in red, low stock in orange.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(194, 65, 12));
        JLabel details = new JLabel("PHARMACY ALERTS");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(194, 65, 12));
        notice.add(text, BorderLayout.WEST);
        notice.add(details, BorderLayout.EAST);
        return notice;
    }

    private JPanel createTableCard() {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Medicine Inventory");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(240, 34));
        JButton searchBtn = UITheme.createRoundedButton("Search", Color.WHITE);
        searchBtn.setForeground(UITheme.TEXT_PRIMARY);
        searchBtn.setPreferredSize(new Dimension(82, 34));
        JButton editBtn = UITheme.createPrimaryButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton refreshBtn = UITheme.createSuccessButton("Refresh");
        JButton stockBtn = UITheme.createRoundedButton("Adjust Stock", UITheme.MEDICINE_ORANGE);
        editBtn.setPreferredSize(new Dimension(76, 34));
        deleteBtn.setPreferredSize(new Dimension(82, 34));
        refreshBtn.setPreferredSize(new Dimension(88, 34));
        stockBtn.setPreferredSize(new Dimension(125, 34));

        searchActions.add(searchField);
        searchActions.add(searchBtn);
        searchActions.add(editBtn);
        searchActions.add(deleteBtn);
        searchActions.add(refreshBtn);
        searchActions.add(stockBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Category", "Manufacturer", "Price (Rs)", "Quantity", "Expiry Date", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    try {
                        int quantity = Integer.parseInt(String.valueOf(tbl.getValueAt(row, 5)).replace(" LOW", ""));
                        String expiry = String.valueOf(tbl.getValueAt(row, 6));
                        long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(expiry));
                        if (days <= 30) setBackground(new Color(254, 226, 226)); // Red alert
                        else if (quantity < 10) setBackground(new Color(255, 247, 237)); // Orange alert
                    } catch (Exception ignored) {}
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchMedicines());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a medicine to edit.");
                return;
            }
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
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
                return;
            }
            Medicine med = medicines.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete medicine: " + med.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION && medicineDAO.deleteMedicine(med.getId())) {
                JOptionPane.showMessageDialog(this, "Medicine deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        });
        stockBtn.addActionListener(e -> adjustStock());

        return card;
    }

    private void searchMedicines() {
        String keyword = searchField.getText().trim();
        medicines = keyword.isEmpty() ? medicineDAO.getAllMedicines() : medicineDAO.searchMedicines(keyword);
        refreshStats();
        populateTable(medicines);
    }

    private void loadData() {
        medicines = medicineDAO.getAllMedicines();
        refreshStats();
        populateTable(medicines);
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int total = medicines == null ? 0 : medicines.size();
        int totalQty = 0;
        int lowStock = 0;
        int nearExpiry = 0;
        if (medicines != null) {
            for (Medicine m : medicines) {
                totalQty += m.getQuantity();
                if (m.getQuantity() < 10) lowStock++;
                try {
                    long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(m.getExpiryDate()));
                    if (days <= 30) nearExpiry++;
                } catch (Exception ignored) {}
            }
        }

        metricsPanel.add(metricCard("Total Catalog", String.valueOf(total), "+1.2%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Items In Stock", String.valueOf(totalQty), "+4.5%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Low Stock Alerts", String.valueOf(lowStock), "-0.8%", UITheme.MEDICINE_ORANGE));
        metricsPanel.add(metricCard("Expiring Batch", String.valueOf(nearExpiry), "+0.2%", UITheme.DANGER));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Inventory Stock Levels", new StockStatusChart(medicines)));
        chartsPanel.add(chartCard("Medicine Category Breakdown", new CategoryBreakdownChart(medicines)));

        metricsPanel.revalidate();
        metricsPanel.repaint();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private JPanel metricCard(String label, String value, String trend, Color color) {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 6));
        JLabel title = new JLabel(label);
        title.setFont(UITheme.FONT_SMALL);
        title.setForeground(UITheme.TEXT_MUTED);
        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 24));
        number.setForeground(UITheme.TEXT_PRIMARY);
        JLabel trendLabel = new JLabel(trend + " since last month");
        trendLabel.setFont(UITheme.FONT_SMALL);
        trendLabel.setForeground(color);
        card.add(title, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        card.add(trendLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel chartCard(String title, JComponent chart) {
        JPanel card = UITheme.createCard(title);
        card.setPreferredSize(new Dimension(360, 220));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private void populateTable(List<Medicine> list) {
        tableModel.setRowCount(0);
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getName(), m.getCategory(), m.getManufacturer(),
                    String.format("%.2f", m.getPrice()), m.getQuantity() < 10 ? m.getQuantity() + " LOW" : m.getQuantity(),
                    m.getExpiryDate(), m.getDescription()
            });
        }
    }

    private void adjustStock() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine first.");
            return;
        }
        Medicine med = medicines.get(row);
        JSpinner delta = new JSpinner(new SpinnerNumberModel(1, -999, 999, 1));
        if (JOptionPane.showConfirmDialog(this, delta, "Stock adjustment for " + med.getName(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            medicineDAO.adjustStock(med.getId(), (Integer) delta.getValue());
            loadData();
        }
    }

    // --- Stock Status Donut Chart ---
    private static class StockStatusChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        StockStatusChart(List<Medicine> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            counts.put("In Stock", 0);
            counts.put("Low Stock", 0);
            counts.put("Out of Stock", 0);
            if (list != null) {
                for (Medicine m : list) {
                    if (m.getQuantity() == 0) counts.put("Out of Stock", counts.get("Out of Stock") + 1);
                    else if (m.getQuantity() < 10) counts.put("Low Stock", counts.get("Low Stock") + 1);
                    else counts.put("In Stock", counts.get("In Stock") + 1);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int total = 0;
            for (int v : counts.values()) total += v;
            if (total == 0) total = 1;

            Color[] colors = {new Color(34, 197, 94), new Color(245, 158, 11), new Color(239, 68, 68)};
            int size = Math.min(getHeight() - 28, getWidth() / 2 - 20);
            int x = 24;
            int y = 20;
            int start = 90;
            int idx = 0;
            for (int val : counts.values()) {
                int arc = (int) Math.round(val * 360.0 / total);
                g2.setColor(colors[idx % colors.length]);
                g2.fillArc(x, y, size, size, start, -arc);
                start -= arc;
                idx++;
            }
            g2.setColor(Color.WHITE);
            g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);

            int lx = x + size + 24;
            int ly = y + 10;
            idx = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                g2.setColor(colors[idx % colors.length]);
                g2.fillOval(lx, ly + idx * 24, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(entry.getKey(), lx + 16, ly + 8 + idx * 24);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(String.valueOf(entry.getValue()), lx + 120, ly + 8 + idx * 24);
                idx++;
            }
            g2.dispose();
        }
    }

    // --- Category Breakdown Bar Chart ---
    private static class CategoryBreakdownChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        CategoryBreakdownChart(List<Medicine> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (list != null) {
                for (Medicine m : list) {
                    String cat = m.getCategory();
                    if (cat == null || cat.trim().isEmpty()) cat = "Other";
                    counts.put(cat, counts.getOrDefault(cat, 0) + 1);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = 1;
            for (int v : counts.values()) max = Math.max(max, v);
            int chartH = getHeight() - 44;
            int count = counts.size();
            if (count == 0) count = 1;
            int gap = Math.max(8, getWidth() / (count * 4));
            int barW = Math.max(20, (getWidth() - gap * (count + 1)) / count);

            int i = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                int h = (int) ((entry.getValue() / (double) max) * (chartH - 18));
                int x = gap + i * (barW + gap);
                int y = chartH - h + 8;
                g2.setColor(new Color(249, 115, 22));
                g2.fillRoundRect(x, y, barW, h, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                String name = entry.getKey();
                if (name.length() > 6) name = name.substring(0, 5) + ".";
                g2.drawString(name, x, getHeight() - 12);
                g2.drawString(String.valueOf(entry.getValue()), x + barW / 2 - 4, y - 4);
                i++;
            }
            g2.dispose();
        }
    }
}
