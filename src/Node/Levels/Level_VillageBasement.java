package Node.Levels;

import Node.Dialog;
import Node.ImageNode;
import Node.Button;
import Node.*;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Util.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.*;
import java.util.List;


public class Level_VillageBasement extends Level {
    // level structure
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    private Player player;

    // 0: pull lever first time
    // 1: need to cut chains next
    // 2: unchained, can use lever
    // 3: can walk trough gate
    private int chainsBrokenState = 0;

    private RectangleCollider chainDestructionArea;
    private RectangleCollider leverArea;
    private RectangleCollider doorArea;
    private Button openGateButton;

    private MouseAdapter clickQuestMouseListener;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/villagebasement/BasementOpen.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/villagebasement/BackgroundBasementUnchained.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/villagebasement/BackgroundBasement.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "theBasement-Thinking",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "theBasement-Unchained",
                        Color.decode("#301c05"),
                        25
                )
        ));

        levelStructure.put("buttons", Arrays.asList(
                new Button(new int[]{200, 45}, new int[]{1185, 365}, Color.decode("#8b523e"), true, "Open Gate", 25, Color.decode("#c2a760"))
        ));

        colliders.add(new RectangleCollider(new int[]{275, 200}, new int[]{260, 500}, 2, false));
        colliders.add(new RectangleCollider(new int[]{225, 275}, new int[]{1185, 425}, 2, false));
        colliders.add(new RectangleCollider(new int[]{425, 220}, new int[]{730, 425}, 2, false));

        // outer wall colliders
        colliders.add(new RectangleCollider(new int[]{215, 1162}, new int[]{0, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{215, 1162}, new int[]{1697, 0}, 0, false));

        colliders.add(new RectangleCollider(new int[]{1482, 525}, new int[]{215, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{1482, 110}, new int[]{215, 1052}, 0, false));
        //
    }
    //

    public Level_VillageBasement(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        for (Node dialog: levelStructure.get("dialog")) {
            dialog.setVisibility(false);
        }

        chainDestructionArea = colliders.get(0);
        leverArea = colliders.get(1);
        doorArea = colliders.get(2);

        openGateButton = (Button) levelStructure.get("buttons").get(0);
        openGateButton.setVisibility(false);

        startLevelClock();

        basementRiddle();
    }

    private void basementRiddle() {
        int[] adjustedPlayerSize = new int[]{(int)(180 * 1.75), (int)(100 * 1.75)};
        player = new Player(adjustedPlayerSize, new int[]{800, 730});
        addChild(player);

        player.moveNodeToTop();
        LevelController.initializePlayer(player);

        openGateButton.moveNodeToTop();
        openGateButton.setOnPressed(button -> {
            if (openGateButton.isVisible()) {
                if (chainsBrokenState == 0) {
                    openGateButton.setVisibility(false);
                    chainsBrokenState = 1;

                    Dialog thinkingDialog = (Dialog) levelStructure.get("dialog").get(0);
                    thinkingDialog.setFinishDialogText("Hmmmmmm");

                    thinkingDialog.addDialogFinishedListener(finished -> {
                        thinkingDialog.setVisibility(false);
                    });

                    thinkingDialog.setVisibility(true);
                    thinkingDialog.moveNodeToTop();
                } else if (chainsBrokenState == 2) {
                    GameWindow.MainWindow.getContentPane().removeMouseListener(clickQuestMouseListener);

                    openGateButton.setVisibility(false);
                    chainsBrokenState = 3;

                    ImageNode closedBackground = (ImageNode)levelStructure.get("background").get(1);
                    closedBackground.setVisibility(false);

                    Dialog unchainedDialog = (Dialog) levelStructure.get("dialog").get(1);
                    unchainedDialog.setFinishDialogText("Lets move on");

                    unchainedDialog.addDialogFinishedListener(finished -> {
                        unchainedDialog.setVisibility(false);
                    });

                    unchainedDialog.setVisibility(true);
                    unchainedDialog.moveNodeToTop();
                }
            }
        });
    }

    public void _process() {
        if (chainsBrokenState == 1) {
            boolean unchaining = CollisionsHandler.isPlayerCollidingWithActionArea(chainDestructionArea.getPolygon()) && player.currentAnimation.equals("attack");
            if (unchaining) {
                chainsBrokenState = 2;
                ImageNode chainedBackground = (ImageNode)levelStructure.get("background").get(2);
                chainedBackground.setVisibility(false);
            }
            return; // unchain first

        } else if (chainsBrokenState == 3) {
            boolean isInGate = CollisionsHandler.isPlayerCollidingWithActionArea(doorArea.getPolygon());
            if (isInGate) {
                stopLevelClock();
                onLevelFinished.onFinished(levelID, 0);
            }
            return;
        }

        boolean collidingWithArea = CollisionsHandler.isPlayerCollidingWithActionArea(leverArea.getPolygon());
        openGateButton.setVisibility(collidingWithArea);
    }
}