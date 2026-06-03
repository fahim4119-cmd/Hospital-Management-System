package hospital.ui;

import hospital.db.StaffDAO;
import hospital.models.Staff;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class StaffPanel extends JPanel {
    private final StaffDAO staffDAO = new StaffDAO();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Role", "Department", "Phone", "Shift", "Salary"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Staff> staff = new ArrayList<>();
    private JPanel metricsPanel;
    private JPanel chartsPanel;

    public StaffPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BACKGROUND);
        initUI();
        load();
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
        JLabel title = new JLabel("Staff Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Manage clinical support teams, shifts, payroll, and phone records.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UITheme.createSuccessButton("+ Add Staff");
        addBtn.setPreferredSize(new Dimension(120, 36));
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        addBtn.addActionListener(e -> editStaff(null));
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(240, 253, 250)); // Light Teal
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 246, 228)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Staff directory displays nursing specialists, front-desk receptionists, and laboratory technicians.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(13, 148, 136));
        JLabel details = new JLabel("HUMAN RESOURCES");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(13, 148, 136));
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
        JLabel title = new JLabel("Hospital Staff Members");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        JButton editBtn = UITheme.createPrimaryButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        editBtn.setPreferredSize(new Dimension(76, 34));
        deleteBtn.setPreferredSize(new Dimension(82, 34));

        searchActions.add(editBtn);
        searchActions.add(deleteBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) editStaff(staff.get(row));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Delete selected staff member?") == JOptionPane.YES_OPTION) {
                staffDAO.delete(staff.get(row).getId());
                load();
            }
        });

        return card;
    }

    private void editStaff(Staff existing) {
        JTextField name = UITheme.createTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"Nurse", "Receptionist", "Lab Technician"});
        JTextField department = UITheme.createTextField();
        JTextField phone = UITheme.createTextField();
        JComboBox<String> shift = new JComboBox<>(new String[]{"Morning", "Evening", "Night"});
        JTextField salary = UITheme.createTextField();
        if (existing != null) {
            name.setText(existing.getName());
            role.setSelectedItem(existing.getRole());
            department.setText(existing.getDepartment());
            phone.setText(existing.getPhone());
            shift.setSelectedItem(existing.getShift());
            salary.setText(String.valueOf(existing.getSalary()));
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Role")); form.add(role);
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Phone")); form.add(phone);
        form.add(new JLabel("Shift")); form.add(shift);
        form.add(new JLabel("Salary")); form.add(salary);
        if (JOptionPane.showConfirmDialog(this, form, existing == null ? "Add Staff" : "Edit Staff", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Staff s = existing == null ? new Staff() : existing;
                s.setName(name.getText().trim());
                s.setRole(String.valueOf(role.getSelectedItem()));
                s.setDepartment(department.getText().trim());
                s.setPhone(phone.getText().trim());
                s.setShift(String.valueOf(shift.getSelectedItem()));
                s.setSalary(Double.parseDouble(salary.getText().trim()));
                if (existing == null) staffDAO.add(s); else staffDAO.update(s);
                load();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a number.");
            }
        }
    }

    private void load() {
        staff = staffDAO.getAll();
        model.setRowCount(0);
        for (Staff s : staff) {
            model.addRow(new Object[]{
                    s.getId(), s.getName(), s.getRole(), s.getDepartment(),
                    s.getPhone(), s.getShift(), String.format("%.2f", s.getSalary())
            });
        }
        refreshStats();
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int total = staff == null ? 0 : staff.size();
        int nurses = 0;
        double sumSalary = 0;
        if (staff != null) {
            for (Staff s : staff) {
                if ("Nurse".equalsIgnoreCase(s.getRole())) nurses++;
                sumSalary += s.getSalary();
            }
        }
        double avgSalary = total > 0 ? (sumSalary / total) : 0.0;

        metricsPanel.add(metricCard("Total Staff", String.valueOf(total), "+1.2%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Nursing Team", String.valueOf(nurses), "+2.0%", UITheme.APPOINTMENT_PURPLE));
        metricsPanel.add(metricCard("Support Team", String.valueOf(total - nurses), "-0.5%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Avg Salary", "Rs " + String.format("%,.0f", avgSalary), "+1.5%", UITheme.MEDICINE_ORANGE));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Staff Shift Allocations", new ShiftChart(staff)));
        chartsPanel.add(chartCard("Clinical Support Roles", new RoleChart(staff)));

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

    // --- Shift Distribution Donut Chart ---
    private static class ShiftChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        ShiftChart(List<Staff> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            counts.put("Morning", 0);
            counts.put("Evening", 0);
            counts.put("Night", 0);
            if (list != null) {
                for (Staff s : list) {
                    String shift = s.getShift();
                    counts.put(shift, counts.getOrDefault(shift, 0) + 1);
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

            Color[] colors = {new Color(20, 184, 166), new Color(245, 158, 11), new Color(99, 102, 241)};
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

    // --- Staff Role Breakdown Bar Chart ---
    private static class RoleChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        RoleChart(List<Staff> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            counts.put("Nurse", 0);
            counts.put("Receptionist", 0);
            counts.put("Lab Technician", 0);
            if (list != null) {
                for (Staff s : list) {
                    String role = s.getRole();
                    counts.put(role, counts.getOrDefault(role, 0) + 1);
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
            int gap = Math.max(16, getWidth() / 14);
            int barW = Math.max(28, (getWidth() - gap * 4) / 3);

            int i = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                int h = (int) ((entry.getValue() / (double) max) * (chartH - 18));
                int x = gap + i * (barW + gap);
                int y = chartH - h + 8;
                g2.setColor(new Color(20, 184, 166));
                g2.fillRoundRect(x, y, barW, h, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                String label = entry.getKey();
                if (label.length() > 8) label = label.substring(0, 7) + ".";
                g2.drawString(label, x, getHeight() - 12);
                g2.drawString(String.valueOf(entry.getValue()), x + barW / 2 - 4, y - 4);
                i++;
            }
            g2.dispose();
        }
    }
}
