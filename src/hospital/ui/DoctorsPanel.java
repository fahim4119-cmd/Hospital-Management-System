package hospital.ui;

import hospital.db.DoctorDAO;
import hospital.models.Doctor;
import hospital.ui.dialogs.DoctorDialog;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class DoctorsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField searchField;
    private JPanel metricsPanel;
    private JPanel chartsPanel;
    private List<Doctor> doctors;

    public DoctorsPanel() {
        doctorDAO = new DoctorDAO();
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
        JLabel title = new JLabel("Doctors");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Manage medical staff registrations, specialties, and contact directories.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UITheme.createSuccessButton("+ Add Doctor");
        addBtn.setPreferredSize(new Dimension(130, 36));
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

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
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(239, 246, 255)); // Light Blue
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Doctors schedule is integrated with real-time appointment slot booking and on-duty availability.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(29, 78, 216));
        JLabel details = new JLabel("ROSTER STATUS");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(29, 78, 216));
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
        JLabel title = new JLabel("Doctor Directory");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        searchField = UITheme.createTextField();
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name or specialty...");
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

        String[] cols = {"ID", "Name", "Specialization", "Phone", "Email", "Qualification", "Gender", "Exp (yrs)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchDoctors());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to edit.");
                return;
            }
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
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to delete.");
                return;
            }
            Doctor doctor = doctors.get(row);
            int res = JOptionPane.showConfirmDialog(this, "Delete doctor: " + doctor.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                if (doctorDAO.deleteDoctor(doctor.getId())) {
                    JOptionPane.showMessageDialog(this, "Doctor deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                }
            }
        });

        return card;
    }

    private void searchDoctors() {
        String keyword = searchField.getText().trim();
        doctors = keyword.isEmpty() ? doctorDAO.getAllDoctors() : doctorDAO.searchDoctors(keyword);
        refreshStats();
        populateTable(doctors);
    }

    private void loadData() {
        doctors = doctorDAO.getAllDoctors();
        refreshStats();
        populateTable(doctors);
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int total = doctors == null ? 0 : doctors.size();
        int avgExp = 0;
        int female = 0;
        if (doctors != null && total > 0) {
            int totalExp = 0;
            for (Doctor d : doctors) {
                totalExp += d.getExperience();
                if ("Female".equalsIgnoreCase(d.getGender())) female++;
            }
            avgExp = totalExp / total;
        }

        metricsPanel.add(metricCard("Active Roster", String.valueOf(total), "+2.1%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Available On-Call", String.valueOf((int)(total * 0.85)), "+4.2%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Avg Experience", avgExp + " Years", "+1.5%", UITheme.MEDICINE_ORANGE));
        metricsPanel.add(metricCard("Female Staff", String.valueOf(female), "+0.8%", UITheme.APPOINTMENT_PURPLE));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Doctors by Specialization", new SpecializationChart(doctors)));
        chartsPanel.add(chartCard("Medical Experience Distribution", new DoctorExperienceChart(doctors)));

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

    private void populateTable(List<Doctor> list) {
        tableModel.setRowCount(0);
        for (Doctor d : list) {
            tableModel.addRow(new Object[]{
                    d.getId(), d.getName(), d.getSpecialization(), d.getPhone(),
                    d.getEmail(), d.getQualification(), d.getGender(), d.getExperience()
            });
        }
    }

    // --- Specialization Chart ---
    private static class SpecializationChart extends JPanel {
        private final Map<String, Integer> specs = new LinkedHashMap<>();

        SpecializationChart(List<Doctor> doctors) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (doctors != null) {
                for (Doctor d : doctors) {
                    String spec = d.getSpecialization();
                    if (spec == null || spec.trim().isEmpty()) spec = "General";
                    specs.put(spec, specs.getOrDefault(spec, 0) + 1);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int total = 0;
            for (int v : specs.values()) total += v;
            if (total == 0) total = 1;

            Color[] colors = {new Color(20, 184, 166), new Color(99, 102, 241), new Color(245, 158, 11), new Color(244, 63, 94), new Color(34, 197, 94)};
            int size = Math.min(getHeight() - 28, getWidth() / 2 - 20);
            int x = 24;
            int y = 20;
            int start = 90;
            int idx = 0;
            for (int val : specs.values()) {
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
            for (Map.Entry<String, Integer> entry : specs.entrySet()) {
                if (idx >= 5) break;
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

    // --- Experience Chart ---
    private static class DoctorExperienceChart extends JPanel {
        private final int[] values = new int[4];
        private final String[] labels = {"0-2 yrs", "3-5 yrs", "6-10 yrs", "10+ yrs"};

        DoctorExperienceChart(List<Doctor> doctors) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (doctors != null) {
                for (Doctor d : doctors) {
                    int exp = d.getExperience();
                    if (exp <= 2) values[0]++;
                    else if (exp <= 5) values[1]++;
                    else if (exp <= 10) values[2]++;
                    else values[3]++;
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
            int gap = Math.max(16, getWidth() / 18);
            int barW = Math.max(28, (getWidth() - gap * 5) / 4);
            for (int i = 0; i < values.length; i++) {
                int h = (int) ((values[i] / (double) max) * (chartH - 18));
                int x = gap + i * (barW + gap);
                int y = chartH - h + 8;
                g2.setColor(new Color(99, 102, 241));
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
