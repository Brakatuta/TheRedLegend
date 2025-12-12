package Node.Levels;

import Node.Dialog;
import Node.ImageNode;
import Node.Button;
import Node.*;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Resources.Animations;
import Util.ExtendedMath;
import Util.GameWindow;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


public class Level_TheBeginning extends Level {
    // level structure
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    private ArrayList<Sprite> fireflies = new ArrayList<>(){};

    private ImageNode nightOverlay;
    private TransitionNode transitionNode;
    private Dialog dialog;

    private Player player;

    private MouseAdapter clickQuestMouseListener;

    private int clickProgressIndex = 0;

    private RectangleCollider escapeTriggerArea;
    private Button escapeButton;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/VillageBackgroundDay.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackgroundNight.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/light/NightOverlay.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("lights", Arrays.asList(
                new Sprite(
                        Animations.SpotLightFrames.get("dim_flicker"),
                        new int[]{128, 128},
                        new int[]{1600, 650},
                        new int[]{0, 0},
                        400,
                        true
                ),

                new Sprite(
                        Animations.SpotLightFrames.get("dim_flicker"),
                        new int[]{128, 128},
                        new int[]{1715, 650},
                        new int[]{0, 0},
                        400,
                        true
                )
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "theBeginning",
                        Color.decode("#301c05"),
                        25
                )
        ));

        levelStructure.put("buttons", Arrays.asList(
               new Button(new int[]{200, 45}, new int[]{1640, 895}, Color.decode("#8b523e"), true, "Escape Village", 25, Color.decode("#c2a760"))
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));

        colliders.add(new RectangleCollider(new int[]{150, 200}, new int[]{1675, 950}, 2, false));
    }
    //

    public Level_TheBeginning(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        nightOverlay = (ImageNode)levelStructure.get("background").get(2);

        transitionNode = (TransitionNode)levelStructure.get("transition").get(0);
        transitionNode.setBackgroundColor(Color.BLACK);
        transitionNode.setAlpha(0.0f);

        escapeTriggerArea = colliders.get(0);
        escapeButton = (Button)levelStructure.get("buttons").get(0);

        escapeButton.setVisibility(false);

        dialog = (Dialog)levelStructure.get("dialog").get(0);
        dialog.moveNodeToTop();

        dialog.addDialogProgressListener(progress -> {
            switch (progress) {
                case 8:
                    dialog.setVisibility(false);

                    transitionEffect(3000);

                    Timer nightSwitchDelayTimer = new Timer(1500, event -> {
                        setNightState(true);
                        setFireFlies(true);
                    });

                    nightSwitchDelayTimer.setRepeats(false);
                    nightSwitchDelayTimer.start();
                    break;
                case 27:
                    dialog.setVisibility(false);
                    transitionNode.moveNodeToTop();

                    transitionEffect(1000);

                    Timer levelDialogDecissionTimer = new Timer(500, event -> {
                        System.out.println(dialog.lastChosenOption);
                        if (dialog.lastChosenOption == 0) {
                            setFireFlies(false);
                        } else if (dialog.lastChosenOption == 2) {
                            setNightState(false);
                            setFireFlies(false);
                        }
                    });

                    levelDialogDecissionTimer.setRepeats(false);
                    levelDialogDecissionTimer.start();
                    break;
                case 30:
                    switch (dialog.lastChosenOption) {
                        case 0:
                            // continue here with break out riddle
                            setFireFlies(false);
                            breakoutRiddle();
                            break;
                        case 1:
                            // failed dialog -> end game
                            System.exit(0);
                        case 2:
                            // continue to next level
                            onLevelFinished.onFinished(levelID, dialog.lastChosenOption);
                            break;
                    }
                    break;
            }
        });

        setNightState(false);

        // testing purposes only
        //Timer riddleDebugTimer = new Timer(3000, event -> {
        //    breakoutRiddle();
        //});
        //riddleDebugTimer.setRepeats(false);
        //riddleDebugTimer.start();
        //
    }

    private void transitionEffect(int transitionDuration) {
        transitionNode.moveNodeToTop();

        transitionNode.setOnTransitionFinished(new TransitionNode.TransitionListener() {
            @Override
            public void onFinished(TransitionNode node) {
                // Disconnect to avoid looping
                transitionNode.removeTransitionFinishedListener();
                if (dialog != null) {
                    dialog.moveNodeToTop();
                    dialog.setVisibility(true);
                }
            }
        });

        transitionNode.startInOutTransition(Color.BLACK, 1.0f, transitionDuration);
    }

    private void setNightState(boolean state) {
        int nightBackgroundNodeIndex = 1;

        while (nightBackgroundNodeIndex < 3) {
            ImageNode backgroundNode = (ImageNode)levelStructure.get("background").get(nightBackgroundNodeIndex);
            backgroundNode.setVisibility(state);
            nightBackgroundNodeIndex++;
        }

        for (Node lightNode: levelStructure.get("lights")) {
            Sprite light = (Sprite)lightNode;
            light.setVisibility(state);
        }
    }

    private void setFireFlies(boolean state) {
        if (state) {
            String[] firefliesFrames = Animations.AmbientFrames.get("fireflies");

            Sprite firefliesSprite0 = new Sprite(firefliesFrames, new int[]{644, 408}, new int[]{100, 800}, new int[]{0, 0}, 200, true);
            addChild(firefliesSprite0);
            fireflies.add(firefliesSprite0);

            Sprite firefliesSprite1 = new Sprite(firefliesFrames, new int[]{644, 408}, new int[]{600, 900}, new int[]{0, 0}, 200, true);
            addChild(firefliesSprite1);
            fireflies.add(firefliesSprite1);
        } else {
            for (Sprite firefliesNode: fireflies) {
                System.out.println(firefliesNode);
                firefliesNode.setVisibility(false);
                removeChild(firefliesNode);
            }

            fireflies.clear();
        }
    }

    private void breakoutRiddle() {
        removeChild(dialog);

        player = new Player(new int[]{180, 100}, new int[]{1700, 730});
        addChild(player);

        player.moveNodeToTop();
        nightOverlay.moveNodeToTop();

        ArrayList<int[]> markerPositions = new ArrayList<>();

        markerPositions.add(new int[]{1200, 820});
        markerPositions.add(new int[]{600, 1100});

        spawnPositionMarkersSequenced(markerPositions, 500, 5000);

        clickQuestMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent clickEvent) {
                Point clickPoint = clickEvent.getPoint();
                switch (clickProgressIndex) {
                   case 0:
                       if (ExtendedMath.getDistanceBetweenPositions(new int[]{clickPoint.x, clickPoint.y}, new int[]{1200, 820}) < 150) {
                           updatePlayerPosition(new int[]{1200, 780});
                       }
                       break;
                    case 1:
                        if (ExtendedMath.getDistanceBetweenPositions(new int[]{clickPoint.x, clickPoint.y}, new int[]{600, 1100}) < 150) {
                            updatePlayerPosition(new int[]{680, 1070});

                            Timer delayTimer = new Timer(500, event -> {
                                LevelController.initializePlayer(player);
                                player.canMoveHorizontalOnly = true;
                                startLevelClock();
                            });

                            delayTimer.setRepeats(false);
                            delayTimer.start();
                        }
                        break;
                }
            }
        };

        GameWindow.MainWindow.getContentPane().addMouseListener(clickQuestMouseListener);

        escapeButton.moveNodeToTop();
        escapeButton.setOnPressed(button -> {
            if (escapeButton.isVisible()) {
                GameWindow.MainWindow.getContentPane().removeMouseListener(clickQuestMouseListener);
                stopLevelClock();
                escapeTriggerArea.removePolygonFromCollisionGroup();
                escapeButton.setVisibility(false);
                onLevelFinished.onFinished(levelID, dialog.lastChosenOption);
            }
        });
    }

    private void updatePlayerPosition(int[] positionToGoTo) {
        player.onClickMovePlayer(positionToGoTo);
        clickProgressIndex++;
    }

    public void _process() {
        if (clickProgressIndex != 2) {
            return;
        }

        boolean collidingWithArea = CollisionsHandler.isPlayerCollidingWithActionArea(escapeTriggerArea.getPolygon());
        escapeButton.setVisibility(collidingWithArea);
    }
}