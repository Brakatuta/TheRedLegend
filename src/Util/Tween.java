package Util;

import Node.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tween {
    private Node node;

    // position tween
    private int[] startPosition = new int[]{0, 0};
    private int[] endPosition = new int[]{0, 0};

    // color tween
    private Color startColor = new Color(0, 0, 0, 255);
    private Color endColor = new Color(0, 0, 0, 255);

    private double duration = 1.0; // seconds
    private volatile boolean running = false;
    private volatile boolean stopped = false; // flag to stop tween

    private Thread tweenThread;

    // Util.Tween finished listeners
    private final List<TweenListener> listeners = new ArrayList<>();

    // Listener interface
    public interface TweenListener {
        void onTweenFinished();
    }

    // add listener
    public void addTweenListener(TweenListener listener) {
        listeners.add(listener);
    }

    public void interpolatePosition(Node nodeToTween, int[] targetPosition, double tweenTime) {
        if (running) return; // prevent overlapping tweens

        this.node = nodeToTween;
        this.startPosition = new int[]{nodeToTween.getPosition()[0], nodeToTween.getPosition()[1]};
        this.endPosition = targetPosition;
        this.duration = tweenTime;
        startTween();
    }

    public void interpolateColor(Node nodeToTween, Color targetColor, double tweenTime) {
        if (running) return;
        this.node = nodeToTween;
        this.startColor = nodeToTween.getBackgroundColor();
        this.endColor = targetColor;
        this.duration = tweenTime;
        startTween();
    }

    private void startTween() {
        running = true;
        stopped = false;

        tweenThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (long) (duration * 1000);

            while (System.currentTimeMillis() < endTime) {
                if (stopped) {
                    running = false;
                    return; // stop the tween early
                }

                long currentTime = System.currentTimeMillis();
                double elapsed = (currentTime - startTime) / 1000.0;
                double progress = Math.min(elapsed / duration, 1.0); // Clamp to 1.0

                // Clamp progress
                if (progress > 1.0) progress = 1.0;

                // Linear interpolation

                if (!Arrays.equals(startPosition, endPosition)) {
                    int newX = (int) (startPosition[0] + (endPosition[0] - startPosition[0]) * progress);
                    int newY = (int) (startPosition[1] + (endPosition[1] - startPosition[1]) * progress);

                    // Update node on EDT
                    SwingUtilities.invokeLater(() -> node.setPosition(new int[]{newX, newY}));
                }

                if (!startColor.equals(endColor)) {
                    int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
                    int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
                    int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);
                    int a = (int) (startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * progress);

                    System.out.println(r + "|" + g + "|" + b + "|" + a);

                    Color interpolatedColor = new Color(r, g, b, a);
                    SwingUtilities.invokeLater(() -> {
                        node.setBackgroundColor(interpolatedColor);
                        node.getPanel().repaint();
                        node.getPanel().revalidate();
                    });
                }

                if (progress >= 1.0) break;

                try {
                    Thread.sleep(16); // ~60fps
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    running = false;
                    return;
                }
            }

            if (!stopped) {
                // Final set to ensure accuracy
                SwingUtilities.invokeLater(() -> node.setPosition(endPosition));

                running = false;

                // Notify listeners on EDT
                SwingUtilities.invokeLater(() -> {
                    for (TweenListener listener : listeners) {
                        listener.onTweenFinished();
                    }
                });
            }
        });

        tweenThread.start();
    }

    public boolean isRunning() {
        return running;
    }

    // New stop method to stop the tween early
    public void stop() {
        stopped = true;
        if (tweenThread != null && tweenThread.isAlive()) {
            tweenThread.interrupt();
        }
    }
}