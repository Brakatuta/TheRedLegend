package Node;

import Util.ExtendedMath;
import Util.Globals;

import javax.swing.*;
import java.awt.*;


public class PlayerHealthNode extends Node {
    static int PLAYER_HEALTH = 100;
    private int currentPlayerHealth = 100;

    private final int[] healthBarSize = new int[]{(int)(640 * 0.8), 200};

    public PlayerHealthNode() {
        super(new int[]{(int)(640 * 0.8), 200}, new int[]{-10, 65}, Color.white, false);

        Node healthBarBackground = new Node(new int[]{(int)(481 * 0.8), (int)(38 * 0.8)}, new int[]{(int)(122 * 0.8), (int)(42 * 0.8)}, new Color(129, 145, 150, 255), true);
        addChild(healthBarBackground);

        Node healthBarInnerPart = new Node(new int[]{(int)(481 * 0.8), (int)(38 * 0.8)}, new int[]{(int)(122 * 0.8), (int)(42 * 0.8)}, new Color(236, 46, 46, 255), true);
        addChild(healthBarInnerPart);

        ImageNode healthBarCover = new ImageNode("textures/PlayerHealthBar.png", new int[]{(int)(640 * 0.8), (int)(117 * 0.8)}, new int[]{0, 0});
        addChild(healthBarCover);

        LabelNode HealthLabel = new LabelNode(currentPlayerHealth + "/" + PLAYER_HEALTH, new int[]{0, 20}, healthBarSize, new Color(236, 46, 46, 255), 25);
        addChild(HealthLabel);

        updatePlayerHealthBar();
    }

    public void damagePlayer(int damage) {
        currentPlayerHealth = (int)ExtendedMath.clamp(currentPlayerHealth - (damage * ((double)PLAYER_HEALTH / (double) Globals.currentHealthMax)), 0, PLAYER_HEALTH);
    }

    public int getHealth() {
        return currentPlayerHealth;
    }

    public void updatePlayerHealthBar() {
        Node progressPart = getAllChildren().get(1);
        LabelNode HealthLabel = (LabelNode)getAllChildren().get(3);

        double progress = (481 * 0.8) * ((double)currentPlayerHealth / PLAYER_HEALTH);
        progress = ExtendedMath.clamp(progress, 0, 481 * 0.8);
        progressPart.setSize((int)progress, (int)(38 * 0.8));

        HealthLabel.setText(currentPlayerHealth + "/" + PLAYER_HEALTH);
    }

    public void resetHealth() {
        currentPlayerHealth = PLAYER_HEALTH;
    }
}
