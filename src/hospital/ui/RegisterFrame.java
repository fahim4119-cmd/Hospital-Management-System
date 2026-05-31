package hospital.ui;

import hospital.db.UserDAO;
import hospital.models.User;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JLabel errorLabel;
    private final UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("HMS - Register");
        setSize(560, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = UITheme.createGradientPanel(UITheme.BACKGROUND, new Color(225, 241, 238));
        mainPanel.setLayout(new GridBagLayout());

        JPanel wrapper = UITheme.createCard(null);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setPreferredSize(new Dimension(390, 560));

        JLabel badge = new JLabel("NEW ACCOUNT");
        badge.setFont(UITheme.FONT_SMALL);
        badge.setForeground(UITheme.PRIMARY);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to access the HMS workspace");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameField = createWideField();
        usernameField = createWideField();
        emailField = createWideField();
        passwordField = createWidePasswordField();
        confirmPasswordField = createWidePasswordField();

        JButton registerBtn = UITheme.createSuccessButton("Register");
        registerBtn.setMaximumSize(new Dimension(340, 42));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setOpaque(false);
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel alreadyLbl = new JLabel("Already have an account? ");
        alreadyLbl.setFont(UITheme.FONT_BODY);
        alreadyLbl.setForeground(UITheme.TEXT_MUTED);
        JButton loginLink = new JButton("Login");
        loginLink.setFont(UITheme.FONT_BUTTON);
        loginLink.setForeground(UITheme.PRIMARY);
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setFocusPainted(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backPanel.add(alreadyLbl);
        backPanel.add(loginLink);

        wrapper.add(badge);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(subtitle);
        wrapper.add(Box.createVerticalStrut(20));
        addFieldRow(wrapper, "Full Name", fullNameField);
        addFieldRow(wrapper, "Username", usernameField);
        addFieldRow(wrapper, "Email", emailField);
        addFieldRow(wrapper, "Password", passwordField);
        addFieldRow(wrapper, "Confirm Password", confirmPasswordField);
        wrapper.add(Box.createVerticalStrut(3));
        wrapper.add(errorLabel);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(registerBtn);
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(backPanel);

        mainPanel.add(wrapper);
        add(mainPanel);

        registerBtn.addActionListener(e -> performRegister());
        loginLink.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private JTextField createWideField() {
        JTextField field = UITheme.createTextField();
        field.setMaximumSize(new Dimension(340, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createWidePasswordField() {
        JPasswordField field = UITheme.createPasswordField();
        field.setMaximumSize(new Dimension(340, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void addFieldRow(JPanel panel, String label, JComponent field) {
        JLabel lbl = UITheme.createLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private void performRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (Validator.isEmpty(fullName) || Validator.isEmpty(username) ||
                Validator.isEmpty(email) || Validator.isEmpty(password)) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!Validator.isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address.");
            return;
        }
        if (!Validator.isPasswordStrong(password)) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        User user = new User(0, username, password, fullName, email);
        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            errorLabel.setText("Username already exists. Choose a different one.");
        }
    }
}
