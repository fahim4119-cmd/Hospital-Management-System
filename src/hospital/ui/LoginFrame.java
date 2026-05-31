package hospital.ui;

import hospital.db.UserDAO;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private final UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Hospital Management System - Login");
        setSize(920, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = UITheme.createGradientPanel(UITheme.PRIMARY_DARK, UITheme.PRIMARY);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(62, 44, 62, 44));

        JLabel badge = new JLabel("CARE DESK");
        badge.setFont(UITheme.FONT_BUTTON);
        badge.setForeground(UITheme.PRIMARY_DARK);
        badge.setOpaque(true);
        badge.setBackground(new Color(229, 248, 244));
        badge.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel("HMS");
        brandTitle.setFont(new Font("Verdana", Font.BOLD, 54));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandSubtitle = new JLabel("Hospital Management System");
        brandSubtitle.setFont(UITheme.FONT_BODY);
        brandSubtitle.setForeground(new Color(230, 249, 246));
        brandSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Streamlining healthcare,<br>one record at a time.</center></html>");
        tagline.setFont(UITheme.FONT_SMALL);
        tagline.setForeground(new Color(213, 238, 236));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(badge);
        leftPanel.add(Box.createVerticalStrut(18));
        leftPanel.add(brandTitle);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(brandSubtitle);
        leftPanel.add(Box.createVerticalStrut(22));
        leftPanel.add(tagline);
        leftPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UITheme.BACKGROUND);

        JPanel formContainer = UITheme.createCard(null);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setPreferredSize(new Dimension(360, 438));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(UITheme.FONT_TITLE);
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSubtitle = new JLabel("Sign in to continue to your dashboard");
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
        usernameField.setMaximumSize(new Dimension(320, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLbl = UITheme.createLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(320, 42));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerPanel.setOpaque(false);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noAccLbl = new JLabel("Don't have an account? ");
        noAccLbl.setFont(UITheme.FONT_BODY);
        noAccLbl.setForeground(UITheme.TEXT_MUTED);

        JButton registerLink = new JButton("Register");
        registerLink.setFont(UITheme.FONT_BUTTON);
        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setFocusPainted(false);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerPanel.add(noAccLbl);
        registerPanel.add(registerLink);

        formContainer.add(loginTitle);
        formContainer.add(Box.createVerticalStrut(4));
        formContainer.add(loginSubtitle);
        formContainer.add(Box.createVerticalStrut(28));
        formContainer.add(userLbl);
        formContainer.add(Box.createVerticalStrut(6));
        formContainer.add(usernameField);
        formContainer.add(Box.createVerticalStrut(16));
        formContainer.add(passLbl);
        formContainer.add(Box.createVerticalStrut(6));
        formContainer.add(passwordField);
        formContainer.add(Box.createVerticalStrut(8));
        formContainer.add(errorLabel);
        formContainer.add(Box.createVerticalStrut(12));
        formContainer.add(loginBtn);
        formContainer.add(Box.createVerticalStrut(18));
        formContainer.add(registerPanel);

        rightPanel.add(formContainer);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

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
