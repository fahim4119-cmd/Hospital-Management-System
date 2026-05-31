package hospital.utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UITheme {
    public static final Color PRIMARY = new Color(17, 116, 123);
    public static final Color PRIMARY_DARK = new Color(9, 55, 63);
    public static final Color PRIMARY_LIGHT = new Color(213, 242, 239);
    public static final Color ACCENT = new Color(232, 138, 62);
    public static final Color SUCCESS = new Color(33, 158, 121);
    public static final Color DANGER = new Color(205, 79, 74);
    public static final Color WARNING = new Color(234, 179, 8);
    public static final Color BACKGROUND = new Color(242, 247, 246);
    public static final Color CARD_BG = new Color(255, 255, 255);
    public static final Color SIDEBAR_BG = new Color(12, 32, 40);
    public static final Color SIDEBAR_DARK = new Color(7, 22, 29);
    public static final Color SIDEBAR_HOVER = new Color(23, 72, 82);
    public static final Color SIDEBAR_TEXT = new Color(218, 236, 235);
    public static final Color TEXT_PRIMARY = new Color(30, 48, 55);
    public static final Color TEXT_MUTED = new Color(102, 121, 128);
    public static final Color BORDER = new Color(214, 226, 224);
    public static final Color TABLE_HEADER = new Color(12, 32, 40);
    public static final Color TABLE_ALT = new Color(247, 251, 250);

    public static final Font FONT_DISPLAY = new Font("Verdana", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Verdana", Font.BOLD, 23);
    public static final Font FONT_SUBTITLE = new Font("Verdana", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Trebuchet MS", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Trebuchet MS", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Trebuchet MS", Font.BOLD, 13);

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, new Color(14, 96, 102));
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, new Color(178, 63, 58));
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, new Color(26, 132, 101));
    }

    public static JButton createAccentButton(String text) {
        return createButton(text, ACCENT, new Color(210, 111, 44));
    }

    private static JButton createButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        btn.setPreferredSize(new Dimension(126, 38));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(PRIMARY);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(220, 38));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(PRIMARY);
        pf.setBackground(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        pf.setPreferredSize(new Dimension(220, 38));
        return pf;
    }

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JPanel createCard(String title) {
        JPanel panel = new RoundedPanel(new BorderLayout(), 22, CARD_BG);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        if (title != null && !title.isEmpty()) {
            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(FONT_SUBTITLE);
            titleLbl.setForeground(TEXT_PRIMARY);
            titleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(titleLbl, BorderLayout.NORTH);
        }
        return panel;
    }

    public static JPanel createSectionHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(10, 4));
        header.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(TEXT_PRIMARY);
        header.add(titleLbl, BorderLayout.NORTH);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subtitleLbl = new JLabel(subtitle);
            subtitleLbl.setFont(FONT_BODY);
            subtitleLbl.setForeground(TEXT_MUTED);
            header.add(subtitleLbl, BorderLayout.CENTER);
        }
        return header;
    }

    public static JPanel createGradientPanel(Color start, Color end) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
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

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_BODY);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBackground(Color.WHITE);
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", FONT_BODY);
            UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
            UIManager.put("TableHeader.cellBorder", BorderFactory.createEmptyBorder());
        } catch (Exception e) {
            // use default
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedPanel(LayoutManager layout, int arc, Color fill) {
            super(layout);
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? fill : getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
