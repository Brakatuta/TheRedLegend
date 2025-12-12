package Node.Levels;

import Node.Dialog;
import Node.ImageNode;
import Node.Button;
import Node.*;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Resources.Animations;
import Util.GameWindow;

import java.awt.*;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.util.*;
import java.util.List;


public class Level_Mineshaft extends Level {
    // level structure
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    static public final Map<List<Node>, List<RectangleCollider>> treeAreas = new LinkedHashMap<>();

    private Player player;

    private RectangleCollider mineshaftArea;
    private Button lightDynamiteButton;

    private Dialog startQuestDialog;
    private Dialog endQuestDialog;

    private int mineshaftQuestProgressIndex = 0;

    private QuestObjectCounterNode questItemCounter;

    private List<Integer> ignoreDynamiteAreaIndexes = new ArrayList<>();

    private String[] dynamiteExplosionFrames = Animations.ExplosionFrames.get("dynamite_explosion");

    private MouseAdapter clickQuestMouseListener;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/mineshaft/BackgroundMineshaftOpened.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/BackgroundMineshaftNoTrees.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("meme", Arrays.asList(
                new ImageNode("textures/backgrounds/mineshaft/mikaMeme.png", new int[]{94, 88}, new int[]{1780, 865})
        ));

        levelStructure.put("dynamite", Arrays.asList(
                new ImageNode("textures/items/Dynamite.png", new int[]{67, 54}, new int[]{517, 98}),
                new ImageNode("textures/items/Dynamite.png", new int[]{67, 54}, new int[]{155, 440}),
                new ImageNode("textures/items/Dynamite.png", new int[]{67, 54}, new int[]{211, 839}),
                new ImageNode("textures/items/Dynamite.png", new int[]{67, 54}, new int[]{619, 994}),
                new ImageNode("textures/items/Dynamite.png", new int[]{67, 54}, new int[]{1358, 1068})
        ));

        levelStructure.put("explosion", Arrays.asList(
                new Sprite(Animations.ExplosionFrames.get("dynamite_explosion"), new int[]{250, 250}, new int[]{993, 393}, new int[]{0, 0}, 100, false)
        ));

        levelStructure.put("player", Arrays.asList(
                new Player(new int[]{(int)(180 * 0.85), (int)(100 * 0.85)}, new int[]{45, 45})
        ));

        levelStructure.put("trees", Arrays.asList(
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree1.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree2.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree3.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree4.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree5.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree6.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree7.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree8.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/trees/Tree9.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "Forest-Narrator",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "Mineshaft-QuestStart",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "Mineshaft-QuestEnd",
                        Color.decode("#301c05"),
                        25
                )
        ));

        levelStructure.put("buttons", Arrays.asList(
                new Button(new int[]{200, 45}, new int[]{1050, 365}, Color.decode("#8b523e"), true, "Light Dynamite", 25, Color.decode("#c2a760"))
        ));

        // world colliders //
        colliders.add(new RectangleCollider(new int[]{222, 386}, new int[]{653, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{96, 440}, new int[]{875, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{941, 450}, new int[]{971, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{652, 245}, new int[]{1260, 450}, 0, false));
        colliders.add(new RectangleCollider(new int[]{218, 92}, new int[]{1436, 695}, 0, false));
        colliders.add(new RectangleCollider(new int[]{258, 182}, new int[]{1654, 695}, 0, false));
        // tree colliders
        colliders.add(new RectangleCollider(new int[]{90, 101}, new int[]{335, 51}, 0, false));
        colliders.add(new RectangleCollider(new int[]{92, 158}, new int[]{39, 572}, 0, false));
        colliders.add(new RectangleCollider(new int[]{92, 158}, new int[]{75, 994}, 0, false));
        colliders.add(new RectangleCollider(new int[]{92, 158}, new int[]{425, 802}, 0, false));
        colliders.add(new RectangleCollider(new int[]{92, 58}, new int[]{961, 1104}, 0, false));
        colliders.add(new RectangleCollider(new int[]{71, 207}, new int[]{1841, 877}, 0, false));
        //               //

        // action areas //
        // mineshaft collider
        colliders.add(new RectangleCollider(new int[]{345, 425}, new int[]{915, 305}, 2, false));

        // tree areas
        colliders.add(new RectangleCollider(new int[]{470, 128}, new int[]{183, 0}, 2, false)); // tree 8, 9
        colliders.add(new RectangleCollider(new int[]{217, 375}, new int[]{0, 253}, 2, false)); // tree 7
        colliders.add(new RectangleCollider(new int[]{250, 297}, new int[]{346, 503}, 2, false)); // tree 6
        colliders.add(new RectangleCollider(new int[]{250, 297}, new int[]{0, 719}, 2, false)); // tree 5
        colliders.add(new RectangleCollider(new int[]{339, 111}, new int[]{199, 1051}, 2, false)); // tree 4
        colliders.add(new RectangleCollider(new int[]{247, 183}, new int[]{538, 979}, 2, false)); // tree 4
        colliders.add(new RectangleCollider(new int[]{248, 245}, new int[]{883, 858}, 2, false)); // tree 3
        colliders.add(new RectangleCollider(new int[]{252, 137}, new int[]{1660, 800}, 2, false)); // tree 2
        colliders.add(new RectangleCollider(new int[]{252, 137}, new int[]{1335, 1025}, 2, false)); // tree 1

        // dynamite areas
        colliders.add(new RectangleCollider(new int[]{67, 54}, new int[]{517, 98}, 2, false));
        colliders.add(new RectangleCollider(new int[]{67, 54}, new int[]{155, 440}, 2, false));
        colliders.add(new RectangleCollider(new int[]{67, 54}, new int[]{211, 839}, 2, false));
        colliders.add(new RectangleCollider(new int[]{67, 54}, new int[]{619, 994}, 2, false));
        colliders.add(new RectangleCollider(new int[]{67, 54}, new int[]{1358, 1068}, 2, false));
        //             //

        // associate trees with their areas //
        List<Node> treeNodes = levelStructure.get("trees");

        treeAreas.put(
                Arrays.asList(treeNodes.get(8)), // Tree9
                Arrays.asList(colliders.get(13)) // Shared with Tree8
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(7)), // Tree8
                Arrays.asList(colliders.get(13)) // Shared with Tree9
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(6)), // Tree7
                Arrays.asList(colliders.get(14))
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(5)), // Tree6
                Arrays.asList(colliders.get(15))
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(4)), // Tree5
                Arrays.asList(colliders.get(16))
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(3)), // Tree4
                Arrays.asList(colliders.get(17), colliders.get(18)) // Two colliders
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(2)), // Tree3
                Arrays.asList(colliders.get(19))
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(1)), // Tree2
                Arrays.asList(colliders.get(20))
        );

        treeAreas.put(
                Arrays.asList(treeNodes.get(0)), // Tree1
                Arrays.asList(colliders.get(21))
        );
        //                                  //

        levelStructure.put("narrator_background", Arrays.asList(
                new ImageNode("textures/backgrounds/mineshaft/world/BackgroundForestDense.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/world/BackgroundForestFields.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/world/BackgroundForestMountains.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/world/BackgroundForest.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/mineshaft/world/BackgroundForestDense.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));
    }
    //

    public Level_Mineshaft(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        TransitionNode transitionNode = (TransitionNode)levelStructure.get("transition").get(0);

        Sprite explosionEffect = (Sprite) levelStructure.get("explosion").get(0);
        explosionEffect.setVisibility(false);

        Dialog narratorDialog = (Dialog)levelStructure.get("dialog").get(0);
        List<Node> worldBackgrounds = levelStructure.get("narrator_background");

        narratorDialog.addDialogProgressListener(progress -> {
            switch (progress) {
                case 3:
                    worldBackgrounds.get(4).setVisibility(false);
                    break;
                case 4:
                    worldBackgrounds.get(3).setVisibility(false);
                    break;
                case 5:
                    worldBackgrounds.get(2).setVisibility(false);
                    break;
                case 6:
                    worldBackgrounds.get(1).setVisibility(false);
                    break;
            }
        });

        narratorDialog.addDialogFinishedListener(finished -> {
            loadMineshaftArea();
            transitionNode.startInOutTransition(Color.black, 1.0f, 1000);
            Timer transitionMidTimer = new Timer(500, completed -> {
                narratorDialog.removeFromParent();
                worldBackgrounds.get(0).setVisibility(false);
            });
            transitionMidTimer.setRepeats(false);
            transitionMidTimer.start();
        });

        transitionNode.setBackgroundColor(Color.black);
        transitionNode.setAlpha(0.0f);

        narratorDialog.moveNodeToTop();
    }

    private void loadMineshaftArea() {
        startQuestDialog = (Dialog)levelStructure.get("dialog").get(1);
        endQuestDialog = (Dialog)levelStructure.get("dialog").get(2);

        startQuestDialog.setVisibility(false);
        endQuestDialog.setVisibility(false);

        startQuestDialog.addDialogProgressListener(progress -> {
            if (progress == 3) {
                startQuestDialog.setFinishDialogText("Start searching");
            } else if (progress == 4) {
                questItemCounter = new QuestObjectCounterNode(
                        "textures/items/Dynamite.png",
                        5,
                        new int[]{250, 100},
                        new int[]{GameWindow.WINDOW_SIZE.width / 2, 75}
                );
                addChild(questItemCounter);
            }
        });

        endQuestDialog.addDialogProgressListener(progress -> {
            if (progress == 2) {
                endQuestDialog.setFinishDialogText("Let's go");
            } else if (progress == 3) {
                mineshaftQuestProgressIndex = 3;
            }
        });

        player = (Player)levelStructure.get("player").get(0);
        mineshaftArea = colliders.get(12);

        lightDynamiteButton = (Button) levelStructure.get("buttons").get(0);
        lightDynamiteButton.setVisibility(false);

        startLevelClock();

        MineshaftRiddle();
    }

    private void MineshaftRiddle() {
        Timer playerInitDelay = new Timer(750, finished -> {
            LevelController.initializePlayer(player);
        });
        playerInitDelay.setRepeats(false);
        playerInitDelay.start();

        lightDynamiteButton.moveNodeToTop();
        lightDynamiteButton.setOnPressed(button -> {
            if (lightDynamiteButton.isVisible()) {
                GameWindow.MainWindow.getContentPane().removeMouseListener(clickQuestMouseListener);
                stopLevelClock();
                lightDynamiteButton.setVisibility(false);
                blowUpRubbleAndFreeMineshaft();
            }
        });
    }

    public void _process() {
        updateTreesOpacity();

        testForDynamiteCollecting();

        boolean collidingWithMineshaftArea = CollisionsHandler.isPlayerCollidingWithActionArea(mineshaftArea.getPolygon());

        if (collidingWithMineshaftArea) {
            if (mineshaftQuestProgressIndex == 0 && !startQuestDialog.isVisible()) {
                mineshaftQuestProgressIndex = 1;
                startQuestDialog.setVisibility(true);
            } else if (mineshaftQuestProgressIndex == 2) {
                endQuestDialog.setVisibility(true);
            } else if (mineshaftQuestProgressIndex == 3) {
                lightDynamiteButton.setVisibility(collidingWithMineshaftArea);
            }
        }
    }

    private void testForDynamiteCollecting() {
        if (mineshaftQuestProgressIndex != 1) {
            return;
        }

        int colliderToRemoveIndex = -1;

        for (int dynamiteColliderIndex = 22; dynamiteColliderIndex <= 26; dynamiteColliderIndex++) {
            if (ignoreDynamiteAreaIndexes.contains(dynamiteColliderIndex)) {
                continue;
            }

            RectangleCollider dynamiteCollider = colliders.get(dynamiteColliderIndex);
            if (CollisionsHandler.isPlayerCollidingWithActionArea(dynamiteCollider.getPolygon())) {
                colliderToRemoveIndex = dynamiteColliderIndex;
            }
        }

        if (colliderToRemoveIndex >= 0) {
            ignoreDynamiteAreaIndexes.add(colliderToRemoveIndex);
            RectangleCollider colliderToRemove = colliders.get(colliderToRemoveIndex);
            colliderToRemove.removeFromParent();
            levelStructure.get("dynamite").get(colliderToRemoveIndex - 22).setVisibility(false);
            questItemCounter.updateQuestItemCounter();

            if (questItemCounter.collectedAmount == questItemCounter.neededAmount) {
                mineshaftQuestProgressIndex = 2;
            }
        }
    }

    private void updateTreesOpacity() {
        for (Map.Entry<List<Node>, List<RectangleCollider>> entry : treeAreas.entrySet()) {
            List<Node> treeNodes = entry.getKey();
            List<RectangleCollider> treeColliders = entry.getValue();

            boolean playerInsideAnyCollider = false;

            // Check if player is inside any of the colliders for this tree area
            for (RectangleCollider collider : treeColliders) {
                if (CollisionsHandler.isPlayerCollidingWithActionArea(collider.getPolygon())) {
                    playerInsideAnyCollider = true;
                    break;
                }
            }

            // Update visibility of all nodes in this tree area based on player presence
            for (Node treeNode : treeNodes) {
                ImageNode treeImage = (ImageNode)treeNode;
                double newAlphaLevel = 1.0;

                if (playerInsideAnyCollider) {
                    newAlphaLevel = 0.5;
                }

                if (newAlphaLevel != treeImage.getAlphaValue()) {
                    treeImage.setAlphaValue(newAlphaLevel);
                }
            }
        }
    }

    private void blowUpRubbleAndFreeMineshaft() {
        levelStructure.get("dynamite").get(0).setPosition(new int[]{1067, 443});
        levelStructure.get("dynamite").get(1).setPosition(new int[]{1017, 505});
        levelStructure.get("dynamite").get(2).setPosition(new int[]{1084, 505});
        levelStructure.get("dynamite").get(3).setPosition(new int[]{1137, 465});
        levelStructure.get("dynamite").get(4).setPosition(new int[]{1140, 546});

        for (Node dynamiteNode: levelStructure.get("dynamite")) {
            dynamiteNode.setVisibility(true);
        }

        Timer explosionDelay = new Timer(1250, event -> {
            for (Node dynamiteNode: levelStructure.get("dynamite")) {
                dynamiteNode.setVisibility(false);
            }
            Sprite explosionEffect = (Sprite)levelStructure.get("explosion").get(0);
            explosionEffect.setVisibility(true);
            explosionEffect.switchToAnimation(dynamiteExplosionFrames, new int[]{250, 250}, new int[]{993, 393}, 150, false);

            Timer backgroundSwitchTimer = new Timer(5 * 150 + 100, finished -> {
                levelStructure.get("background").get(1).setVisibility(false);
            });
            backgroundSwitchTimer.setRepeats(false);
            backgroundSwitchTimer.start();

            Timer explosionEffectCleaner = new Timer(6 * 150 + 1000, finished -> {
                explosionEffect.setVisibility(false);
                onLevelFinished.onFinished(levelID, 0);
            });
            explosionEffectCleaner.setRepeats(false);
            explosionEffectCleaner.start();
        });

        explosionDelay.setRepeats(false);
        explosionDelay.start();
    }
}