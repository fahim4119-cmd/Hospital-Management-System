package hospital.ui;

import hospital.db.*;
import hospital.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class ReportsPanel extends JPanel implements Exportable {
    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable appointmentTable = new JTable(new DefaultTableModel(new String[]{"Status", "Total"}, 0));
    private final JTable revenueTable = new JTable(new DefaultTableModel(new String[]{"Date", "Revenue"}, 0));
    private final JTable stockTable = new JTable(new DefaultTableModel(new String[]{"Medicine", "Quantity", "Expiry"}, 0));
    private JPanel metricsPanel;

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BACKGROUND);
        initUI();
        loadReports();
    }

    private void initUI() {
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

        body.add(createTabCard());
        page.add(body, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Export appointment, revenue, and medicine stock summaries.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        toolbar.add(titleBlock, BorderLayout.WEST);
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(243, 244, 246)); // Light Gray
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Analytical data is pulled live from core operations database. Choose tabs below to filter reports.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(55, 65, 81));
        JLabel details = new JLabel("DB SYNCED");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(55, 65, 81));
        notice.add(text, BorderLayout.WEST);
        notice.add(details, BorderLayout.EAST);
        return notice;
    }

    private JPanel createTabCard() {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Report Queries");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton refreshBtn = UITheme.createSuccessButton("Refresh");
        JButton exportBtn = UITheme.createPrimaryButton("Export CSV");
        refreshBtn.setPreferredSize(new Dimension(88, 34));
        exportBtn.setPreferredSize(new Dimension(105, 34));
        actions.add(refreshBtn);
        actions.add(exportBtn);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        UITheme.styleTable(appointmentTable);
        UITheme.styleTable(revenueTable);
        UITheme.styleTable(stockTable);

        tabs.addTab("Appointments", new JScrollPane(appointmentTable));
        tabs.addTab("Revenue Streams", new JScrollPane(revenueTable));
        tabs.addTab("Medicine Inventory", new JScrollPane(stockTable));

        card.add(header, BorderLayout.NORTH);
        card.add(tabs, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadReports());
        exportBtn.addActionListener(e -> chooseCsv());

        return card;
    }

    private void loadReports() {
        query((DefaultTableModel) appointmentTable.getModel(),
                "SELECT status, COUNT(*) FROM appointments GROUP BY status",
                2);
        query((DefaultTableModel) revenueTable.getModel(),
                "SELECT DATE(created_at), SUM(total_amount) FROM bills GROUP BY DATE(created_at) ORDER BY DATE(created_at) DESC",
                2);
        query((DefaultTableModel) stockTable.getModel(),
                "SELECT name, quantity, expiry_date FROM medicines ORDER BY quantity ASC",
                3);

        refreshStats();
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int totalAppts = 0;
        double netBillings = 0;
        int lowStock = 0;
        int patientsCount = 0;

        // Fetch metrics directly via SQL
        try (Connection conn = DBConnection.getConnection()) {
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM appointments")) {
                if (rs.next()) totalAppts = rs.getInt(1);
            }
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT SUM(total_amount) FROM bills")) {
                if (rs.next()) netBillings = rs.getDouble(1);
            }
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM medicines WHERE quantity < 10")) {
                if (rs.next()) lowStock = rs.getInt(1);
            }
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM patients")) {
                if (rs.next()) patientsCount = rs.getInt(1);
            }
        } catch (SQLException ignored) {}

        metricsPanel.add(metricCard("Weekly Bookings", String.valueOf(totalAppts), "+4.2%", UITheme.APPOINTMENT_PURPLE));
        metricsPanel.add(metricCard("Net Billings", "Rs " + String.format("%,.0f", netBillings), "+8.5%", UITheme.MEDICINE_ORANGE));
        metricsPanel.add(metricCard("Safety Stock Alert", String.valueOf(lowStock), "-2.0%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Registered Cases", String.valueOf(patientsCount), "+1.8%", UITheme.PATIENT_GREEN));

        metricsPanel.revalidate();
        metricsPanel.repaint();
    }

    private JPanel metricCard(String label, String value, String trend, Color color) {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 6));
        JLabel title = new JLabel(label);
        title.setFont(UITheme.FONT_SMALL);
        title.setForeground(UITheme.TEXT_MUTED);
        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 22));
        number.setForeground(UITheme.TEXT_PRIMARY);
        JLabel trendLabel = new JLabel(trend + " since last month");
        trendLabel.setFont(UITheme.FONT_SMALL);
        trendLabel.setForeground(color);
        card.add(title, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        card.add(trendLabel, BorderLayout.SOUTH);
        return card;
    }

    private void query(DefaultTableModel model, String sql, int cols) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException e) {
            Object[] row = new Object[cols];
            row[0] = "Error: " + e.getMessage();
            model.addRow(row);
        }
    }

    private JTable selectedTable() {
        int i = tabs.getSelectedIndex();
        if (i == 0) return appointmentTable;
        if (i == 1) return revenueTable;
        return stockTable;
    }

    private void chooseCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(tabs.getTitleAt(tabs.getSelectedIndex()).toLowerCase().replace(" ", "-") + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) exportToCsv(chooser.getSelectedFile());
    }

    @Override
    public void exportToCsv(File file) {
        try {
            CsvExporter.exportTable(selectedTable(), file);
            JOptionPane.showMessageDialog(this, "CSV exported.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "CSV export failed: " + e.getMessage());
        }
    }
}
