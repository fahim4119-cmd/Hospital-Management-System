package hospital.ui;

import hospital.db.UserDAO;
import hospital.models.User;
import hospital.utils.Session;
import hospital.utils.UITheme;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    public ProfilePanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BACKGROUND);
        initUI();
    }

    private void initUI() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setOpaque(false);
        
        // Header
        page.add(UITheme.createPageHeader("Profile Settings", "Logged-in user information and password management.", UITheme.PRIMARY), BorderLayout.NORTH);

        // Card Container
        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.add(profileCard(), BorderLayout.CENTER);
        
        page.add(body, BorderLayout.CENTER);
        add(page, BorderLayout.CENTER);
    }

    private JPanel profileCard() {
        User user = Session.getInstance().getUser();
        JPanel card = UITheme.createCard("");
        card.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Top avatar icon section
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setOpaque(false);
        JLabel avatarLabel = avatarLabel();
        avatarPanel.add(avatarLabel);

        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(avatarPanel, gbc);

        // Information Grid
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Full Name
        gbc.gridy++; gbc.gridx = 0;
        JLabel nameLbl = new JLabel("Full Name");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(nameLbl, gbc);
        
        gbc.gridx = 1;
        JLabel nameVal = new JLabel(user == null ? "Guest User" : user.getFullName());
        nameVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameVal.setForeground(UITheme.TEXT_PRIMARY);
        card.add(nameVal, gbc);

        // Username
        gbc.gridy++; gbc.gridx = 0;
        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(userLbl, gbc);
        
        gbc.gridx = 1;
        JLabel userVal = new JLabel(user == null ? "-" : user.getUsername());
        userVal.setFont(UITheme.FONT_BODY);
        userVal.setForeground(UITheme.TEXT_PRIMARY);
        card.add(userVal, gbc);

        // Role Badge
        gbc.gridy++; gbc.gridx = 0;
        JLabel roleLbl = new JLabel("Account Role");
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(roleLbl, gbc);
        
        gbc.gridx = 1;
        JLabel roleVal = new JLabel(Session.getInstance().getRole().toUpperCase());
        roleVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleVal.setForeground(UITheme.PRIMARY);
        card.add(roleVal, gbc);

        // Email
        gbc.gridy++; gbc.gridx = 0;
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLbl.setForeground(UITheme.TEXT_MUTED);
        card.add(emailLbl, gbc);
        
        gbc.gridx = 1;
        JLabel emailVal = new JLabel(user == null ? "-" : user.getEmail());
        emailVal.setFont(UITheme.FONT_BODY);
        emailVal.setForeground(UITheme.TEXT_PRIMARY);
        card.add(emailVal, gbc);

        // Actions Row
        gbc.gridy++; gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(24, 15, 10, 15);
        JButton change = UITheme.createPrimaryButton("Change Password");
        change.setPreferredSize(new Dimension(160, 36));
        card.add(change, gbc);

        change.addActionListener(e -> changePassword(user));
        return card;
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
                g2.setColor(UITheme.PRIMARY_DARK);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setPreferredSize(new Dimension(72, 72));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        return label;
    }

    private void changePassword(User user) {
        if (user == null) return;
        JPasswordField oldPass = UITheme.createPasswordField();
        JPasswordField newPass = UITheme.createPasswordField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Current Password")); form.add(oldPass);
        form.add(new JLabel("New Password")); form.add(newPass);
        if (JOptionPane.showConfirmDialog(this, form, "Change Password", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            boolean ok = new UserDAO().changePassword(user.getId(), new String(oldPass.getPassword()), new String(newPass.getPassword()));
            JOptionPane.showMessageDialog(this, ok ? "Password updated." : "Current password is incorrect.");
        }
    }
}
