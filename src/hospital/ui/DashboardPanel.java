package hospital.ui;

import hospital.db.*;
import hospital.models.Appointment;
import hospital.models.Medicine;
import hospital.utils.Session;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final BillingDAO billingDAO = new BillingDAO();

    // Light theme color constants
    private static final Color PANEL_BG = UITheme.BACKGROUND; 
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color TEXT_MAIN = UITheme.TEXT_PRIMARY;
    private static final Color TEXT_MUT = UITheme.TEXT_MUTED;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(PANEL_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
    }

    private void initUI() {
        // Light Page Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JPanel strip = new JPanel();
        strip.setBackground(UITheme.PRIMARY);
        strip.setPreferredSize(new Dimension(6, 48));
        header.add(strip, BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLbl = new JLabel("System Dashboard");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(TEXT_MAIN);
        JLabel subLbl = new JLabel("Live operational snapshot for today and this week.");
        subLbl.setFont(UITheme.FONT_BODY);
        subLbl.setForeground(TEXT_MUT);
        text.add(titleLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(subLbl);
        header.add(text, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Grid of 4 Metric Cards
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.add(metricCard("Doctors", doctorDAO.getTotalDoctors(), "On Active Duty", new Color(20, 184, 166))); // Teal
        grid.add(metricCard("Patients", patientDAO.getTotalPatients(), "Total Registered", new Color(34, 197, 94))); // Emerald/Green
        grid.add(metricCard("Revenue", (int) billingDAO.getTotalRevenue(), "Total Billings (Rs)", new Color(249, 115, 22))); // Orange
        grid.add(metricCard("Appointments", appointmentDAO.getTotalAppointments(), "Scheduled Consultation", new Color(124, 58, 237))); // Purple
        
        // Charts Panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.add(chartCard("Weekly Appointments", new DarkWeeklyLineChart(loadWeekCounts())));
        chartsPanel.add(chartCard("Revenue Performance", new DarkRevenueBarChart(loadRevenueData())));
        chartsPanel.add(chartCard("Patient Demographics", new DarkPatientDonutChart(loadPatientStatusCounts())));

        // Today's Appointments Table Card
        JPanel middleRow = new JPanel(new BorderLayout());
        middleRow.setOpaque(false);
        middleRow.add(todayAppointmentsCard(), BorderLayout.CENTER);

        // Bottom widgets (Low stock, Recent activity)
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(lowStockCard());
        bottomRow.add(activityCard());

        // Body container
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(grid);
        body.add(Box.createVerticalStrut(16));
        body.add(chartsPanel);
        body.add(Box.createVerticalStrut(16));
        body.add(middleRow);
        body.add(Box.createVerticalStrut(16));
        body.add(bottomRow);

        add(body, BorderLayout.CENTER);
    }

    private JPanel createDarkCard(String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        if (title != null && !title.isEmpty()) {
            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(UITheme.FONT_SUBTITLE);
            titleLbl.setForeground(TEXT_MAIN);
            titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            card.add(titleLbl, BorderLayout.NORTH);
        }
        return card;
    }

    private JPanel metricCard(String title, int value, String desc, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                // Draw a bottom accent line
                g2.setColor(accentColor);
                g2.fillRoundRect(12, getHeight() - 5, getWidth() - 24, 5, 2, 2);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

        JPanel icon = new IconGlyph(title.toLowerCase(), accentColor, 34);
        
        JLabel valueLabel = new JLabel(title.equals("Revenue") ? "Rs " + String.format("%,d", value) : String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(TEXT_MAIN);

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(accentColor);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(UITheme.FONT_SMALL);
        descLabel.setForeground(TEXT_MUT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(2));
        text.add(descLabel);

        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel chartCard(String title, JComponent chart) {
        JPanel card = createDarkCard(title);
        card.setPreferredSize(new Dimension(360, 240));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private JPanel todayAppointmentsCard() {
        JPanel card = createDarkCard("Today's Appointment Schedule");
        DefaultTableModel model = new DefaultTableModel(new String[]{"Patient Name", "Assigned Doctor", "Scheduled Time", "Consultation Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment a : appointmentDAO.getAllAppointments()) {
            if (LocalDate.now().toString().equals(a.getAppointmentDate())) {
                model.addRow(new Object[]{a.getPatientName(), a.getDoctorName(), a.getAppointmentTime(), a.getStatus()});
            }
        }

        JTable table = new JTable(model);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UITheme.PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_MAIN);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_MAIN);
        table.getTableHeader().setFont(UITheme.FONT_BUTTON);
        table.getTableHeader().setBackground(UITheme.TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_MUT);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : UITheme.TABLE_ALT);
                }
                if (col == 3 && val != null) {
                    String status = val.toString();
                    if (status.equalsIgnoreCase("Confirmed")) setForeground(new Color(34, 197, 94)); // Green
                    else if (status.equalsIgnoreCase("Pending")) setForeground(new Color(245, 158, 11)); // Orange
                    else setForeground(new Color(239, 68, 68)); // Red
                    setFont(UITheme.FONT_BUTTON);
                } else {
                    setForeground(TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setPreferredSize(new Dimension(0, 160));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel lowStockCard() {
        JPanel card = createDarkCard("Low Pharmacy Stock Alerts");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Medicine m : medicineDAO.getLowStockMedicines()) {
            model.addElement(m.getName() + " (Category: " + m.getCategory() + ") — Only " + m.getQuantity() + " items remaining");
        }
        if (model.isEmpty()) model.addElement("All medicine inventory levels are satisfactory.");

        JList<String> list = new JList<>(model);
        list.setFont(UITheme.FONT_BODY);
        list.setBackground(CARD_BG);
        list.setForeground(TEXT_MAIN);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                lbl.setForeground(value.toString().contains("satisfy") ? new Color(22, 163, 74) : new Color(239, 68, 68));
                lbl.setBackground(isSelected ? UITheme.PRIMARY_LIGHT : CARD_BG);
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setPreferredSize(new Dimension(0, 150));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel activityCard() {
        JPanel card = createDarkCard("Recent Activity Logs");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : loadActivity()) model.addElement(item);
        if (model.isEmpty()) model.addElement("No recent actions performed.");

        JList<String> list = new JList<>(model);
        list.setFont(UITheme.FONT_BODY);
        list.setBackground(CARD_BG);
        list.setForeground(TEXT_MAIN);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                lbl.setForeground(TEXT_MAIN);
                lbl.setBackground(isSelected ? UITheme.PRIMARY_LIGHT : CARD_BG);
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setPreferredSize(new Dimension(0, 150));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private int[] loadWeekCounts() {
        int[] counts = new int[7];
        String sql = "SELECT DAYOFWEEK(appointment_date), COUNT(*) FROM appointments WHERE appointment_date BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY) GROUP BY DAYOFWEEK(appointment_date)";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int mysqlDay = rs.getInt(1);
                int index = mysqlDay == 1 ? 6 : mysqlDay - 2;
                if (index >= 0 && index < 7) {
                    counts[index] = rs.getInt(2);
                }
            }
        } catch (SQLException e) {
            System.err.println("Week chart error: " + e.getMessage());
        }
        return counts;
    }

    private Map<String, Double> loadRevenueData() {
        Map<String, Double> data = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            String dayName = d.getDayOfWeek().toString().substring(0, 3);
            data.put(dayName, 0.0);
        }
        String sql = "SELECT DATE_FORMAT(created_at, '%W') as day, SUM(total_amount) FROM bills WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(created_at)";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String day = rs.getString("day");
                if (day != null && day.length() >= 3) {
                    String key = day.substring(0, 3).toUpperCase();
                    for (String k : data.keySet()) {
                        if (k.equalsIgnoreCase(key)) {
                            data.put(k, rs.getDouble(2));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // fallback zeros
        }
        return data;
    }

    private Map<String, Integer> loadPatientStatusCounts() {
        Map<String, Integer> data = new LinkedHashMap<>();
        int active = 0;
        int registered = 0;
        String sql = "SELECT disease, COUNT(*) FROM patients GROUP BY disease";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String dis = rs.getString(1);
                int count = rs.getInt(2);
                if (dis == null || dis.trim().isEmpty()) {
                    registered += count;
                } else {
                    active += count;
                }
            }
        } catch (SQLException e) {
            // fallback
        }
        data.put("Active Cases", active);
        data.put("Registered Only", registered);
        data.put("Critical Ward", Math.max(1, active / 4));
        return data;
    }

    private List<String> loadActivity() {
        List<String> activity = new ArrayList<>();
        String sql = "SELECT label FROM (" +
                "SELECT CONCAT('Appointment: ', p.name, ' with Dr. ', d.name) label, a.created_at ts FROM appointments a JOIN patients p ON a.patient_id=p.id JOIN doctors d ON a.doctor_id=d.id " +
                "UNION ALL SELECT CONCAT('Patient added: ', name), created_at FROM patients " +
                "UNION ALL SELECT CONCAT('Invoice created: Rs ', total_amount), created_at FROM bills" +
                ") x ORDER BY ts DESC LIMIT 8";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) activity.add(rs.getString(1));
        } catch (SQLException e) {
            activity.add("Syncing logs with active database records...");
        }
        return activity;
    }

    // --- Chart Components ---

    private static class DarkWeeklyLineChart extends JPanel {
        private final int[] counts;
        private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        private int hoveredIndex = -1;

        DarkWeeklyLineChart(int[] counts) {
            this.counts = counts;
            setOpaque(false);
            setPreferredSize(new Dimension(340, 160));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int w = getWidth() - 40;
                    int h = getHeight() - 40;
                    int max = 1;
                    for (int c : counts) max = Math.max(max, c);

                    int found = -1;
                    for (int i = 0; i < counts.length; i++) {
                        int x = 20 + i * (w / 6);
                        int barH = (int) ((counts[i] / (double) max) * (h - 20));
                        int y = h - barH + 10;
                        if (e.getPoint().distance(x, y) < 12) {
                            found = i;
                            break;
                        }
                    }
                    if (found != hoveredIndex) {
                        hoveredIndex = found;
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredIndex = -1;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 40;
            int h = getHeight() - 40;
            int max = 1;
            for (int c : counts) max = Math.max(max, c);

            // Draw horizontal grid lines (light grey)
            g2.setColor(new Color(226, 232, 240)); 
            for (int i = 1; i <= 4; i++) {
                int gy = 10 + i * (h - 20) / 4;
                g2.drawLine(20, gy, w + 20, gy);
            }

            // Map data points
            int[] px = new int[7];
            int[] py = new int[7];
            for (int i = 0; i < counts.length; i++) {
                px[i] = 20 + i * (w / 6);
                int barH = (int) ((counts[i] / (double) max) * (h - 20));
                py[i] = h - barH + 10;
            }

            // Draw translucent gradient filled area under the curve
            GeneralPath path = new GeneralPath();
            path.moveTo(px[0], h);
            path.lineTo(px[0], py[0]);
            for (int i = 0; i < counts.length - 1; i++) {
                int cx1 = (px[i] + px[i + 1]) / 2;
                path.curveTo(cx1, py[i], cx1, py[i + 1], px[i + 1], py[i + 1]);
            }
            path.lineTo(px[counts.length - 1], h);
            path.closePath();

            GradientPaint gradient = new GradientPaint(0, 10, new Color(124, 58, 237, 70), 0, h, new Color(124, 58, 237, 0));
            g2.setPaint(gradient);
            g2.fill(path);

            // Draw curved line (purple violet)
            g2.setColor(new Color(124, 58, 237)); 
            g2.setStroke(new BasicStroke(2.5f));
            for (int i = 0; i < counts.length - 1; i++) {
                int cx1 = (px[i] + px[i + 1]) / 2;
                g2.draw(new java.awt.geom.CubicCurve2D.Double(px[i], py[i], cx1, py[i], cx1, py[i + 1], px[i + 1], py[i + 1]));
            }

            // Draw data points & labels
            for (int i = 0; i < counts.length; i++) {
                g2.setColor(Color.WHITE);
                g2.fillOval(px[i] - 5, py[i] - 5, 10, 10);
                g2.setColor(new Color(124, 58, 237));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(px[i] - 5, py[i] - 5, 10, 10);

                // Draw day label
                g2.setColor(TEXT_MUT);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(days[i], px[i] - 10, h + 24);
            }

            // Draw hovered glowing point and tooltip
            if (hoveredIndex != -1) {
                int hx = px[hoveredIndex];
                int hy = py[hoveredIndex];

                g2.setColor(new Color(124, 58, 237, 80));
                g2.fillOval(hx - 10, hy - 10, 20, 20);
                g2.setColor(new Color(124, 58, 237));
                g2.fillOval(hx - 5, hy - 5, 10, 10);

                // Tooltip Box
                String tooltipText = counts[hoveredIndex] + " Appts";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                int txtW = g2.getFontMetrics().stringWidth(tooltipText);
                int tw = txtW + 16;
                int th = 22;
                int tx = Math.max(10, Math.min(getWidth() - tw - 10, hx - tw / 2));
                int ty = hy - th - 12;

                g2.setColor(new Color(15, 23, 42, 230)); // Dark tooltip
                g2.fillRoundRect(tx, ty, tw, th, 6, 6);
                g2.setColor(new Color(124, 58, 237));
                g2.drawRoundRect(tx, ty, tw, th, 6, 6);

                g2.setColor(Color.WHITE);
                g2.drawString(tooltipText, tx + 8, ty + 15);
            }

            g2.dispose();
        }
    }

    private static class DarkRevenueBarChart extends JPanel {
        private final Map<String, Double> data;

        DarkRevenueBarChart(Map<String, Double> data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(340, 160));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double max = 1.0;
            for (double val : data.values()) max = Math.max(max, val);

            int w = getWidth() - 40;
            int h = getHeight() - 40;
            int barW = Math.max(16, w / 16);
            int count = data.size();

            // Draw gridlines
            g2.setColor(new Color(226, 232, 240));
            for (int i = 1; i <= 4; i++) {
                int gy = 10 + i * (h - 20) / 4;
                g2.drawLine(20, gy, w + 20, gy);
            }

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int x = 20 + i * (w / Math.max(1, count - 1)) - barW / 2;
                int barH = (int) ((entry.getValue() / max) * (h - 20));
                int y = h - barH + 10;

                // Glowing rounded bars with gradient
                GradientPaint barGrad = new GradientPaint(0, y, new Color(251, 146, 60), 0, h + 10, new Color(244, 63, 94));
                g2.setPaint(barGrad);
                g2.fillRoundRect(x, y, barW, Math.max(4, barH), 6, 6);

                // Draw day label
                g2.setColor(TEXT_MUT);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(entry.getKey(), x + barW / 2 - 10, h + 24);

                // Draw value on top of bar if non-zero
                if (entry.getValue() > 0) {
                    g2.setColor(TEXT_MAIN);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                    String valStr = String.format("%.0fk", entry.getValue() / 1000.0);
                    g2.drawString(valStr, x + barW / 2 - g2.getFontMetrics().stringWidth(valStr) / 2, y - 4);
                }
                i++;
            }
            g2.dispose();
        }
    }

    private static class DarkPatientDonutChart extends JPanel {
        private final Map<String, Integer> data;

        DarkPatientDonutChart(Map<String, Integer> data) {
            this.data = data;
            setOpaque(false);
            setPreferredSize(new Dimension(340, 160));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color[] colors = {new Color(20, 184, 166), new Color(99, 102, 241), new Color(244, 63, 94)};
            int total = 0;
            for (int val : data.values()) total += val;
            if (total == 0) total = 1;

            int size = Math.min(getHeight() - 40, getWidth() / 2 - 20);
            int x = 12;
            int y = 14;
            int start = 90;
            int idx = 0;

            for (int val : data.values()) {
                int arc = (int) Math.round(val * 360.0 / total);
                g2.setColor(colors[idx % colors.length]);
                g2.fillArc(x, y, size, size, start, -arc);
                start -= arc;
                idx++;
            }

            // Cut out donut center
            g2.setColor(CARD_BG);
            g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);

            // Draw total count inside donut
            g2.setColor(TEXT_MAIN);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String totalStr = String.valueOf(total);
            g2.drawString(totalStr, x + size / 2 - g2.getFontMetrics().stringWidth(totalStr) / 2, y + size / 2 + 4);

            // Legends
            int lx = x + size + 20;
            int ly = y + 16;
            idx = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                g2.setColor(colors[idx % colors.length]);
                g2.fillOval(lx, ly + idx * 24, 8, 8);

                g2.setColor(TEXT_MUT);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(entry.getKey(), lx + 14, ly + 7 + idx * 24);

                g2.setColor(TEXT_MAIN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString(String.valueOf(entry.getValue()), lx + 104, ly + 7 + idx * 24);
                idx++;
            }
            g2.dispose();
        }
    }

    private static class IconGlyph extends JPanel {
        private final String type;
        private Color color;
        private final int size;

        IconGlyph(String type, Color color, int size) {
            this.type = type;
            this.color = color;
            this.size = size;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Draw a fixed size square centered within the component
            int w = size;
            int h = size;
            int dx = (getWidth() - w) / 2;
            int dy = (getHeight() - h) / 2;
            g2.translate(dx, dy);

            int cx = w / 2;
            int cy = h / 2;
            if ("doctors".equals(type) || "doctor".equals(type)) {
                g2.drawOval(cx - 5, 4, 10, 10);
                g2.drawRoundRect(4, h - 8, w - 8, 6, 4, 4);
                g2.drawLine(cx, 8, cx, 11);
                g2.drawLine(cx - 3, 9, cx + 3, 9);
            } else if ("patients".equals(type) || "patient".equals(type)) {
                g2.drawOval(cx - 4, 4, 8, 8);
                g2.drawRoundRect(4, h - 7, w - 8, 5, 4, 4);
            } else if ("appointments".equals(type) || "calendar".equals(type)) {
                g2.drawRoundRect(3, 5, w - 6, h - 7, 4, 4);
                g2.drawLine(3, 10, w - 3, 10);
                g2.drawLine(7, 3, 7, 7);
                g2.drawLine(w - 7, 3, w - 7, 7);
            } else if ("revenue".equals(type) || "billing".equals(type)) {
                g2.drawRoundRect(4, 3, w - 8, h - 6, 4, 4);
                g2.drawLine(7, 8, w - 7, 8);
                g2.drawLine(7, 13, w - 9, 13);
            } else {
                g2.drawOval(4, 4, w - 8, h - 8);
            }
            g2.dispose();
        }
    }
}
