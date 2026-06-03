package hospital.ui;

import hospital.db.PatientDAO;
import hospital.db.RoomDAO;
import hospital.models.Patient;
import hospital.models.Room;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoomsPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Room No", "Type", "Floor", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Room> rooms = new ArrayList<>();
    private JPanel metricsPanel;
    private JPanel chartsPanel;

    public RoomsPanel() {
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
        JLabel title = new JLabel("Rooms & Wards");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Allocate general beds, private rooms, and ICU units for incoming patients.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UITheme.createSuccessButton("+ Add Room");
        addBtn.setPreferredSize(new Dimension(120, 36));
        actions.add(addBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        addBtn.addActionListener(e -> editRoom(null));
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(240, 253, 244)); // Light Green
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Room allocation status is dynamically updated when patient bed assignments are modified.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(22, 163, 74));
        JLabel details = new JLabel("WARD ADMISSIONS");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(22, 163, 74));
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
        JLabel title = new JLabel("Room Inventory");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        JButton editBtn = UITheme.createPrimaryButton("Edit");
        JButton deleteBtn = UITheme.createDangerButton("Delete");
        JButton assignBtn = UITheme.createRoundedButton("Assign Patient", UITheme.APPOINTMENT_PURPLE);
        editBtn.setPreferredSize(new Dimension(76, 34));
        deleteBtn.setPreferredSize(new Dimension(82, 34));
        assignBtn.setPreferredSize(new Dimension(140, 34));

        searchActions.add(editBtn);
        searchActions.add(deleteBtn);
        searchActions.add(assignBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                if (!sel) {
                    String status = String.valueOf(tbl.getValueAt(row, 4));
                    setBackground("Available".equalsIgnoreCase(status) ? new Color(220, 252, 231) : new Color(254, 226, 226));
                    setForeground(UITheme.TEXT_PRIMARY);
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

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) editRoom(rooms.get(row));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Delete selected room?") == JOptionPane.YES_OPTION) {
                roomDAO.delete(rooms.get(row).getId());
                load();
            }
        });
        assignBtn.addActionListener(e -> assignPatient());

        return card;
    }

    private void editRoom(Room room) {
        JTextField number = UITheme.createTextField();
        JComboBox<String> type = new JComboBox<>(new String[]{"General", "ICU", "Private"});
        JTextField floor = UITheme.createTextField();
        JComboBox<String> status = new JComboBox<>(new String[]{"Available", "Occupied"});
        if (room != null) {
            number.setText(room.getRoomNumber());
            type.setSelectedItem(room.getRoomType());
            floor.setText(String.valueOf(room.getFloor()));
            status.setSelectedItem(room.getStatus());
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Room Number")); form.add(number);
        form.add(new JLabel("Type")); form.add(type);
        form.add(new JLabel("Floor")); form.add(floor);
        form.add(new JLabel("Status")); form.add(status);
        if (JOptionPane.showConfirmDialog(this, form, room == null ? "Add Room" : "Edit Room", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Room r = room == null ? new Room() : room;
                r.setRoomNumber(number.getText().trim());
                r.setRoomType(String.valueOf(type.getSelectedItem()));
                r.setFloor(Integer.parseInt(floor.getText().trim()));
                r.setStatus(String.valueOf(status.getSelectedItem()));
                if (room == null) roomDAO.add(r); else roomDAO.update(r);
                load();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Floor must be a number.");
            }
        }
    }

    private void assignPatient() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an available room.");
            return;
        }
        Room room = rooms.get(row);
        if (!"Available".equalsIgnoreCase(room.getStatus())) {
            JOptionPane.showMessageDialog(this, "This room is already occupied.");
            return;
        }
        JComboBox<Patient> patients = new JComboBox<>();
        for (Patient p : patientDAO.getAllPatients()) patients.addItem(p);
        JTextField notes = UITheme.createTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Patient")); form.add(patients);
        form.add(new JLabel("Notes")); form.add(notes);
        if (JOptionPane.showConfirmDialog(this, form, "Assign Patient", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Patient p = (Patient) patients.getSelectedItem();
            if (p != null && roomDAO.assignPatient(p.getId(), room.getId(), notes.getText().trim())) load();
        }
    }

    private void load() {
        rooms = roomDAO.getAll();
        model.setRowCount(0);
        for (Room r : rooms) model.addRow(new Object[]{r.getId(), r.getRoomNumber(), r.getRoomType(), r.getFloor(), r.getStatus()});
        refreshStats();
    }

    private void refreshStats() {
        metricsPanel.removeAll();
        int total = rooms == null ? 0 : rooms.size();
        int occupied = 0;
        int available = 0;
        if (rooms != null) {
            for (Room r : rooms) {
                if ("Available".equalsIgnoreCase(r.getStatus())) available++;
                else occupied++;
            }
        }
        double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0.0;

        metricsPanel.add(metricCard("Total Wards/Rooms", String.valueOf(total), "+0.5%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Occupied Beds", String.valueOf(occupied), "+2.4%", UITheme.DANGER));
        metricsPanel.add(metricCard("Available Beds", String.valueOf(available), "-1.8%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Occupancy Rate", String.format("%.1f%%", occupancyRate), "+1.2%", UITheme.APPOINTMENT_PURPLE));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Room Bed Occupancy", new RoomOccupancyChart(rooms)));
        chartsPanel.add(chartCard("Room Type Distributions", new RoomTypeChart(rooms)));

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

    // --- Room Occupancy Donut Chart ---
    private static class RoomOccupancyChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        RoomOccupancyChart(List<Room> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            counts.put("Available", 0);
            counts.put("Occupied", 0);
            if (list != null) {
                for (Room r : list) {
                    String status = r.getStatus();
                    if ("Available".equalsIgnoreCase(status)) counts.put("Available", counts.get("Available") + 1);
                    else counts.put("Occupied", counts.get("Occupied") + 1);
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

            Color[] colors = {new Color(34, 197, 94), new Color(239, 68, 68)};
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

    // --- Room Type Bar Chart ---
    private static class RoomTypeChart extends JPanel {
        private final Map<String, Integer> counts = new LinkedHashMap<>();

        RoomTypeChart(List<Room> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            counts.put("General", 0);
            counts.put("ICU", 0);
            counts.put("Private", 0);
            if (list != null) {
                for (Room r : list) {
                    String t = r.getRoomType();
                    counts.put(t, counts.getOrDefault(t, 0) + 1);
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
                g2.setColor(new Color(34, 197, 94));
                g2.fillRoundRect(x, y, barW, h, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(entry.getKey(), x + 2, getHeight() - 12);
                g2.drawString(String.valueOf(entry.getValue()), x + barW / 2 - 4, y - 4);
                i++;
            }
            g2.dispose();
        }
    }
}
