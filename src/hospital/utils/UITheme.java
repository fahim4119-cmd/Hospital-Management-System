package hospital.utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UITheme {
    // Color palette
    public static final Color PRIMARY = new Color(20, 184, 166);
    public static final Color PRIMARY_DARK = new Color(15, 118, 110);
    public static final Color PRIMARY_LIGHT = new Color(204, 251, 241);
    public static final Color ACCENT = new Color(16, 185, 129);
    public static final Color DANGER = new Color(244, 63, 94);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color BACKGROUND = new Color(241, 245, 249);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color SIDEBAR_BG = Color.WHITE;
    public static final Color SIDEBAR_TEXT = new Color(71, 85, 105);
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color TABLE_HEADER = new Color(248, 250, 252);
    public static final Color TABLE_ALT = new Color(250, 252, 255);
    public static final Color PATIENT_GREEN = new Color(34, 197, 94);
    public static final Color APPOINTMENT_PURPLE = new Color(124, 58, 237);
    public static final Color MEDICINE_ORANGE = new Color(249, 115, 22);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    public static JButton createPrimaryButton(String text) {
        return createRoundedButton(text, PRIMARY);
    }

    public static JButton createDangerButton(String text) {
        return createRoundedButton(text, DANGER);
    }

    public static JButton createSuccessButton(String text) {
        return createRoundedButton(text, ACCENT);
    }

    public static JButton createRoundedButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(200, 36));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(200, 36));
        return pf;
    }

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JPanel createCard(String title) {
        JPanel panel = new ShadowPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (title != null && !title.isEmpty()) {
            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(FONT_SUBTITLE);
            titleLbl.setForeground(TEXT_PRIMARY);
            titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(titleLbl, BorderLayout.NORTH);
        }
        return panel;
    }

    public static JPanel createPageHeader(String title, String subtitle, Color accent) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JPanel strip = new JPanel();
        strip.setBackground(accent);
        strip.setPreferredSize(new Dimension(6, 48));
        panel.add(strip, BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(TEXT_PRIMARY);
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_BODY);
        subLbl.setForeground(TEXT_MUTED);
        text.add(titleLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(subLbl);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(javax.swing.JTable tbl, Object val,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // use default
        }
    }

    public static class ShadowPanel extends JPanel {
        public ShadowPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 12));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 10, 14, 14);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 8, getHeight() - 10, 14, 14);
            g2.setColor(new Color(226, 232, 240));
            g2.drawRoundRect(1, 1, getWidth() - 9, getHeight() - 11, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
