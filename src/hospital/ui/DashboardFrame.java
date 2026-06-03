package hospital.ui;

import hospital.db.*;
import hospital.models.Appointment;
import hospital.models.Medicine;
import hospital.utils.LoadingOverlay;
import hospital.utils.Session;
import hospital.utils.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class DashboardFrame extends JFrame implements DataChangeBus.DataChangeListener {
    private JPanel contentArea;
    private JLabel pageTitle;
    private JLabel notificationBadge;
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final BillingDAO billingDAO = new BillingDAO();

    public DashboardFrame() {
        initUI();
        DataChangeBus.getInstance().addListener(this);
    }

    private void initUI() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        setLayout(new BorderLayout());

        LoadingOverlay overlay = new LoadingOverlay();
        setGlassPane(overlay);

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.BACKGROUND);
        mainArea.add(createTopBar(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BACKGROUND);
        contentArea.setBorder(new EmptyBorder(22, 26, 22, 26));
        mainArea.add(contentArea, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        showHome();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 18));
        brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(250, 72));
        IconGlyph brandIcon = new IconGlyph("logo", UITheme.PRIMARY, 30);
        JLabel logo = new JLabel("HMS Care");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(UITheme.TEXT_PRIMARY);
        brand.add(brandIcon);
        brand.add(logo);
        sidebar.add(brand);

        addMenuLabel(sidebar, "MAIN MENU");
        addNav(sidebar, "dashboard", "dashboard", "Dashboard", () -> showHome(), true);
        if (Session.getInstance().isAdmin()) addNav(sidebar, "doctors", "doctor", "Doctors", () -> showModule("doctors"), true);
        if (!Session.getInstance().isDoctor()) addNav(sidebar, "patients", "patient", "Patients", () -> showModule("patients"), true);
        addNav(sidebar, "appointments", "calendar", "Appointments", () -> showModule("appointments"), true);
        if (Session.getInstance().isAdmin()) addNav(sidebar, "medicines", "medicine", "Medicines", () -> showModule("medicines"), true);
        if (!Session.getInstance().isDoctor()) addNav(sidebar, "billing", "billing", "Billing", () -> showModule("billing"), true);
        if (Session.getInstance().isAdmin()) addNav(sidebar, "rooms", "rooms", "Rooms & Wards", () -> showModule("rooms"), true);
        if (Session.getInstance().isAdmin()) addNav(sidebar, "staff", "staff", "Staff", () -> showModule("staff"), true);
        if (Session.getInstance().isAdmin()) addNav(sidebar, "reports", "reports", "Reports", () -> showModule("reports"), true);
        if (Session.getInstance().isDoctor()) addNav(sidebar, "patients", "patient", "Patient History", () -> showModule("patients"), true);

        sidebar.add(Box.createVerticalGlue());
        addMenuLabel(sidebar, "ACCOUNT");
        addNav(sidebar, "profile", "profile", "Profile", () -> showModule("profile"), true);
        addNav(sidebar, "logout", "logout", "Logout", () -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Session.getInstance().logout();
                new LoginFrame().setVisible(true);
                dispose();
            }
        }, false);
        sidebar.add(Box.createVerticalStrut(10));
        return sidebar;
    }

    private void addMenuLabel(JPanel sidebar, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setBorder(new EmptyBorder(14, 26, 6, 0));
        label.setMaximumSize(new Dimension(250, 32));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(label);
    }

    private void addNav(JPanel sidebar, String key, String icon, String label, Runnable action, boolean selectable) {
        NavItem item = new NavItem(icon, label, UITheme.PRIMARY, action, selectable);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(item);
        if (selectable) navItems.put(key, item);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 26, 0, 26)));
        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(pageTitle, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        right.setOpaque(false);
        JPanel bell = new JPanel(null);
        bell.setOpaque(false);
        bell.setPreferredSize(new Dimension(42, 38));
        IconGlyph bellIcon = new IconGlyph("bell", UITheme.TEXT_MUTED, 24);
        bellIcon.setBounds(4, 7, 24, 24);
        notificationBadge = new JLabel("", SwingConstants.CENTER);
        notificationBadge.setOpaque(true);
        notificationBadge.setBackground(UITheme.DANGER);
        notificationBadge.setForeground(Color.WHITE);
        notificationBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        notificationBadge.setBounds(20, 2, 20, 18);
        bell.add(bellIcon);
        bell.add(notificationBadge);
        updateBadge();

        JLabel user = new JLabel(Session.getInstance().getFullName() + "  |  " + Session.getInstance().getRole());
        user.setFont(UITheme.FONT_BODY);
        user.setForeground(UITheme.TEXT_MUTED);
        JLabel avatar = avatarLabel();
        right.add(bell);
        right.add(user);
        right.add(avatar);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JLabel avatarLabel() {
        String name = Session.getInstance().getFullName();
        String initials = "";
        for (String part : name.split(" ")) if (!part.isEmpty()) initials += part.charAt(0);
        if (initials.length() > 2) initials = initials.substring(0, 2);
        JLabel label = new JLabel(initials.toUpperCase(), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setPreferredSize(new Dimension(38, 38));
        label.setForeground(Color.WHITE);
        label.setFont(UITheme.FONT_BUTTON);
        return label;
    }

    private void showHome() {
        setActive("dashboard");
        pageTitle.setText("Dashboard");
        contentArea.removeAll();
        contentArea.add(new DashboardPanel(), BorderLayout.CENTER);
        refreshContent();
    }

    private JPanel metricCard(String title, int value, Color color) {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout());
        JPanel icon = new IconGlyph(title.toLowerCase(), color, 36);
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(color);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_BODY);
        titleLabel.setForeground(UITheme.TEXT_MUTED);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(8));
        text.add(valueLabel);
        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel chartCard() {
        JPanel card = UITheme.createCard("Appointments This Week");
        card.add(new WeeklyChart(loadWeekCounts()), BorderLayout.CENTER);
        return card;
    }

    private JPanel todayCard() {
        JPanel card = UITheme.createCard("Today's Appointments");
        DefaultTableModel model = new DefaultTableModel(new String[]{"Patient", "Doctor", "Time", "Status"}, 0);
        for (Appointment a : appointmentDAO.getAllAppointments()) {
            if (LocalDate.now().toString().equals(a.getAppointmentDate())) {
                model.addRow(new Object[]{a.getPatientName(), a.getDoctorName(), a.getAppointmentTime(), a.getStatus()});
            }
        }
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel lowStockCard() {
        JPanel card = UITheme.createCard("Low Stock Alerts");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Medicine m : medicineDAO.getLowStockMedicines()) model.addElement(m.getName() + " - " + m.getQuantity() + " left");
        if (model.isEmpty()) model.addElement("No low-stock medicines.");
        JList<String> list = new JList<>(model);
        list.setFont(UITheme.FONT_BODY);
        card.add(new JScrollPane(list), BorderLayout.CENTER);
        return card;
    }

    private JPanel activityCard() {
        JPanel card = UITheme.createCard("Recent Activity");
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : loadActivity()) model.addElement(item);
        JList<String> list = new JList<>(model);
        list.setFont(UITheme.FONT_BODY);
        card.add(new JScrollPane(list), BorderLayout.CENTER);
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
                counts[index] = rs.getInt(2);
            }
        } catch (SQLException e) {
            System.err.println("Week chart error: " + e.getMessage());
        }
        return counts;
    }

    private List<String> loadActivity() {
        List<String> activity = new ArrayList<>();
        String sql = "SELECT label FROM (" +
                "SELECT CONCAT('Appointment: ', p.name, ' with ', d.name) label, a.created_at ts FROM appointments a JOIN patients p ON a.patient_id=p.id JOIN doctors d ON a.doctor_id=d.id " +
                "UNION ALL SELECT CONCAT('Patient added: ', name), created_at FROM patients " +
                "UNION ALL SELECT CONCAT('Invoice created: Rs ', total_amount), created_at FROM bills" +
                ") x ORDER BY ts DESC LIMIT 8";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) activity.add(rs.getString(1));
        } catch (SQLException e) {
            activity.add("Activity feed will appear after database migration.");
        }
        return activity;
    }

    private void showModule(String module) {
        setActive(module);
        contentArea.removeAll();
        switch (module) {
            case "doctors": pageTitle.setText("Doctors"); contentArea.add(new DoctorsPanel()); break;
            case "patients": pageTitle.setText("Patients"); contentArea.add(new PatientsPanel()); break;
            case "appointments": pageTitle.setText("Appointments"); contentArea.add(new AppointmentsPanel()); break;
            case "medicines": pageTitle.setText("Medicines"); contentArea.add(new MedicinesPanel()); break;
            case "billing": pageTitle.setText("Billing"); contentArea.add(new BillingPanel()); break;
            case "rooms": pageTitle.setText("Rooms & Wards"); contentArea.add(new RoomsPanel()); break;
            case "staff": pageTitle.setText("Staff"); contentArea.add(new StaffPanel()); break;
            case "reports": pageTitle.setText("Reports"); contentArea.add(new ReportsPanel()); break;
            case "profile": pageTitle.setText("Profile"); contentArea.add(new ProfilePanel()); break;
            default: showHome(); return;
        }
        refreshContent();
    }

    private void setActive(String key) {
        for (Map.Entry<String, NavItem> entry : navItems.entrySet()) entry.getValue().setSelected(entry.getKey().equals(key));
    }

    private void updateBadge() {
        if (notificationBadge != null) notificationBadge.setText(String.valueOf(appointmentDAO.getTodayPendingAppointments()));
    }

    private void refreshContent() {
        contentArea.revalidate();
        contentArea.repaint();
        updateBadge();
    }

    @Override
    public void onDataChanged(String source) {
        SwingUtilities.invokeLater(() -> {
            updateBadge();
            if ("Dashboard".equals(pageTitle.getText())) showHome();
        });
    }

    @Override
    public void dispose() {
        DataChangeBus.getInstance().removeListener(this);
        super.dispose();
    }

    private static class WeeklyChart extends JPanel {
        private final int[] counts;
        private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        WeeklyChart(int[] counts) {
            this.counts = counts;
            setOpaque(false);
            setPreferredSize(new Dimension(420, 220));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = 1;
            for (int c : counts) max = Math.max(max, c);
            int w = getWidth() - 50;
            int h = getHeight() - 50;
            int barW = Math.max(18, w / 10);
            for (int i = 0; i < counts.length; i++) {
                int x = 28 + i * (w / 7);
                int barH = (int) ((counts[i] / (double) max) * (h - 20));
                g2.setColor(UITheme.APPOINTMENT_PURPLE);
                g2.fillRoundRect(x, h - barH + 10, barW, barH, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(days[i], x, h + 28);
                g2.drawString(String.valueOf(counts[i]), x + 4, h - barH + 2);
            }
            g2.dispose();
        }
    }

    private static class NavItem extends JPanel {
        private final JLabel label;
        private final Color accent;
        private final IconGlyph icon;
        private boolean selected;
        private Color bg = UITheme.SIDEBAR_BG;

        NavItem(String iconName, String text, Color accent, Runnable action, boolean selectable) {
            this.accent = accent;
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 16));
            setMaximumSize(new Dimension(250, 40));
            setPreferredSize(new Dimension(250, 40));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            this.icon = new IconGlyph(iconName, UITheme.SIDEBAR_TEXT, 18);
            label = new JLabel(text);
            label.setFont(UITheme.FONT_BODY);
            label.setForeground(UITheme.SIDEBAR_TEXT);
            add(this.icon, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { animateTo(new Color(241, 245, 249)); }
                @Override public void mouseExited(MouseEvent e) { animateTo(selected ? new Color(238, 242, 247) : UITheme.SIDEBAR_BG); }
                @Override public void mouseClicked(MouseEvent e) { action.run(); }
            });
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            bg = selected ? new Color(238, 242, 247) : UITheme.SIDEBAR_BG;
            label.setForeground(selected ? UITheme.TEXT_PRIMARY : UITheme.SIDEBAR_TEXT);
            icon.setColor(selected ? UITheme.PRIMARY : UITheme.SIDEBAR_TEXT);
            repaint();
        }

        private void animateTo(Color target) {
            javax.swing.Timer timer = new javax.swing.Timer(16, null);
            timer.addActionListener(e -> {
                bg = blend(bg, target, 0.25f);
                repaint();
                if (distance(bg, target) < 3) {
                    bg = target;
                    timer.stop();
                }
            });
            timer.start();
        }

        private Color blend(Color a, Color b, float t) {
            return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                    (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
        }

        private int distance(Color a, Color b) {
            return Math.abs(a.getRed() - b.getRed()) + Math.abs(a.getGreen() - b.getGreen()) + Math.abs(a.getBlue() - b.getBlue());
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(18, 3, getWidth() - 32, getHeight() - 6, 8, 8);
            if (selected) {
                g2.setColor(accent);
                g2.fillRoundRect(18, 10, 4, getHeight() - 20, 4, 4);
            }
            g2.dispose();
            super.paintComponent(g);
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

        void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Draw a fixed size square centered within the component
            int w = size;
            int h = size;
            int dx = (getWidth() - w) / 2;
            int dy = (getHeight() - h) / 2;
            g2.translate(dx, dy);
            
            int cx = w / 2;
            int cy = h / 2;
            if ("logo".equals(type)) {
                g2.fillRoundRect(2, cy - 4, w - 4, 8, 5, 5);
                g2.fillRoundRect(cx - 4, 2, 8, h - 4, 5, 5);
            } else if ("dashboard".equals(type)) {
                g2.drawRect(3, 3, 6, 6);
                g2.drawRect(w - 9, 3, 6, 6);
                g2.drawRect(3, h - 9, 6, 6);
                g2.drawRect(w - 9, h - 9, 6, 6);
            } else if ("doctor".equals(type)) {
                g2.drawOval(cx - 5, 3, 10, 10);
                g2.drawRoundRect(4, h - 9, w - 8, 7, 5, 5);
                g2.drawLine(cx, 7, cx, 11);
                g2.drawLine(cx - 3, 9, cx + 3, 9);
            } else if ("patient".equals(type) || "patients".equals(type)) {
                g2.drawOval(cx - 4, 3, 8, 8);
                g2.drawRoundRect(4, h - 8, w - 8, 6, 5, 5);
            } else if ("calendar".equals(type) || "appointments".equals(type)) {
                g2.drawRoundRect(3, 5, w - 6, h - 7, 4, 4);
                g2.drawLine(3, 10, w - 3, 10);
                g2.drawLine(7, 3, 7, 7);
                g2.drawLine(w - 7, 3, w - 7, 7);
            } else if ("medicine".equals(type) || "medicines".equals(type)) {
                g2.drawRoundRect(4, 4, w - 8, h - 8, 8, 8);
                g2.drawLine(7, h - 7, w - 7, 7);
            } else if ("billing".equals(type) || "revenue".equals(type)) {
                g2.drawRoundRect(4, 3, w - 8, h - 6, 4, 4);
                g2.drawLine(7, 8, w - 7, 8);
                g2.drawLine(7, 13, w - 9, 13);
            } else if ("rooms".equals(type)) {
                g2.drawRect(4, 4, w - 8, h - 8);
                g2.drawLine(cx, 4, cx, h - 4);
                g2.drawLine(4, cy, w - 4, cy);
            } else if ("staff".equals(type)) {
                g2.drawOval(4, 5, 7, 7);
                g2.drawOval(w - 11, 5, 7, 7);
                g2.drawLine(5, h - 4, w - 5, h - 4);
            } else if ("reports".equals(type)) {
                g2.drawLine(5, h - 4, 5, h - 9);
                g2.drawLine(cx, h - 4, cx, 5);
                g2.drawLine(w - 5, h - 4, w - 5, h - 12);
            } else if ("profile".equals(type)) {
                g2.drawOval(cx - 5, 3, 10, 10);
                g2.drawArc(4, h - 12, w - 8, 10, 0, 180);
            } else if ("logout".equals(type)) {
                g2.drawLine(4, cy, w - 6, cy);
                g2.drawLine(w - 10, cy - 4, w - 5, cy);
                g2.drawLine(w - 10, cy + 4, w - 5, cy);
            } else if ("bell".equals(type)) {
                g2.drawArc(6, 5, w - 12, h - 8, 0, 180);
                g2.drawLine(6, cy, 6, h - 7);
                g2.drawLine(w - 6, cy, w - 6, h - 7);
                g2.drawLine(5, h - 7, w - 5, h - 7);
                g2.fillOval(cx - 2, h - 4, 4, 3);
            } else {
                g2.drawOval(4, 4, w - 8, h - 8);
            }
            g2.dispose();
        }
    }
}
