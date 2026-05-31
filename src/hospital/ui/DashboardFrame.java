package hospital.ui;

import hospital.db.*;
import hospital.utils.UITheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardFrame extends JFrame {

    private JPanel contentArea;
    private JLabel pageTitle;
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private MedicineDAO medicineDAO = new MedicineDAO();

    public DashboardFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));

        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.BACKGROUND);

        // Top bar
        JPanel topBar = createTopBar();
        mainArea.add(topBar, BorderLayout.NORTH);

        // Content
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BACKGROUND);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainArea.add(contentArea, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        showHome();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo area
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(20, 28, 35));
        logoPanel.setMaximumSize(new Dimension(220, 70));
        logoPanel.setPreferredSize(new Dimension(220, 70));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel logo = new JLabel("✚ HMS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        logoPanel.add(logo);
        logoPanel.add(Box.createHorizontalGlue());

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(15));

        // Nav items
        sidebar.add(createNavItem("🏠  Dashboard", () -> showHome()));
        sidebar.add(createNavItem("👨‍⚕️  Doctors", () -> showModule("doctors")));
        sidebar.add(createNavItem("🏥  Patients", () -> showModule("patients")));
        sidebar.add(createNavItem("📅  Appointments", () -> showModule("appointments")));
        sidebar.add(createNavItem("💊  Medicines", () -> showModule("medicines")));

        sidebar.add(Box.createVerticalGlue());

        // Logout
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        logoutPanel.setBackground(UITheme.SIDEBAR_BG);
        logoutPanel.setMaximumSize(new Dimension(220, 50));
        JButton logoutBtn = new JButton("🚪  Logout");
        logoutBtn.setFont(UITheme.FONT_BODY);
        logoutBtn.setForeground(new Color(231, 76, 60));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private JPanel createNavItem(String text, Runnable action) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        item.setBackground(UITheme.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(220, 46));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.SIDEBAR_TEXT);
        item.add(lbl);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(44, 55, 63));
                lbl.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(UITheme.SIDEBAR_BG);
                lbl.setForeground(UITheme.SIDEBAR_TEXT);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return item;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 25, 0, 25)
        ));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_SUBTITLE);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(pageTitle, BorderLayout.WEST);

        JLabel userInfo = new JLabel("Administrator  👤");
        userInfo.setFont(UITheme.FONT_BODY);
        userInfo.setForeground(UITheme.TEXT_MUTED);
        topBar.add(userInfo, BorderLayout.EAST);

        return topBar;
    }

    private void showHome() {
        pageTitle.setText("Dashboard");
        contentArea.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);

        JLabel welcome = new JLabel("Welcome to Hospital Management System");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.TEXT_PRIMARY);
        welcome.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Stats row
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(UITheme.BACKGROUND);

        statsPanel.add(createStatCard("Total Doctors", String.valueOf(doctorDAO.getTotalDoctors()),
                "👨‍⚕️", UITheme.PRIMARY));
        statsPanel.add(createStatCard("Total Patients", String.valueOf(patientDAO.getTotalPatients()),
                "🏥", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Appointments", String.valueOf(appointmentDAO.getTotalAppointments()),
                "📅", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Medicines", String.valueOf(medicineDAO.getTotalMedicines()),
                "💊", new Color(241, 196, 15)));

        // Quick access cards
        JPanel quickPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        quickPanel.setBackground(UITheme.BACKGROUND);
        quickPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        quickPanel.add(createQuickCard("Manage Doctors", "View, add, edit doctors", () -> showModule("doctors"), UITheme.PRIMARY));
        quickPanel.add(createQuickCard("Manage Patients", "View, add, edit patients", () -> showModule("patients"), new Color(46, 204, 113)));
        quickPanel.add(createQuickCard("Appointments", "Schedule appointments", () -> showModule("appointments"), new Color(155, 89, 182)));
        quickPanel.add(createQuickCard("Medicines", "Manage medicine stock", () -> showModule("medicines"), new Color(241, 196, 15)));

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(UITheme.BACKGROUND);
        northWrapper.add(welcome, BorderLayout.NORTH);
        northWrapper.add(statsPanel, BorderLayout.CENTER);

        panel.add(northWrapper, BorderLayout.NORTH);
        panel.add(quickPanel, BorderLayout.CENTER);

        contentArea.add(panel);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(color);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BODY);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(titleLbl);

        return card;
    }

    private JPanel createQuickCard(String title, String description, Runnable action, Color color) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(18, 16, 18, 16)
            )
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBTITLE);
        titleLbl.setForeground(UITheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel(description);
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_MUTED);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrow.setForeground(color);
        arrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(arrow);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
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
        }
        contentArea.revalidate();
        contentArea.repaint();
    }
}
