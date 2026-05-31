package hospital;

import hospital.ui.LoginFrame;
import hospital.utils.UITheme;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UITheme.applyLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
