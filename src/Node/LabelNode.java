package Node;

import Util.Globals;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;


public class LabelNode extends Node {
    private String text;
    private Color textColor;
    private Color outlineColor = null;
    private float outlineThickness = 0f;
    final int textSize;
    final Font font;

    public LabelNode(String text, int[] position, int[] textBounds, Color textColor, int textSize) {
        super(textBounds, position);
        this.text = text;
        this.textColor = textColor;
        this.textSize = textSize;
        this.font = new Font("SansSerif", Font.BOLD, textSize);

        this.panel = new JLabelPanel();
        this.panel.setBounds(position[0], position[1], textBounds[0], textBounds[1]);
        this.panel.setOpaque(false);
    }

    public void setText(String newText) {
        this.text = newText.replace("Lukas", Globals.HERO_NAME);
        this.panel.repaint();
    }

    public void setColor(Color newColor) {
        this.textColor = newColor;
        if (this.panel != null) {
            this.panel.repaint();
        }
    }

    /** Adds or updates the outline */
    public void setOutline(Color color, float thickness) {
        this.outlineColor = color;
        this.outlineThickness = thickness;
        if (this.panel != null) {
            this.panel.repaint();
        }
    }

    private class JLabelPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setFont(font);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            FontMetrics fm = g2.getFontMetrics();

            int lineHeight = fm.getHeight();
            int maxWidth = getWidth();

            // Wrap text into lines
            List<String> lines = wrapText(text, fm, maxWidth);

            // Compute vertical starting point to center all lines
            int totalTextHeight = lines.size() * lineHeight;
            int y = (getHeight() - totalTextHeight) / 2 + fm.getAscent();

            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                int x = (getWidth() - lineWidth) / 2;

                // Draw outline if enabled
                if (outlineColor != null && outlineThickness > 0) {
                    g2.setColor(outlineColor);
                    g2.setStroke(new BasicStroke(outlineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    FontRenderContext frc = g2.getFontRenderContext();
                    GeneralPath path = new GeneralPath(
                            font.createGlyphVector(frc, line).getOutline(x, y)
                    );
                    g2.draw(path);
                }

                // Draw text fill
                g2.setColor(textColor);
                g2.drawString(line, x, y);
                y += lineHeight;
            }

            g2.dispose();
        }

        // Breaks text into lines that fit the panel width
        private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
            List<String> lines = new ArrayList<>();
            String[] words = text.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line.isEmpty() ? word : line + " " + word;
                if (fm.stringWidth(testLine) <= maxWidth) {
                    line.append(line.isEmpty() ? word : " " + word);
                } else {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                }
            }
            if (!line.isEmpty()) {
                lines.add(line.toString());
            }
            return lines;
        }
    }
}