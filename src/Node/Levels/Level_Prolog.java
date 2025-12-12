package Node.Levels;

import Node.ImageNode;
import Node.*;
import Physics.RectangleCollider;
import Resources.Animations;
import Util.Tween;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.Color;
import java.util.*;


public class Level_Prolog extends Level {
    // level structure
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    static double DRAGON_SCALE = 2.5;

    // for village loop spawning
    private int currentBackgroundIndex = 2;
    private static Timer backgroundSpawnTimer;
    //

    private static boolean inVillageDragonAnimation = false;

    private TransitionNode transitionNode;

    private int animationPointIndex = 0;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/MountainRangeScenery.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/maps/UtopiriaMap.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackground2.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackground3.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackground4.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackground5.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));

        // initial dragon spawn
        levelStructure.put("dragon", new ArrayList<Node>());

        levelStructure.put("flames", new ArrayList<Node>());

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "Prolog",
                        Color.decode("#301c05"),
                        25
                )
        ));
    }
    //

    public Level_Prolog(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        for (int i = 1; i < levelStructure.get("background").size(); i++) {
            ImageNode background = (ImageNode)levelStructure.get("background").get(i);
            background.setVisibility(false);
        }

        transitionNode = (TransitionNode)levelStructure.get("transition").get(0);

        transitionNode.setBackgroundColor(Color.black);
        transitionNode.setAlpha(1.0f);

        transitionNode.startTransition(Color.black, 0.0f, 1000);

        ImageNode mapNode = (ImageNode)levelStructure.get("background").get(1);

        Dialog dialog = (Dialog)levelStructure.get("dialog").get(0);
        dialog.addDialogProgressListener(progress -> {
            switch (progress) {
                case 3:
                    ImageNode village = (ImageNode)levelStructure.get("background").get(currentBackgroundIndex);
                    village.setVisibility(true);
                    flyDragonTroughVillage(dialog, currentBackgroundIndex, 0.25);

                    Timer delayTweenTimer = new Timer(3750, finished -> {
                        transitionNode.moveNodeToTop();
                        transitionNode.startInOutTransition(Color.black, 1.0f, 1000);
                    });

                    delayTweenTimer.setRepeats(false);
                    delayTweenTimer.start();

                    Timer delayDialog = new Timer(4500, finished -> {
                        dialog.setVisibility(true);
                    });

                    delayDialog.setRepeats(false);
                    delayDialog.start();

                    break;
                case 4:
                    mapNode.moveNodeToTop();
                    dialog.moveNodeToTop();
                    mapNode.setVisibility(true);
                    break;
                case 5:
                    mapNode.setVisibility(false);
                    mapNode.destroy();
                    break;
                case 7:
                    currentBackgroundIndex++;
                    backgroundSpawnTimer = new Timer(16, event -> spawnVillageFromLoop(dialog));
                    backgroundSpawnTimer.start();

                    delayTweenTimer = new Timer(13500, finished -> {
                        transitionNode.moveNodeToTop();
                        transitionNode.startInOutTransition(Color.black, 1.0f, 1000);
                    });

                    delayTweenTimer.setRepeats(false);
                    delayTweenTimer.start();

                    delayDialog = new Timer(14000, finished -> {
                        dialog.setVisibility(true);
                    });

                    delayDialog.setRepeats(false);
                    delayDialog.start();

                    break;
            }
        });

        dialog.addDialogFinishedListener(lastChosenOption -> {
            onLevelFinished.onFinished(levelID, lastChosenOption);
        });
    }

    public void spawnVillageFromLoop(Dialog dialog) {
        if (currentBackgroundIndex > 5) {
            backgroundSpawnTimer.stop();
            return;
        }

        if (!inVillageDragonAnimation) {
            inVillageDragonAnimation = true;

            int finalBackgroundIndex = currentBackgroundIndex;
            currentBackgroundIndex++; // increment AFTER passing to thread

            ImageNode village = (ImageNode)levelStructure.get("background").get(finalBackgroundIndex);
            village.setVisibility(true);

            Timer delayTimer = new Timer(300, finished -> {
                flyDragonTroughVillage(dialog, finalBackgroundIndex, 0.35);
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    public void flyDragonTroughVillage(Dialog dialog, int backgroundIndex, double dragonFlightSpeedMultiplier) { // lower multiplier number -> faster dragon
        dialog.setVisibility(false);

        levelStructure.get("dragon").clear();
        levelStructure.get("dragon").add(
                new Sprite(
                        Animations.FireDragonFrames.get("fly"),
                        new int[]{(int)(200.0 * DRAGON_SCALE), (int)(160.0 * DRAGON_SCALE)},
                        new int[]{0, 0},
                        new int[]{0, 0},
                        333,
                        true
                )
        );

        ImageNode village = (ImageNode)levelStructure.get("background").get(backgroundIndex);

        Sprite dragon = (Sprite)levelStructure.get("dragon").get(0);
        addChild(dragon);

        dragon.setRotation(45);
        dragon.setPosition(new int[]{0, 0});

        dragon.moveNodeToTop();
        dialog.moveNodeToTop();

        Tween dragonTween = new Tween();
        dragonTween.interpolatePosition(dragon, new int[]{450, 500}, 2.0 * dragonFlightSpeedMultiplier);

        // animations
        dragonTween.addTweenListener(() -> {
            switch (animationPointIndex) {
                case 0:
                    dragon.setRotation(-5);
                    dragonTween.interpolatePosition(dragon, new int[]{800, 600}, dragonFlightSpeedMultiplier);
                    animationPointIndex++;
                    break;
                case 1:
                    dragon.setRotation(-35);
                    dragonTween.interpolatePosition(dragon, new int[]{1900, 0}, 3.0 * dragonFlightSpeedMultiplier);
                    animationPointIndex++;
                    break;
                case 2:
                    int amountFlames1 = Math.max(4, (int)(Math.random() * 10));
                    int amountFlames2 = Math.max(4, (int)(Math.random() * 10));

                    for (int i = 0; i < (amountFlames1 + amountFlames2); i++) {
                        Sprite flame;
                        double randomFlameScaleMultiplier = Math.max(0.35, Math.random());
                        int[] randomFlamePosition = new int[]{(int)(Math.random() * 1800), (int)(Math.max(150, (Math.random() * 400)) * (1.0 / randomFlameScaleMultiplier))};

                        if (i <= amountFlames1) { // flames 1
                            flame = new Sprite(
                                    Animations.FireFlamesFrames.get("flames1"),
                                    new int[]{(int)(276 * randomFlameScaleMultiplier), (int)(728 * randomFlameScaleMultiplier)},
                                    randomFlamePosition,
                                    new int[]{0, 0},
                                    1000 / 8,
                                    true
                            );

                        } else {                 // flames 2
                            flame = new Sprite(
                                    Animations.FireFlamesFrames.get("flames2"),
                                    new int[]{(int)(264 * randomFlameScaleMultiplier), (int)(743 * randomFlameScaleMultiplier)},
                                    randomFlamePosition,
                                    new int[]{0, 0},
                                    1000 / 8,
                                    true
                            );
                        }

                        getParent().addChild(flame);
                        levelStructure.get("flames").add(flame);
                    }

                    dialog.moveNodeToTop();

                    Timer cleanupTimer = new Timer(1800, event -> {
                        levelStructure.get("dragon").get(0).removeFromParent();
                        for (Node flame : levelStructure.get("flames")) {
                            flame.removeFromParent();
                        }
                        levelStructure.get("flames").clear();

                        village.setVisibility(false);
                        village.removeFromParent();
                        village.destroy();
                        levelStructure.get("background").set(backgroundIndex, null);

                        animationPointIndex = 0;
                        inVillageDragonAnimation = false;

                        if (currentBackgroundIndex <= 5) {
                            ImageNode nextVillage = (ImageNode) levelStructure.get("background").get(currentBackgroundIndex);
                            if (nextVillage != null) {
                                nextVillage.setVisibility(true);

                                transitionNode.moveNodeToTop();
                                transitionNode.startInOutTransition(Color.black, 1.0f, 450);
                            }
                        }
                    });

                    cleanupTimer.setRepeats(false);
                    cleanupTimer.start();
                    break;
            }
        });
    }
}