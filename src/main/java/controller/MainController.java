package controller;

import model.MultiApiClient;
import model.Player;
import view.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Assigns data to the GUI and handles events
public class MainController {
    private MainView view;

    public MainController(MainView view) {
        this.view = view;

        // Attach event listener
        this.view.getStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = view.textField1.getText().trim().replaceAll("[^a-zA-Z0-9 -]", "");
                String year = view.textField2.getText().trim();
                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Please enter a player name.");
                    return;
                }

                try {
                    Player player = MultiApiClient.fetchPlayerData(playerName, year);
                    if (player != null) {
                        view.StatsLabel.setText("<html>" + player.toString().replaceAll("\n", "<br>") + "</html>");

                    } else {
                        view.StatsLabel.setText("Player not found.");
                    }
                } catch (Exception ex) {
                    view.StatsLabel.setText("Error fetching player data. Error: " + ex.getMessage());
                }
            }
        });
    }
}
