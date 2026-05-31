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
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("HMS - Register");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(360, 480));

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to access the HMS");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameField = UITheme.createTextField();
        fullNameField.setMaximumSize(new Dimension(360, 36));
        fullNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UITheme.createTextField();
        usernameField.setMaximumSize(new Dimension(360, 36));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = UITheme.createTextField();
        emailField.setMaximumSize(new Dimension(360, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = UITheme.createPasswordField();
        passwordField.setMaximumSize(new Dimension(360, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmPasswordField = UITheme.createPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(360, 36));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerBtn = UITheme.createSuccessButton("Register");
        registerBtn.setMaximumSize(new Dimension(360, 40));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backPanel.setBackground(Color.WHITE);
        backPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel alreadyLbl = new JLabel("Already have an account? ");
        alreadyLbl.setFont(UITheme.FONT_BODY);
        alreadyLbl.setForeground(UITheme.TEXT_MUTED);
        JButton loginLink = new JButton("Login");
        loginLink.setFont(UITheme.FONT_BUTTON);
        loginLink.setForeground(UITheme.PRIMARY);
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backPanel.add(alreadyLbl);
        backPanel.add(loginLink);

        addFieldRow(form, "Full Name", fullNameField);
        form.add(Box.createVerticalStrut(10));
        addFieldRow(form, "Username", usernameField);
        form.add(Box.createVerticalStrut(10));
        addFieldRow(form, "Email", emailField);
        form.add(Box.createVerticalStrut(10));
        addFieldRow(form, "Password", passwordField);
        form.add(Box.createVerticalStrut(10));
        addFieldRow(form, "Confirm Password", confirmPasswordField);
        form.add(Box.createVerticalStrut(5));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(10));
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(15));
        form.add(backPanel);

        JPanel wrapper = new JPanel();
        wrapper.setBackground(Color.WHITE);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(subtitle);
        wrapper.add(Box.createVerticalStrut(20));
        wrapper.add(form);
        wrapper.setPreferredSize(new Dimension(360, 520));

        mainPanel.add(wrapper);
        add(mainPanel);

        registerBtn.addActionListener(e -> performRegister());
        loginLink.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addFieldRow(JPanel panel, String label, JComponent field) {
        JLabel lbl = UITheme.createLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
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
