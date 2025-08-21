package view;

import javax.swing.*;
import java.awt.*;

// Builds the GUI
public class MainView extends JFrame {
    public final JLabel StatsLabel;
    public JTextField textField1;
    public JTextField textField2;
    public JButton getStatsButton;
    public JPanel MainPanel;
    public JLabel TitleName;
    public JLabel TitleYear;

    public MainView() {
        setTitle("NBA Stats App");
        setSize(660, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        TitleName = new JLabel("Enter NBA Player Name:");
        textField1 = new JTextField(15);
        TitleYear = new JLabel("Enter Year:");
        textField2 = new JTextField(5);
        getStatsButton = new JButton("Get Stats");
        StatsLabel = new JLabel("", SwingConstants.CENTER);

        // Main panel (vertical layout)
        MainPanel = new JPanel(new BorderLayout());
        MainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10)); // Reduced top padding

        // Input row panel (centered horizontally)
        JPanel inputPanel = new JPanel();
        inputPanel.add(TitleName);
        inputPanel.add(textField1);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(TitleYear);
        inputPanel.add(textField2);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(getStatsButton);

        // Center the input panel horizontally
        JPanel inputWrapper = new JPanel(new GridBagLayout());
        inputWrapper.add(inputPanel);

        // Stats label configuration
        StatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(StatsLabel);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

        // Add components to main panel
        MainPanel.add(inputWrapper, BorderLayout.NORTH);
        MainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(MainPanel);
    }
}