package hospital.ui;

import hospital.db.UserDAO;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Hospital Management System - Login");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left side - branding
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY_DARK, getWidth(), getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel crossIcon = new JLabel("✚");
        crossIcon.setFont(new Font("Segoe UI", Font.PLAIN, 70));
        crossIcon.setForeground(new Color(255, 255, 255, 200));
        crossIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel("HMS");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandSubtitle = new JLabel("Hospital Management System");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        brandSubtitle.setForeground(new Color(255, 255, 255, 200));
        brandSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Streamlining healthcare,<br>one record at a time.</center></html>");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tagline.setForeground(new Color(255, 255, 255, 170));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(crossIcon);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(brandTitle);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(brandSubtitle);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(tagline);
        leftPanel.add(Box.createVerticalGlue());

        // Right side - login form
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formContainer = new JPanel();
        formContainer.setBackground(Color.WHITE);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setPreferredSize(new Dimension(320, 420));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(UITheme.FONT_TITLE);
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSubtitle = new JLabel("Sign in to your account");
        loginSubtitle.setFont(UITheme.FONT_BODY);
        loginSubtitle.setForeground(UITheme.TEXT_MUTED);
        loginSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = UITheme.createLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(320, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLbl = UITheme.createLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(320, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noAccLbl = new JLabel("Don't have an account? ");
        noAccLbl.setFont(UITheme.FONT_BODY);
        noAccLbl.setForeground(UITheme.TEXT_MUTED);
        JButton registerLink = new JButton("Register");
        registerLink.setFont(UITheme.FONT_BUTTON);
        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerPanel.add(noAccLbl);
        registerPanel.add(registerLink);

        formContainer.add(loginTitle);
        formContainer.add(Box.createVerticalStrut(4));
        formContainer.add(loginSubtitle);
        formContainer.add(Box.createVerticalStrut(25));
        formContainer.add(userLbl);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(usernameField);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(passLbl);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(passwordField);
        formContainer.add(Box.createVerticalStrut(6));
        formContainer.add(errorLabel);
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(loginBtn);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(registerPanel);

        rightPanel.add(formContainer);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> performLogin());
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });
        registerLink.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        if (userDAO.login(username, password)) {
            new DashboardFrame().setVisible(true);
            dispose();
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}
