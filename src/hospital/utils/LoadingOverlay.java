package hospital.utils;

import javax.swing.*;
import java.awt.*;

public class LoadingOverlay extends JPanel {
    private final JLabel label = new JLabel("Loading");
    private int dots = 0;
    private final Timer timer;

    public LoadingOverlay() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        add(label);
        timer = new Timer(350, e -> {
            dots = (dots + 1) % 4;
            StringBuilder text = new StringBuilder("Loading");
            for (int i = 0; i < dots; i++) text.append(".");
            label.setText(text.toString());
        });
        setVisible(false);
    }

    public void showOverlay() {
        setVisible(true);
        timer.start();
    }

    public void hideOverlay() {
        timer.stop();
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(15, 23, 42, 130));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
