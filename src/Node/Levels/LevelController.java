package Node.Levels;

import Node.*;
import Node.Button;

import Physics.RectangleCollider;
import Util.GameWindow;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LevelController {
    public static Player currentPlayer;

    public static Node currentLevel;

    private static MouseListener playerMouseListener;
    private static KeyListener playerKeyListener;

    public static void loadLevel(Node levelRoot, Map<String, List<Node>> levelStructure, List<RectangleCollider> colliders) {
        currentLevel = levelRoot;
        // Load level data
        for (Map.Entry<String, List<Node>> entry : levelStructure.entrySet()) {
            List<Node> layerNodes = entry.getValue();

            for (Node node : layerNodes) {
                levelRoot.addChild(node);

                if (node instanceof Player playerNode) {
                    initializePlayer(playerNode);
                }
            }
        }

        // Load collisions
        for (RectangleCollider collider : colliders) {
            levelRoot.addChild(collider);
        }
    }

    public static void unloadLevel(Node levelRoot) {
        List<Node> childrenCopy = new ArrayList<>(levelRoot.getAllChildren());

        for (Node child : childrenCopy) {
            if (child instanceof RectangleCollider rectCollider) { // remove old colliders
                rectCollider.removePolygonFromCollisionGroup();
            } else if (child instanceof Button button) {
                button.removeOnPressed();
            } else if (child instanceof PlayerInventory inventory) {
                inventory.removeInventoryListener();
            }

            levelRoot.removeChild(child);
        }

        currentLevel.destroy();
        currentLevel = null;

        System.gc();
    }

    public static void initializePlayer(Player player) {
        currentPlayer = player;
        currentPlayer.setVisibility(true);

        playerMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                Point clickPoint = event.getPoint();
                currentPlayer.onClickMovePlayer(new int[]{clickPoint.x, clickPoint.y});
            }
        };

        playerKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_F) {
                    currentPlayer.playAttackAnimation();
                }
            }
        };

        GameWindow.MainWindow.getContentPane().addMouseListener(playerMouseListener);
        GameWindow.MainWindow.addKeyListener(playerKeyListener);
    }

    public static void killCurrentPlayer() {
        if (currentPlayer != null) {
            currentPlayer.removeFromParent();
            currentPlayer.destroy();
            currentPlayer = null;
        }

        if (playerMouseListener != null) {
            GameWindow.MainWindow.getContentPane().removeMouseListener(playerMouseListener);
            playerMouseListener = null;
        }

        if (playerKeyListener != null) {
            GameWindow.MainWindow.removeKeyListener(playerKeyListener);
            playerKeyListener = null;
        }
    }
}
