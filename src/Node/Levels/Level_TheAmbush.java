package Node.Levels;

import Node.*;
import Node.Dialog;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Util.ExtendedMath;
import Util.GameWindow;
import Util.Globals;

import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class Level_TheAmbush extends Level {
    // level structure
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    private Player player;
    private PlayerInventory inventoryWindow;

    private int actionTriggerState = 0;

    private RectangleCollider barbariansTriggerCollider;
    private RectangleCollider escapeAmbushCollider;

    private Dialog ambushEncounterDialog;
    private Dialog ambushEncounterDialog2;
    private Dialog defeatedAmbushDialog;

    private PlayerHealthNode healthBar;

    private boolean barbariansAIActiveState = false;

    private boolean damageable = true;

    private final int BARBARIANS_NEEDED_TO_KO = 7;
    public int barbariansKOd = 0;

    private QuestObjectCounterNode KOCounter;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/PlanesBattlefieldBackground.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("player", Arrays.asList(
                new Player(new int[]{(int)(180 * 0.85), (int)(100 * 0.85)}, new int[]{45, 45})
        ));

        levelStructure.put("barbarians", Arrays.asList(
                new Barbarian(new int[]{1912, 1144}),
                new Barbarian(new int[]{2073, 1320}),
                new Barbarian(new int[]{2215, 1279}),
                new Barbarian(new int[]{2305, 1168}),
                new Barbarian(new int[]{2313, 1400}),
                new Barbarian(new int[]{2423, 1245}),
                new Barbarian(new int[]{2484, 1400})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheAmbush-Introduction",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheAmbush-TheStart",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheAmbush-Trolls",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheAmbush-FightWon",
                        Color.decode("#301c05"),
                        25
                )
        ));

        // world colliders //
        colliders.add(new RectangleCollider(new int[]{17, 1162}, new int[]{0, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{17, 1162}, new int[]{1895, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{1878, 18}, new int[]{17, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{1878, 19}, new int[]{17, 1144}, 0, false));
        //                 //

        colliders.add(new RectangleCollider(new int[]{990, 790}, new int[]{932, 372}, 2, false));
        colliders.add(new RectangleCollider(new int[]{329, 374}, new int[]{1583, 788}, 2, false));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));
    }
    //

    public Level_TheAmbush(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        Dialog introductionDialog = (Dialog)levelStructure.get("dialog").get(0);
        introductionDialog.setVisibility(false);

        ambushEncounterDialog = (Dialog)levelStructure.get("dialog").get(1);
        ambushEncounterDialog.setVisibility(false);

        ambushEncounterDialog2 = (Dialog)levelStructure.get("dialog").get(2);
        ambushEncounterDialog2.setVisibility(false);

        defeatedAmbushDialog = (Dialog)levelStructure.get("dialog").get(3);
        defeatedAmbushDialog.setVisibility(false);

        TransitionNode transitionNode = (TransitionNode) levelStructure.get("transition").get(0);

        transitionNode.setBackgroundColor(Color.black);
        transitionNode.setAlpha(1.0f);

        transitionNode.startTransition(Color.black, 0.0f, 1000);
        transitionNode.setOnTransitionFinished(finished -> {
            player = (Player)levelStructure.get("player").get(0);
            LevelController.initializePlayer(player);
            introductionDialog.setVisibility(true);
            transitionNode.removeTransitionFinishedListener();
        });

        inventoryWindow = new PlayerInventory();
        addChild(inventoryWindow);

        introductionDialog.addDialogFinishedListener(finished -> {
            ArrayList<int[]> markerPositions = new ArrayList<>();

            markerPositions.add(new int[]{390, 390});
            markerPositions.add(new int[]{660, 560});
            markerPositions.add(new int[]{980, 720});

            spawnPositionMarkersSequenced(markerPositions, 500, 5000);

            barbariansTriggerCollider = colliders.get(4);
            escapeAmbushCollider = colliders.get(5);
            startLevelClock();
        });
    }

    public void _process() {
        switch (actionTriggerState) {
            case 0:
                boolean collidingWithBarbariansTriggerArea = CollisionsHandler.isPlayerCollidingWithActionArea(barbariansTriggerCollider.getPolygon());

                if (collidingWithBarbariansTriggerArea) {
                    barbariansEncounter();
                    actionTriggerState = 1;
                }
                break;
            case 1:
                if (barbariansAIActiveState) {
                    for (int barbarianIndex = 0; barbarianIndex <= 6; barbarianIndex++) {
                        if (levelStructure.get("barbarians").get(barbarianIndex) != null) {
                            Barbarian barbarian = (Barbarian)levelStructure.get("barbarians").get(barbarianIndex);
                            barbarian.AIRunner();

                            RectangleCollider barbarianDamageCollider = barbarian.barbarianDamageCollider;
                            barbarianDamageCollider.updatePolygon(barbarianDamageCollider.getGlobalPosition(), new int[]{-1000, -1000});

                            if (CollisionsHandler.isPlayerCollidingWithActionArea(barbarianDamageCollider.getPolygon()) && player.currentAnimation.equals("attack")) {
                                int damage = ExtendedMath.randIRange(Globals.currentMinDamage, Globals.currentHealthMax);
                                barbarian.dealDamage(damage);
                            }

                            RectangleCollider barbarianCollider = barbarian.barbarianCollider;
                            barbarianCollider.updatePolygon(barbarianCollider.getGlobalPosition(), new int[]{-1000, -1000});

                            if (damageable && barbarian.isAttacking && CollisionsHandler.isPlayerCollidingWithActionArea(barbarianCollider.getPolygon())) {
                                damageable = false;
                                healthBar.damagePlayer(barbarian.DAMAGE);
                                healthBar.updatePlayerHealthBar();

                                Timer damageTimer = new Timer(450, finished -> {
                                    damageable = true;
                                });

                                damageTimer.setRepeats(false);
                                damageTimer.start();
                            }
                        }
                    }

                    if (healthBar.getHealth() == 0) {
                        // reload level
                        actionTriggerState = -1;
                        stopLevelClock();

                        for (int barbarianIndex = 0; barbarianIndex <= 6; barbarianIndex++) {
                            if (levelStructure.get("barbarians").get(barbarianIndex) != null) {
                                Barbarian barbarian = (Barbarian)levelStructure.get("barbarians").get(barbarianIndex);
                                barbarian.removeFromParent();
                            }
                        }

                        Timer restartLevelDelayTimer = new Timer(1000, finished -> {
                            System.exit(0);
                        });

                        restartLevelDelayTimer.setRepeats(false);
                        restartLevelDelayTimer.start();
                    }
                }

                if (barbariansKOd == BARBARIANS_NEEDED_TO_KO) {
                    barbariansAIActiveState = false;
                    System.out.println("All barbarians KO");
                    actionTriggerState = 2;

                    ArrayList<int[]> markerPositions = new ArrayList<>();

                    markerPositions.add(new int[]{1215, 835});
                    markerPositions.add(new int[]{1468, 955});
                    markerPositions.add(new int[]{1710, 1058});

                    spawnPositionMarkersSequenced(markerPositions, 250, 2500);
                }
                break;
            case 2:
                boolean collidingWithEscapeAmbushCollider = CollisionsHandler.isPlayerCollidingWithActionArea(escapeAmbushCollider.getPolygon());

                if (collidingWithEscapeAmbushCollider) {
                    stopLevelClock();
                    onLevelFinished.onFinished(levelID, 0);
                }
        }
    }

    private void barbariansEncounter() {
        System.out.println("Barbarians triggered.");
        startBarbariansFormation();

        AtomicBoolean ignoreDialogFinished = new AtomicBoolean(false);

        Timer delayDialogTimer = new Timer(2500, completed -> {
            ambushEncounterDialog.setVisibility(true);
            ambushEncounterDialog.addDialogFinishedListener(lastChosenOption -> {
                if (ignoreDialogFinished.get()) {
                    return;
                }

                Timer delayRobTimer = new Timer(1000, finished -> {
                    // rob player
                    Globals.coins = 0;
                    Globals.silver = 0;
                    Globals.gold = 0;
                    Globals.crystals = 0;
                    Globals.diamonds = 0;
                    letBarbariansWalkAway();

                    Timer delayBarbariansEncounter2Timer = new Timer(7500, finished2 -> {
                        startBarbariansFormation();
                        Timer delayDialog2Timer = new Timer(2500, finished3 -> {
                            ambushEncounterDialog2.setVisibility(true);
                            ambushEncounterDialog2.addDialogFinishedListener(lastChosenOption2 -> {
                                setUpKOCounter();
                                setPlayerHealthBar(true);
                                setBarbariansAI(true);
                            });
                        });

                        delayDialog2Timer.setRepeats(false);
                        delayDialog2Timer.start();
                    });

                    delayBarbariansEncounter2Timer.setRepeats(false);
                    delayBarbariansEncounter2Timer.start();
                });

                delayRobTimer.setRepeats(false);
                delayRobTimer.start();
            });

            ambushEncounterDialog.addDialogProgressListener(progress -> {
                if (progress == 11 && ambushEncounterDialog.lastChosenOption == 1) {
                    ignoreDialogFinished.set(true);
                    ambushEncounterDialog.setVisibility(false);
                    setUpKOCounter();
                    setPlayerHealthBar(true);
                    setBarbariansAI(true);
                }
            });
        });

        delayDialogTimer.setRepeats(false);
        delayDialogTimer.start();
    }

    private void setUpKOCounter() {
        KOCounter = new QuestObjectCounterNode(
                "textures/Skull.png",
                7,
                new int[]{200, 100},
                new int[]{GameWindow.WINDOW_SIZE.width - GameWindow.WINDOW_SIZE.width / 7, 75}
        );
        addChild(KOCounter);
    }

    public void updateKOCounter() {
        KOCounter.updateQuestItemCounter();
    }

    private void setPlayerHealthBar(boolean state) {
        if (state) {
            healthBar = new PlayerHealthNode();
            addChild(healthBar);
        } else {
            healthBar.setVisibility(false);
        }
    }

    private void setBarbariansAI(boolean state) {
        if (state) {
            for (int barbarianIndex = 0; barbarianIndex <= 6; barbarianIndex++) {
                Barbarian barbarian = (Barbarian)levelStructure.get("barbarians").get(barbarianIndex);
                barbarian.initFightingInstinct();
            }
            barbariansAIActiveState = true;
        } else {
            barbariansAIActiveState = false;
        }
    }

    private void startBarbariansFormation() {
        ArrayList<int[]> walkToPositions = new ArrayList<>();
        walkToPositions.add(new int[]{1128, 736});
        walkToPositions.add(new int[]{1289, 912});
        walkToPositions.add(new int[]{1431, 871});
        walkToPositions.add(new int[]{1521, 760});
        walkToPositions.add(new int[]{1529, 992});
        walkToPositions.add(new int[]{1639, 837});
        walkToPositions.add(new int[]{1700, 992});

        for (int barbarianIndex = 0; barbarianIndex <= 6; barbarianIndex++) {
            Barbarian barbarian = (Barbarian)levelStructure.get("barbarians").get(barbarianIndex);
            barbarian.runToPosition(walkToPositions.get(barbarianIndex));
        }
    }

    private void letBarbariansWalkAway() {
        ArrayList<int[]> walkToInitialPositions = new ArrayList<>();
        walkToInitialPositions.add(new int[]{1912, 1144});
        walkToInitialPositions.add(new int[]{2073, 1320});
        walkToInitialPositions.add(new int[]{2215, 1279});
        walkToInitialPositions.add(new int[]{2305, 1168});
        walkToInitialPositions.add(new int[]{2313, 1400});
        walkToInitialPositions.add(new int[]{2423, 1245});
        walkToInitialPositions.add(new int[]{2484, 1400});

        for (int barbarianIndex = 0; barbarianIndex <= 6; barbarianIndex++) {
            Barbarian barbarian = (Barbarian)levelStructure.get("barbarians").get(barbarianIndex);
            barbarian.runToPosition(walkToInitialPositions.get(barbarianIndex));
        }
    }
}