package Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;


public class Button extends Node {

    final JLabel label;
    private Consumer<Button> onPressed; // pressed event hook

    public Button(int[] size, int[] relativePosition, Color backgroundColor, boolean opaque, String text, int textSize, Color textColor) {
        super(size, relativePosition, backgroundColor, opaque);

        label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(textColor);
        label.setFont(new Font("Arial", Font.BOLD, textSize));
        label.setBounds(0, 0, size[0], size[1]);
        label.setOpaque(false);

        panel.setLayout(null);
        panel.add(label);

        // Add mouse listener for press event
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (onPressed != null) {
                    onPressed.accept(Button.this);
                }
            }
        });
    }

    // Getter and setter for text
    public void setText(String text) {
        label.setText(text);
    }

    public String getText() {
        return label.getText();
    }

    // Getter and setter for text size
    public void setTextSize(int size) {
        label.setFont(label.getFont().deriveFont((float) size));
    }

    public int getTextSize() {
        return label.getFont().getSize();
    }

    // Getter and setter for text color
    public void setTextColor(Color color) {
        label.setForeground(color);
    }

    public Color getTextColor() {
        return label.getForeground();
    }

    // Set the pressed event callback
    public void setOnPressed(Consumer<Button> callback) {
        this.onPressed = callback;
    }

    public void removeOnPressed() {
        this.onPressed = null;
    }
}
