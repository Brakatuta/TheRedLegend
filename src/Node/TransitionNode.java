package Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TransitionNode extends Node {
    private float currentAlpha = 1.0f;
    private Color currentColor = Color.BLACK;

    private Color targetColor;
    private float targetAlpha;
    private int duration; // in milliseconds

    private Timer transitionTimer;
    private long startTime;
    private Color startColor;
    private float startAlpha;

    private TransitionListener onTransitionFinished; // Callback interface

    public TransitionNode(int[] size, int[] relativePosition) {
        super(size, relativePosition);
        panel = new FadingPanel();
        panel.setBounds(relativePosition[0], relativePosition[1], size[0], size[1]);
        panel.setOpaque(false);
    }

    // Start a transition to a new color and alpha over a specified duration
    public void startTransition(Color newColor, float newAlpha, int durationMillis) {
        this.startColor = currentColor;
        this.startAlpha = currentAlpha;
        this.targetColor = newColor;
        this.targetAlpha = newAlpha;
        this.duration = durationMillis;
        this.startTime = System.currentTimeMillis();

        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }

        transitionTimer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float t = Math.min(1.0f, (float) elapsed / duration);

                currentAlpha = lerp(startAlpha, targetAlpha, t);
                currentColor = lerpColor(startColor, targetColor, t);

                panel.repaint();

                if (t >= 1.0f) {
                    transitionTimer.stop();
                    // Trigger finished callback
                    if (onTransitionFinished != null) {
                        onTransitionFinished.onFinished(TransitionNode.this);
                    }
                }
            }
        });

        transitionTimer.start();
    }

    // Starts an in-out transition: to targetColor/alpha and back to original
    public void startInOutTransition(Color newColor, float newAlpha, int durationMillis) {
        final Color originalColor = currentColor;
        final float originalAlpha = currentAlpha;

        final int halfDuration = durationMillis / 2;
        this.startTime = System.currentTimeMillis();

        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }

        transitionTimer = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float t;
                boolean firstHalf = elapsed < halfDuration;

                if (firstHalf) {
                    t = (float) elapsed / halfDuration;
                    currentAlpha = lerp(originalAlpha, newAlpha, t);
                    currentColor = lerpColor(originalColor, newColor, t);
                } else {
                    t = (float) (elapsed - halfDuration) / halfDuration;
                    t = Math.min(t, 1.0f); // Clamp to avoid going over
                    currentAlpha = lerp(newAlpha, originalAlpha, t);
                    currentColor = lerpColor(newColor, originalColor, t);
                }

                panel.repaint();

                if (elapsed >= durationMillis) {
                    transitionTimer.stop();

                    // Ensure final values are clean
                    currentAlpha = originalAlpha;
                    currentColor = originalColor;
                    panel.repaint();

                    if (onTransitionFinished != null) {
                        onTransitionFinished.onFinished(TransitionNode.this);
                    }
                }
            }
        });

        transitionTimer.start();
    }

    // listener to be called when the transition finishes
    public void setOnTransitionFinished(TransitionListener listener) {
        this.onTransitionFinished = listener;
    }

    public void removeTransitionFinishedListener() {
        this.onTransitionFinished = null;
    }

    private float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }

    private Color lerpColor(Color c1, Color c2, float t) {
        int r = (int) lerp(c1.getRed(), c2.getRed(), t);
        int g = (int) lerp(c1.getGreen(), c2.getGreen(), t);
        int b = (int) lerp(c1.getBlue(), c2.getBlue(), t);
        return new Color(r, g, b);
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.currentColor = color;
        panel.repaint();
    }

    public void setAlpha(float alpha) {
        this.currentAlpha = alpha;
        panel.repaint();
    }

    public float getAlpha() {
        return currentAlpha;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    // Custom JPanel with alpha rendering
    private class FadingPanel extends JPanel {
        public FadingPanel() {
            super(null);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
            g2d.setColor(currentColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    // Callback interface
    public interface TransitionListener {
        void onFinished(TransitionNode node);
    }
}