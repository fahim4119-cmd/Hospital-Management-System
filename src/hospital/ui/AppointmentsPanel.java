package hospital.ui;

import hospital.db.AppointmentDAO;
import hospital.models.Appointment;
import hospital.ui.dialogs.AppointmentDialog;
import hospital.utils.Session;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppointmentsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AppointmentDAO appointmentDAO;
    private JTextField searchField;
    private JPanel metricsPanel;
    private JPanel chartsPanel;
    private List<Appointment> appointments;

    public AppointmentsPanel() {
        appointmentDAO = new AppointmentDAO();
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
        JLabel title = new JLabel("Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Schedule patient visits, set timetables, and monitor daily consultation flows.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UITheme.createSuccessButton("+ Schedule");
        addBtn.setPreferredSize(new Dimension(120, 36));
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

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
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(245, 243, 255)); // Light Purple
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 214, 254)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Patient appointments can be marked as completed directly from the actions column.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(109, 40, 217));
        JLabel details = new JLabel("LIVE CALENDAR");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(109, 40, 217));
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
        JLabel title = new JLabel("Appointment List");
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
        editBtn.setPreferredSize(new Dimension(76, 34));
        deleteBtn.setPreferredSize(new Dimension(82, 34));
        refreshBtn.setPreferredSize(new Dimension(88, 34));

        searchActions.add(searchField);
        searchActions.add(searchBtn);
        searchActions.add(editBtn);
        searchActions.add(deleteBtn);
        searchActions.add(refreshBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Notes", "Complete"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
                if (!isSel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    setForeground(UITheme.TEXT_PRIMARY);
                }
                if (col == 5 && val != null) {
                    String status = val.toString();
                    if (!isSel) {
                        if (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Completed")) setForeground(new Color(39, 174, 96));
                        else if (status.equalsIgnoreCase("Pending")) setForeground(new Color(243, 156, 18));
                        else if (status.equalsIgnoreCase("Cancelled")) setForeground(new Color(192, 57, 43));
                        setFont(UITheme.FONT_BUTTON);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchAppointments());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an appointment to edit.");
                return;
            }
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
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an appointment to delete.");
                return;
            }
            Appointment appt = appointments.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete this appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION && appointmentDAO.deleteAppointment(appt.getId())) {
                JOptionPane.showMessageDialog(this, "Appointment deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        });

        return card;
    }

    private void searchAppointments() {
        String keyword = searchField.getText().trim();
        appointments = keyword.isEmpty() ? appointmentDAO.getAllAppointments() : appointmentDAO.searchAppointments(keyword);
        applyDoctorFilter();
        refreshStats();
        populateTable(appointments);
    }

    private void loadData() {
        appointments = appointmentDAO.getAllAppointments();
        applyDoctorFilter();
        refreshStats();
        populateTable(appointments);
    }

    private void applyDoctorFilter() {
        if (!Session.getInstance().isDoctor()) return;
        String doctorName = Session.getInstance().getFullName();
        appointments.removeIf(a -> a.getDoctorName() == null || !a.getDoctorName().equalsIgnoreCase(doctorName));
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int total = appointments == null ? 0 : appointments.size();
        int pending = 0;
        int completed = 0;
        int cancelled = 0;
        if (appointments != null) {
            for (Appointment a : appointments) {
                if ("Pending".equalsIgnoreCase(a.getStatus())) pending++;
                else if ("Completed".equalsIgnoreCase(a.getStatus())) completed++;
                else if ("Cancelled".equalsIgnoreCase(a.getStatus())) cancelled++;
            }
        }

        metricsPanel.add(metricCard("Total Bookings", String.valueOf(total), "+3.4%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Pending Today", String.valueOf(pending), "-1.2%", UITheme.MEDICINE_ORANGE));
        metricsPanel.add(metricCard("Completed Visits", String.valueOf(completed), "+5.8%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Cancellations", String.valueOf(cancelled), "-0.5%", UITheme.DANGER));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Appointment Status Distribution", new StatusChart(appointments)));
        chartsPanel.add(chartCard("Time-Slot Popularity", new SlotChart(appointments)));

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

    private void populateTable(List<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(), a.getPatientName(), a.getDoctorName(),
                    a.getAppointmentDate(), a.getAppointmentTime(),
                    a.getStatus(), a.getNotes(), "Done"
            });
        }
    }

    // --- Action Button Column Renderer & Editor ---
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer() {
            setText("Done");
            setFont(UITheme.FONT_BUTTON);
            setForeground(Color.WHITE);
            setBackground(UITheme.ACCENT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button = UITheme.createSuccessButton("Done");
        private int row;

        ButtonEditor() {
            super(new JCheckBox());
            button.addActionListener(e -> {
                fireEditingStopped();
                if (row >= 0 && row < appointments.size()) {
                    Appointment appt = appointments.get(row);
                    if (!"Completed".equalsIgnoreCase(appt.getStatus()) && appointmentDAO.markCompleted(appt.getId())) {
                        loadData();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Done";
        }
    }

    // --- Status Chart ---
    private static class StatusChart extends JPanel {
        private final Map<String, Integer> statusCounts = new LinkedHashMap<>();

        StatusChart(List<Appointment> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            statusCounts.put("Confirmed", 0);
            statusCounts.put("Pending", 0);
            statusCounts.put("Cancelled", 0);
            statusCounts.put("Completed", 0);
            if (list != null) {
                for (Appointment a : list) {
                    String status = a.getStatus();
                    statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int total = 0;
            for (int v : statusCounts.values()) total += v;
            if (total == 0) total = 1;

            Color[] colors = {new Color(34, 197, 94), new Color(245, 158, 11), new Color(239, 68, 68), new Color(59, 130, 246)};
            int size = Math.min(getHeight() - 28, getWidth() / 2 - 20);
            int x = 24;
            int y = 20;
            int start = 90;
            int idx = 0;
            for (int val : statusCounts.values()) {
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
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
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

    // --- Slot Popularity Chart ---
    private static class SlotChart extends JPanel {
        private final int[] values = new int[3];
        private final String[] labels = {"Morning", "Afternoon", "Evening"};

        SlotChart(List<Appointment> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (list != null) {
                for (Appointment a : list) {
                    String time = a.getAppointmentTime();
                    if (time != null && time.contains(":")) {
                        try {
                            int hour = Integer.parseInt(time.split(":")[0]);
                            if (hour < 12) values[0]++;
                            else if (hour < 16) values[1]++;
                            else values[2]++;
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = 1;
            for (int v : values) max = Math.max(max, v);
            int chartH = getHeight() - 44;
            int gap = Math.max(16, getWidth() / 14);
            int barW = Math.max(28, (getWidth() - gap * 4) / 3);
            for (int i = 0; i < values.length; i++) {
                int h = (int) ((values[i] / (double) max) * (chartH - 18));
                int x = gap + i * (barW + gap);
                int y = chartH - h + 8;
                g2.setColor(new Color(124, 58, 237));
                g2.fillRoundRect(x, y, barW, h, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(labels[i], x + 2, getHeight() - 12);
                g2.drawString(String.valueOf(values[i]), x + barW / 2 - 4, y - 4);
            }
            g2.dispose();
        }
    }
}
