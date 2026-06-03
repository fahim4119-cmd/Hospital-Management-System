package hospital.ui;

import hospital.db.PatientDAO;
import hospital.models.Patient;
import hospital.ui.dialogs.PatientDialog;
import hospital.ui.dialogs.PatientHistoryDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatientsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private PatientDAO patientDAO;
    private JTextField searchField;
    private JLabel statsLabel;
    private JPanel metricsPanel;
    private JPanel chartsPanel;
    private List<Patient> patients;

    public PatientsPanel() {
        patientDAO = new PatientDAO();
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
        JLabel title = new JLabel("Patients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Manage patient enrollment, visits, and medical history.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton exportBtn = UITheme.createRoundedButton("Export", Color.WHITE);
        exportBtn.setForeground(UITheme.TEXT_PRIMARY);
        exportBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(7, 13, 7, 13)));
        JButton addBtn = UITheme.createSuccessButton("+ Create New");
        addBtn.setPreferredSize(new Dimension(132, 36));
        actions.add(exportBtn);
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        addBtn.addActionListener(e -> openPatientDialog(null));
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "CSV export is available from Reports."));
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(236, 253, 245));
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(94, 234, 212)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Patient records are synced with appointments, billing history, and dashboard activity.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(15, 118, 110));
        JLabel details = new JLabel("LIVE VIEW");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(15, 118, 110));
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
        JLabel title = new JLabel("Patient List");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(220, 34));
        JButton searchBtn = UITheme.createRoundedButton("Search", Color.WHITE);
        searchBtn.setForeground(UITheme.TEXT_PRIMARY);
        searchBtn.setPreferredSize(new Dimension(82, 34));
        JButton editBtn = UITheme.createPrimaryButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton refreshBtn = UITheme.createSuccessButton("Refresh");
        JButton historyBtn = UITheme.createRoundedButton("History", UITheme.APPOINTMENT_PURPLE);
        editBtn.setPreferredSize(new Dimension(76, 34));
        deleteBtn.setPreferredSize(new Dimension(82, 34));
        refreshBtn.setPreferredSize(new Dimension(88, 34));
        historyBtn.setPreferredSize(new Dimension(86, 34));
        searchActions.add(searchField);
        searchActions.add(searchBtn);
        searchActions.add(editBtn);
        searchActions.add(deleteBtn);
        searchActions.add(refreshBtn);
        searchActions.add(historyBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        String[] cols = {"Patient ID", "Patient Name", "Age", "Gender", "Phone", "Diagnosis", "Photo", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateStats());
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        statsLabel = new JLabel("Select a patient to view quick stats.");
        statsLabel.setFont(UITheme.FONT_BODY);
        statsLabel.setForeground(UITheme.TEXT_MUTED);
        footer.add(statsLabel, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchPatients());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
                return;
            }
            openPatientDialog(patients.get(row));
        });
        deleteBtn.addActionListener(e -> deleteSelectedPatient());
        historyBtn.addActionListener(e -> openHistory());
        return card;
    }

    private void openPatientDialog(Patient patient) {
        PatientDialog dialog = new PatientDialog((JFrame) SwingUtilities.getWindowAncestor(this), patient);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean saved = patient == null
                    ? patientDAO.addPatient(dialog.getPatient())
                    : patientDAO.updatePatient(dialog.getPatient());
            if (saved) {
                JOptionPane.showMessageDialog(this, patient == null ? "Patient added successfully." : "Patient updated successfully.");
                loadData();
            }
        }
    }

    private void deleteSelectedPatient() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            return;
        }
        Patient p = patients.get(row);
        int res = JOptionPane.showConfirmDialog(this, "Delete patient: " + p.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION && patientDAO.deletePatient(p.getId())) {
            JOptionPane.showMessageDialog(this, "Patient deleted.");
            loadData();
        }
    }

    private void openHistory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient.");
            return;
        }
        new PatientHistoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), patients.get(row)).setVisible(true);
    }

    private void searchPatients() {
        String keyword = searchField.getText().trim();
        patients = keyword.isEmpty() ? patientDAO.getAllPatients() : patientDAO.searchPatients(keyword);
        refreshDashboard();
        populateTable(patients);
    }

    private void loadData() {
        patients = patientDAO.getAllPatients();
        refreshDashboard();
        populateTable(patients);
    }

    private void refreshDashboard() {
        metricsPanel.removeAll();
        int total = patients == null ? 0 : patients.size();
        int visits = 0;
        int active = 0;
        int photos = 0;
        if (patients != null) {
            for (Patient p : patients) {
                visits += patientDAO.getVisitCount(p.getId());
                if (p.getDisease() != null && !p.getDisease().trim().isEmpty()) active++;
                if (p.getPhotoPath() != null && !p.getPhotoPath().trim().isEmpty()) photos++;
            }
        }
        metricsPanel.add(metricCard("Patient Enrollment", String.valueOf(total), "+18.7%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Patient Visits", String.valueOf(visits), "+5.2%", UITheme.APPOINTMENT_PURPLE));
        metricsPanel.add(metricCard("Active Cases", String.valueOf(active), "+6.0%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Profile Photos", String.valueOf(photos), "+2.4%", UITheme.MEDICINE_ORANGE));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Age Group Distribution", new AgeBarChart(patients)));
        chartsPanel.add(chartCard("Gender Coverage Distribution", new GenderDonutChart(patients)));

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

    private void populateTable(List<Patient> list) {
        tableModel.setRowCount(0);
        for (Patient p : list) {
            String status = p.getDisease() == null || p.getDisease().trim().isEmpty() ? "REGISTERED" : "IN TREATMENT";
            tableModel.addRow(new Object[]{
                    String.format("%05d", p.getId()),
                    p.getName(),
                    p.getAge(),
                    p.getGender(),
                    p.getPhone(),
                    p.getDisease(),
                    p.getPhotoPath(),
                    status
            });
        }
    }

    private void updateStats() {
        int row = table.getSelectedRow();
        if (row < 0 || patients == null || row >= patients.size()) return;
        Patient p = patients.get(row);
        statsLabel.setText("Visits: " + patientDAO.getVisitCount(p.getId()) + "   Last appointment: " + patientDAO.getLastAppointmentDate(p.getId()));
    }

    private static class AgeBarChart extends JPanel {
        private final int[] values = new int[4];
        private final String[] labels = {"0-18", "19-35", "36-55", "56+"};

        AgeBarChart(List<Patient> patients) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (patients != null) {
                for (Patient p : patients) {
                    int age = p.getAge();
                    if (age <= 18) values[0]++;
                    else if (age <= 35) values[1]++;
                    else if (age <= 55) values[2]++;
                    else values[3]++;
                }
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = 1;
            for (int value : values) max = Math.max(max, value);
            int chartH = getHeight() - 44;
            int gap = Math.max(16, getWidth() / 18);
            int barW = Math.max(28, (getWidth() - gap * 5) / 4);
            for (int i = 0; i < values.length; i++) {
                int h = (int) ((values[i] / (double) max) * (chartH - 18));
                int x = gap + i * (barW + gap);
                int y = chartH - h + 8;
                g2.setColor(new Color(56, 189, 248));
                g2.fillRoundRect(x, y, barW, h, 8, 8);
                g2.setColor(new Color(99, 102, 241, 180));
                g2.fillRoundRect(x + barW / 2, y + 8, barW / 2, Math.max(2, h - 8), 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(labels[i], x + 2, getHeight() - 12);
                g2.drawString(String.valueOf(values[i]), x + barW / 2 - 4, y - 4);
            }
            g2.dispose();
        }
    }

    private static class GenderDonutChart extends JPanel {
        private final Map<String, Integer> values = new LinkedHashMap<>();

        GenderDonutChart(List<Patient> patients) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            values.put("Female", 0);
            values.put("Male", 0);
            values.put("Other", 0);
            if (patients != null) {
                for (Patient p : patients) {
                    String gender = p.getGender() == null ? "Other" : p.getGender();
                    if (!values.containsKey(gender)) gender = "Other";
                    values.put(gender, values.get(gender) + 1);
                }
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color[] colors = {new Color(20, 184, 166), new Color(99, 102, 241), new Color(245, 158, 11)};
            int total = 0;
            for (int value : values.values()) total += value;
            if (total == 0) total = 1;
            int size = Math.min(getHeight() - 28, getWidth() / 2 - 20);
            int x = 24;
            int y = 20;
            int start = 90;
            int i = 0;
            for (int value : values.values()) {
                int arc = (int) Math.round(value * 360.0 / total);
                g2.setColor(colors[i % colors.length]);
                g2.fillArc(x, y, size, size, start, -arc);
                start -= arc;
                i++;
            }
            g2.setColor(Color.WHITE);
            g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);

            int lx = x + size + 34;
            int ly = y + 14;
            i = 0;
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                g2.setColor(colors[i % colors.length]);
                g2.fillOval(lx, ly + i * 28, 9, 9);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(entry.getKey(), lx + 16, ly + 8 + i * 28);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(String.valueOf(entry.getValue()), lx + 112, ly + 8 + i * 28);
                i++;
            }
            g2.dispose();
        }
    }
}
