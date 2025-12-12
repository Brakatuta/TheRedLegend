package Node;

import javax.swing.*;
import java.awt.*;


public class QuestObjectCounterNode extends Node {
    private final LabelNode counterLabel;

    public int neededAmount = 0;
    public int collectedAmount = 0;

    public QuestObjectCounterNode(String imagePath, int itemAmount, int[] size, int[] relativePosition) {
        super(size, relativePosition, Color.white, false);

        Node backFill = new Node(size, new int[]{0, 0}, Color.decode("#e0ab6e"), true);
        addChild(backFill);

        Node frontFill = new Node(new int[]{size[0] - 15, size[1] - 15}, new int[]{15 / 2, 15 / 2}, Color.decode("#916b3f"), true);
        backFill.addChild(frontFill);

        ImageNode questItem = new ImageNode(imagePath, new int[]{size[1] - 10, size[1] - 10}, new int[]{5, 5});
        frontFill.addChild(questItem);

        neededAmount = itemAmount;
        String counterText = 0 + "/" + neededAmount;

        counterLabel = new LabelNode(
                counterText,
                new int[]{35, 5},
                new int[]{size[0] - 25, size[1] - 25},
                Color.decode("#301c05"),
                45
        );
        frontFill.addChild(counterLabel);
    }

    public void updateQuestItemCounter() {
        collectedAmount++;

        counterLabel.setText(collectedAmount + "/" + neededAmount);

        if (collectedAmount == neededAmount) {
            Timer removalTimer = new Timer(1500, event -> {
                removeFromParent();
            });
            removalTimer.setRepeats(false);
            removalTimer.start();
        }
    }
}