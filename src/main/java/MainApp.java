import controller.MainController;
import view.MainView;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Ensure GUI runs on the Swing event dispatch thread
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();      // initialize the view
            new MainController(view);            // initialize the controller
            view.setVisible(true);               // show the GUI
        });
    }
}
