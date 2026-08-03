package view;

import java.awt.*;
import javax.swing.*;

/**
 * MainFrame - the main application shell.
 * Now includes SidebarPanel (left nav) and TopBarPanel (top search/icons),
 * with a CardLayout content area in the center for screen switching.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("Sunrise Dental Clinic - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout());

        initContentPanel();

        add(new SidebarPanel(this), BorderLayout.WEST);
        add(new TopBarPanel(), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Placeholder dashboard card for now.
        // Will be replaced with view.DashboardPanel once it's built.
        JPanel dashboardPlaceholder = new JPanel(new BorderLayout());
        JLabel placeholderLabel = new JLabel("Dashboard goes here", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        placeholderLabel.setForeground(new Color(100, 100, 100));
        dashboardPlaceholder.add(placeholderLabel, BorderLayout.CENTER);
        dashboardPlaceholder.setBackground(Color.WHITE);

        contentPanel.add(dashboardPlaceholder, "DASHBOARD");

        cardLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Called by SidebarPanel to switch screens.
     * e.g. mainFrame.showCard("PATIENTS")
     */
    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    /**
     * Called once real screens exist, to register them as cards.
     * e.g. mainFrame.addScreen("PATIENTS", new PatientListPanel());
     */
    public void addScreen(String cardName, JPanel screen) {
        contentPanel.add(screen, cardName);
    }

    public static void main(String[] args) {
        // Run on the Event Dispatch Thread, as Swing requires
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}