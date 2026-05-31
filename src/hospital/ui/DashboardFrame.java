package hospital.ui;

import hospital.db.AppointmentDAO;
import hospital.db.DoctorDAO;
import hospital.db.MedicineDAO;
import hospital.db.PatientDAO;
import hospital.utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFrame extends JFrame {

    private JPanel contentArea;
    private JLabel pageTitle;
    private JPanel activeNavItem;
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    public DashboardFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.BACKGROUND);
        mainArea.add(createTopBar(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BACKGROUND);
        contentArea.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        mainArea.add(contentArea, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);
        showHome();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UITheme.SIDEBAR_DARK);
        logoPanel.setMaximumSize(new Dimension(245, 96));
        logoPanel.setPreferredSize(new Dimension(245, 96));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        JLabel logo = new JLabel("HMS");
        logo.setFont(new Font("Verdana", Font.BOLD, 30));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Care desk console");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(new Color(164, 204, 202));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitle);
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(18));

        sidebar.add(createNavItem("Dashboard", "Overview and quick actions", () -> showHome()));
        sidebar.add(createNavItem("Doctors", "Specialists and staff", () -> showModule("doctors")));
        sidebar.add(createNavItem("Patients", "Patient records", () -> showModule("patients")));
        sidebar.add(createNavItem("Appointments", "Visits and schedules", () -> showModule("appointments")));
        sidebar.add(createNavItem("Medicines", "Inventory control", () -> showModule("medicines")));
        sidebar.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 22, 18));
        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        logoutPanel.add(logoutBtn, BorderLayout.CENTER);
        sidebar.add(logoutPanel);

        return sidebar;
    }

    private JPanel createNavItem(String title, String caption, Runnable action) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(UITheme.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(245, 64));
        item.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 18));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BUTTON);
        titleLbl.setForeground(UITheme.SIDEBAR_TEXT);

        JLabel captionLbl = new JLabel(caption);
        captionLbl.setFont(UITheme.FONT_SMALL);
        captionLbl.setForeground(new Color(143, 177, 180));

        item.add(titleLbl, BorderLayout.NORTH);
        item.add(captionLbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (item != activeNavItem) item.setBackground(UITheme.SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (item != activeNavItem) item.setBackground(UITheme.SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveNav(item);
                action.run();
            }
        });

        return item;
    }

    private void setActiveNav(JPanel item) {
        if (activeNavItem != null) {
            activeNavItem.setBackground(UITheme.SIDEBAR_BG);
        }
        activeNavItem = item;
        activeNavItem.setBackground(UITheme.PRIMARY);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 68));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 28, 0, 28)
        ));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_SUBTITLE);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(pageTitle, BorderLayout.WEST);

        JLabel userInfo = new JLabel("Administrator");
        userInfo.setFont(UITheme.FONT_BODY);
        userInfo.setForeground(UITheme.TEXT_MUTED);
        topBar.add(userInfo, BorderLayout.EAST);

        return topBar;
    }

    private void showHome() {
        pageTitle.setText("Dashboard");
        contentArea.removeAll();

        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(UITheme.BACKGROUND);

        JPanel hero = UITheme.createGradientPanel(UITheme.PRIMARY_DARK, UITheme.PRIMARY);
        hero.setLayout(new BorderLayout(20, 0));
        hero.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Hospital command center");
        welcome.setFont(UITheme.FONT_DISPLAY);
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage doctors, patients, appointments, and medicine stock from one calm workspace.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(new Color(219, 244, 242));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        heroText.add(welcome);
        heroText.add(Box.createVerticalStrut(8));
        heroText.add(subtitle);
        hero.add(heroText, BorderLayout.CENTER);

        JLabel badge = new JLabel("LIVE");
        badge.setFont(UITheme.FONT_BUTTON);
        badge.setForeground(UITheme.PRIMARY_DARK);
        badge.setOpaque(true);
        badge.setBackground(new Color(228, 248, 243));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        hero.add(badge, BorderLayout.EAST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatCard("Doctors", String.valueOf(doctorDAO.getTotalDoctors()), "Specialists", UITheme.PRIMARY));
        statsPanel.add(createStatCard("Patients", String.valueOf(patientDAO.getTotalPatients()), "Active records", UITheme.SUCCESS));
        statsPanel.add(createStatCard("Appointments", String.valueOf(appointmentDAO.getTotalAppointments()), "Scheduled visits", new Color(86, 120, 196)));
        statsPanel.add(createStatCard("Medicines", String.valueOf(medicineDAO.getTotalMedicines()), "Inventory items", UITheme.ACCENT));

        JPanel quickPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        quickPanel.setOpaque(false);
        quickPanel.add(createQuickCard("Manage Doctors", "Add specialists, edit profiles, and search departments.", () -> showModule("doctors"), UITheme.PRIMARY));
        quickPanel.add(createQuickCard("Manage Patients", "Keep patient demographics and conditions organized.", () -> showModule("patients"), UITheme.SUCCESS));
        quickPanel.add(createQuickCard("Appointments", "Schedule visits and update appointment statuses.", () -> showModule("appointments"), new Color(86, 120, 196)));
        quickPanel.add(createQuickCard("Medicine Stock", "Track pricing, quantity, categories, and expiry dates.", () -> showModule("medicines"), UITheme.ACCENT));

        JPanel body = new JPanel(new BorderLayout(0, 22));
        body.setOpaque(false);
        body.add(statsPanel, BorderLayout.NORTH);
        body.add(quickPanel, BorderLayout.CENTER);

        panel.add(hero, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);

        contentArea.add(panel);
        refreshContent();
    }

    private JPanel createStatCard(String title, String value, String caption, Color color) {
        JPanel card = UITheme.createCard(null);
        card.setLayout(new BorderLayout(0, 12));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BODY);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Verdana", Font.BOLD, 38));
        valueLbl.setForeground(color);

        JLabel captionLbl = new JLabel(caption);
        captionLbl.setFont(UITheme.FONT_SMALL);
        captionLbl.setForeground(UITheme.TEXT_MUTED);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(titleLbl);
        text.add(Box.createVerticalStrut(8));
        text.add(valueLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(captionLbl);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickCard(String title, String description, Runnable action, Color color) {
        JPanel card = UITheme.createCard(null);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setLayout(new BorderLayout(0, 12));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);

        JLabel descLbl = new JLabel("<html><body style='width:170px'>" + description + "</body></html>");
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel actionLbl = new JLabel("Open module");
        actionLbl.setFont(UITheme.FONT_BUTTON);
        actionLbl.setForeground(color);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(actionLbl, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 253, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private void showModule(String module) {
        contentArea.removeAll();
        switch (module) {
            case "doctors":
                pageTitle.setText("Doctors");
                contentArea.add(new DoctorsPanel());
                break;
            case "patients":
                pageTitle.setText("Patients");
                contentArea.add(new PatientsPanel());
                break;
            case "appointments":
                pageTitle.setText("Appointments");
                contentArea.add(new AppointmentsPanel());
                break;
            case "medicines":
                pageTitle.setText("Medicines");
                contentArea.add(new MedicinesPanel());
                break;
            default:
                showHome();
                return;
        }
        refreshContent();
    }

    private void refreshContent() {
        contentArea.revalidate();
        contentArea.repaint();
    }
}
