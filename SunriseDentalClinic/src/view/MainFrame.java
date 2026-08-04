package view;

import model.User;
import model.User.UserRole;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SidebarPanel sidebarPanel;

    public MainFrame() {
        setTitle("Sunrise Dental Clinic - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initContentPanel();
        
        sidebarPanel = new SidebarPanel(this);
        add(sidebarPanel, BorderLayout.WEST);
        add(new TopBarPanel(), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Dashboard placeholder
        JPanel dashboardPlaceholder = new JPanel(new BorderLayout());
        JLabel placeholderLabel = new JLabel("Dashboard goes here", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        placeholderLabel.setForeground(new Color(100, 100, 100));
        dashboardPlaceholder.add(placeholderLabel, BorderLayout.CENTER);
        dashboardPlaceholder.setBackground(Color.WHITE);

        contentPanel.add(dashboardPlaceholder, "DASHBOARD");
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    public void configureSidebarForRole(UserRole role) {
        sidebarPanel.configureForRole(role);
    }

    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    public void addScreen(String cardName, JPanel screen) {
        contentPanel.add(screen, cardName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}