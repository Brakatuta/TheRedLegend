package Util;

import javax.swing.*;
import java.awt.*;


public class GameWindow {
    public static Dimension WINDOW_SIZE = new Dimension(1912, 1162);

    public static JFrame MainWindow;
    public static Dimension ScreenSize;
    public static int[] ScreenCenter;

    public static void initDefaults(JFrame frame) {
        // Get the primary monitor's bounds
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice(); // primary monitor
        Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();

        Dimension frameSize = frame.getSize();

        int x = screenBounds.x + (screenBounds.width - frameSize.width) / 2;
        int y = screenBounds.y + (screenBounds.height - frameSize.height) / 2;

        ScreenCenter = new int[]{x, y};
    }

    public static void repositionWindow(int[] WindowPosition) {
        if (WindowPosition != null) {
            MainWindow.setLocation(WindowPosition[0], WindowPosition[1]);
        }
    }

    public static void initializeWindow() {
        MainWindow = new JFrame("Adventure Game");
        MainWindow.setPreferredSize(new Dimension(1928, 1200));
        MainWindow.setResizable(true);
        MainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.setLayout(null); // Allow absolute positioning

        MainWindow.pack();
        initDefaults(MainWindow);
        repositionWindow(ScreenCenter);
        MainWindow.setVisible(true);
    }
}