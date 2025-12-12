package Node.Levels;

import Node.*;
import Node.Dialog;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Util.ExtendedMath;
import Util.Globals;
import Util.Tween;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Level_DragonFight extends Level {
    // Debug
    static private final boolean skipNarratorAndDialogs = false;
    static private final boolean testEnd = false;
    static double tweenSpeed = 1.0;
    //

    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    static int DRAGON_HEALTH = 1000;
    private int currentDragonHealth = 1000;

    private int DIRECT_DAMAGE_FROM_DRAGON = 10;
    private int DRAGONS_BREATH_DAMAGE = 5;
    private int DRAGON_FIRESTRIKE_DAMAGE = 15;

    private boolean damageCalculation = true;

    private boolean playerDamageDebounce = false;
    private boolean dragonDamageDebounce = false;

    static double DRAGON_SCALE = 5.0;

    private boolean canRunAttackCycle = true;

    private Player player;
    private Dragon fireDragon;

    private int[] playerSpawnPoint = new int[]{45, 1035};

    private TransitionNode transitionNode;

    private PlayerHealthNode healthBar;
    private Node bossBar;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/dragonfight/BackgroundDragonFight.png", new int[]{3824, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/dragonfight/DragonLandBackground.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("player", Arrays.asList(
                new Player(new int[]{(int)(180 * 1.25), (int)(100 * 1.25)}, new int[]{45, 1035})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheGreatDragonFight",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 850},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheFinalFaceOf",
                        Color.decode("#301c05"),
                        25
                ),
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TheFinalFaceOfEnd",
                        Color.decode("#301c05"),
                        25
                )
        ));

        colliders.add(new RectangleCollider(new int[]{1912, 122}, new int[]{0, 900}, 0, false));
        colliders.add(new RectangleCollider(new int[]{17, 140}, new int[]{0, 1022}, 0, false));
        colliders.add(new RectangleCollider(new int[]{17, 140}, new int[]{1350, 1022}, 0, false));
    }
    //

    public Level_DragonFight(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        player = (Player)levelStructure.get("player").get(0);
        player.canMove = false;
        player.setVisibility(false);
        levelStructure.get("dialog").get(1).setVisibility(false);
        levelStructure.get("dialog").get(2).setVisibility(false);

        transitionNode = (TransitionNode) levelStructure.get("transition").get(0);

        transitionNode.setBackgroundColor(Color.black);
        transitionNode.setAlpha(1.0f);

        transitionNode.startTransition(Color.black, 0.0f, 1000);

        Dialog dialog = (Dialog)levelStructure.get("dialog").get(0);

        if (skipNarratorAndDialogs || testEnd) {
            tweenSpeed = 0.1;
            dialog.removeFromParent();
            levelStructure.get("background").get(1).setVisibility(false);
            levelStructure.get("background").get(1).removeFromParent();

            ImageNode fightBackground = (ImageNode)levelStructure.get("background").get(0);
            Tween tweenToFightingSpace = new Tween();
            tweenToFightingSpace.interpolatePosition(fightBackground, new int[]{-1912, 0}, tweenSpeed * 10.0);
            tweenToFightingSpace.addTweenListener(this::initializeFight);
            player.setVisibility(true);
            return;
        }

        dialog.addDialogFinishedListener(lastChosenOption -> {
            transitionNode.startInOutTransition(Color.black, 1.0f, 1000);

            Timer delayTimer = new Timer(500, finished -> {
                levelStructure.get("background").get(1).setVisibility(false);
                levelStructure.get("background").get(1).removeFromParent();

                ImageNode fightBackground = (ImageNode)levelStructure.get("background").get(0);
                Tween tweenToFightingSpace = new Tween();
                tweenToFightingSpace.interpolatePosition(fightBackground, new int[]{-1912, 0}, tweenSpeed * 10.0);
                tweenToFightingSpace.addTweenListener(this::initializeFight);
            });

            delayTimer.setRepeats(false);
            delayTimer.start();

            Timer playerAnimationDelayTimer = new Timer(750, finished ->{
                player.setVisibility(true);
                player.character.switchToAnimation(player.walkFrames, player.walkFramesScale, player.walkFramesOffset, 100, true);
            });

            playerAnimationDelayTimer.setRepeats(false);
            playerAnimationDelayTimer.start();
        });
    }

    private void buildPlayerHealthBar() {
        healthBar = new PlayerHealthNode();
        addChild(healthBar);
        healthBar.updatePlayerHealthBar();
    }

    private void buildBossBar() {
        int[] size = getSize();
        int[] bossBarSize = new int[]{862, 200};
        bossBar = new Node(bossBarSize, new int[]{size[0] / 2 - (bossBarSize[0] / 2), bossBarSize[1] / 8}, Color.white, false);
        addChild(bossBar);

        Node bossBarBackground = new Node(new int[]{814, 32}, new int[]{24, 73}, new Color(71, 23, 19, 255), true);
        bossBar.addChild(bossBarBackground);

        Node bossBarInnerPart = new Node(new int[]{814, 32}, new int[]{24, 73}, new Color(184, 82, 25, 255), true);
        bossBar.addChild(bossBarInnerPart);

        ImageNode bossBarCover = new ImageNode("textures/firedragon/Bossbar.png", new int[]{862, 136}, new int[]{0, 0});
        bossBar.addChild(bossBarCover);

        LabelNode HealthLabel = new LabelNode(currentDragonHealth + "/" + DRAGON_HEALTH, new int[]{-5, 60}, bossBarSize, new Color(174, 91, 6, 255), 35);
        bossBar.addChild(HealthLabel);

        updateBossBar();
    }

    private void updateBossBar() {
        Node progressPart = bossBar.getAllChildren().get(1);
        LabelNode HealthLabel = (LabelNode)bossBar.getAllChildren().get(3);

        double progress = (double)814 * ((double)currentDragonHealth / DRAGON_HEALTH);
        progress = ExtendedMath.clamp(progress, 0, DRAGON_HEALTH);
        progressPart.setSize((int)progress, 32);

        HealthLabel.setText(currentDragonHealth + "/" + DRAGON_HEALTH);
    }

    private void initializeFight() {
        player.character.switchToAnimation(player.idleFrames, player.idleFramesScale, player.idleFramesOffset, 100, true);

        fireDragon = new Dragon(new int[]{(int)(89 * DRAGON_SCALE), (int)(160 * DRAGON_SCALE)}, new int[]{1912 - 800, 500});
        addChild(fireDragon);

        if (skipNarratorAndDialogs || testEnd) {
            startFight();
            return;
        }

        Dialog dragonDialog = (Dialog)levelStructure.get("dialog").get(1);
        dragonDialog.moveNodeToTop();
        dragonDialog.setVisibility(true);

        dragonDialog.addDialogFinishedListener(finished -> {
            startFight();
        });
    }

    private void startFight() {
        Dialog dragonDialog = (Dialog)levelStructure.get("dialog").get(1);
        dragonDialog.removeFromParent();
        System.out.println("Dragon Fight starting!");
        buildPlayerHealthBar();
        buildBossBar();
        player.canMove = true;
        player.moveSpeed = 20;
        LevelController.initializePlayer(player);
        player.canMoveHorizontalOnly = true;
        player.moveNodeToTop();

        PlayerInventory playerInventory = new PlayerInventory();
        addChild(playerInventory);

        Timer startAttackCycleDelay = new Timer(1500, completed -> {
            if (testEnd) {
                dragonDefeated();
            } else {
                startLevelClock();
                attackCycle();
            }
        });

        startAttackCycleDelay.setRepeats(false);
        startAttackCycleDelay.start();
    }

    private void attackCycle() {
        Timer delayTimer = new Timer(1000, finished -> {
            int attackType = (int)(Math.random() * 2);
            System.out.println("Dragon attacks with Attack " + attackType);

            switch (attackType) {
                case 0:
                    fireDragon.basicAttack();
                    break;
                case 1:
                    fireDragon.fireStrikeAttack();
                    break;
            }
        });

        delayTimer.setRepeats(false);
        delayTimer.start();

        Timer delayNextAttackTimer = new Timer(ExtendedMath.randIRange(4000, 6000), finished -> {
            if (canRunAttackCycle) {
                attackCycle();
            }
        });

        delayNextAttackTimer.setRepeats(false);
        delayNextAttackTimer.start();
    }

    public void _process() {
        if (damageCalculation) {
            checkForPlayerDamage();
            checkForDragonDamage();
        }
    }

    private void checkForPlayerDamage() {
        if (playerDamageDebounce) {
            return;
        }

        boolean collidingWithDragonDamageCollider = CollisionsHandler.isPlayerCollidingWithActionArea(fireDragon.dragonAttackPlayerCollider.getPolygon());
        boolean collidingWithDragonsBreathCollider = false;
        boolean collidingWithFireStrikeCollider = false;

        for (RectangleCollider collider: fireDragon.dragonsBreathColliders) {
            if (CollisionsHandler.isPlayerCollidingWithActionArea(collider.getPolygon())) {
                collidingWithDragonsBreathCollider = true;
                break;
            }
        }

        for (RectangleCollider collider: fireDragon.dragonFireStrikeColliders) {
            if (CollisionsHandler.isPlayerCollidingWithActionArea(collider.getPolygon())) {
                collidingWithFireStrikeCollider = true;
                break;
            }
        }

        if (collidingWithDragonDamageCollider || collidingWithDragonsBreathCollider || collidingWithFireStrikeCollider) {
            playerDamageDebounce = true;
            int debounceTimeMS = 450;

            if (collidingWithDragonDamageCollider) {
                healthBar.damagePlayer(DIRECT_DAMAGE_FROM_DRAGON);
            }

            if (collidingWithDragonsBreathCollider) {
                healthBar.damagePlayer(DRAGONS_BREATH_DAMAGE);
                debounceTimeMS = 500;
            }

            if (collidingWithFireStrikeCollider) {
                healthBar.damagePlayer(DRAGON_FIRESTRIKE_DAMAGE);
                debounceTimeMS = 900;
            }

            healthBar.updatePlayerHealthBar();

            if (healthBar.getHealth() == 0) {
                restartFight();
                return;
            }

            Timer damageDebounceTimer = new Timer(debounceTimeMS, finished -> {
                playerDamageDebounce = false;
            });

            damageDebounceTimer.setRepeats(false);
            damageDebounceTimer.start();
        }
    }

    private void checkForDragonDamage() {
        if (dragonDamageDebounce || fireDragon.stunned) {
            return;
        }

        boolean attacking = CollisionsHandler.isPlayerCollidingWithActionArea(fireDragon.dragonAttackCollider.getPolygon()) && player.currentAnimation.equals("attack");
        if (attacking) {
            dragonDamageDebounce = true;

            int damage = ExtendedMath.randIRange(Globals.currentMinDamage, Globals.currentMaxDamage);
            currentDragonHealth = (int)ExtendedMath.clamp(currentDragonHealth - damage, 0, DRAGON_HEALTH);
            fireDragon.stunEffect();
            updateBossBar();

            Timer damageDebounceTimer = new Timer(350, finished -> {
                dragonDamageDebounce = false;
            });

            damageDebounceTimer.setRepeats(false);
            damageDebounceTimer.start();

            if (currentDragonHealth == 0) {
                System.out.println("Dragon defeated!");
                dragonDefeated();
            }
        }
    }

    private void dragonDefeated() {
        canRunAttackCycle = false;
        damageCalculation = false;
        stopLevelClock();

        Dialog endDialog = (Dialog)levelStructure.get("dialog").get(2);
        endDialog.moveNodeToTop();
        endDialog.setVisibility(true);

        endDialog.addDialogProgressListener(progress -> {
            if (progress == 2) {
                healthBar.removeFromParent();
                bossBar.removeFromParent();
                Timer delayDeathEfefctTimer = new Timer(500, finished -> {
                    fireDragon.deathEffect();
                });

                delayDeathEfefctTimer.setRepeats(false);
                delayDeathEfefctTimer.start();
            }
        });

        endDialog.addDialogFinishedListener(lastChosenOption -> {
            transitionNode.moveNodeToTop();

            transitionNode.startTransition(Color.black, 1.0f, 5000);

            transitionNode.setOnTransitionFinished(finished -> {
                transitionNode.removeTransitionFinishedListener();
                System.out.println("Completed Boss Fight!");
                onLevelFinished.onFinished(levelID, lastChosenOption);
            });
        });
    }

    private void restartFight() {
        // reset variables
        damageCalculation = false;
        canRunAttackCycle = false;
        stopLevelClock();
        playerDamageDebounce = false;
        //

        Color colorStart = new Color(89, 8, 8, 0);
        Color colorMiddle = new Color(89, 8, 8, 255);

        Node messageHolder = new Node(new int[]{1912, 500}, new int[]{0, 300}, Color.WHITE, false);
        addChild(messageHolder);
        messageHolder.moveNodeToTop();

        LabelNode deathMessage = new LabelNode(
                "YOU DIED",
                new int[]{0, 0},
                new int[]{1912, 500},
                colorStart,
                150
        );
        messageHolder.addChild(deathMessage);

        int fadeInDuration = 2000;
        int holdDuration = 2000;
        int fadeOutDuration = 2000;
        int frameRate = 60;

        new Thread(() -> {
            try {
                // === FADE IN ===
                int fadeInFrames = (fadeInDuration / 1000) * frameRate;
                for (int i = 0; i <= fadeInFrames; i++) {
                    float t = (float) i / fadeInFrames;

                    int r = (int) (colorStart.getRed() + t * (colorMiddle.getRed() - colorStart.getRed()));
                    int g = (int) (colorStart.getGreen() + t * (colorMiddle.getGreen() - colorStart.getGreen()));
                    int b = (int) (colorStart.getBlue() + t * (colorMiddle.getBlue() - colorStart.getBlue()));
                    int a = (int) (colorStart.getAlpha() + t * (colorMiddle.getAlpha() - colorStart.getAlpha()));

                    deathMessage.setColor(new Color(r, g, b, a));
                    Thread.sleep(1000 / frameRate);
                }

                // === HOLD (fully visible) ===
                Thread.sleep(holdDuration);

                // === FADE OUT ===
                int fadeOutFrames = (fadeOutDuration / 1000) * frameRate;
                for (int i = 0; i <= fadeOutFrames; i++) {
                    float t = (float) i / fadeOutFrames;

                    int r = (int) (colorMiddle.getRed() + t * (colorStart.getRed() - colorMiddle.getRed()));
                    int g = (int) (colorMiddle.getGreen() + t * (colorStart.getGreen() - colorMiddle.getGreen()));
                    int b = (int) (colorMiddle.getBlue() + t * (colorStart.getBlue() - colorMiddle.getBlue()));
                    int a = (int) (colorMiddle.getAlpha() + t * (colorStart.getAlpha() - colorMiddle.getAlpha()));

                    deathMessage.setColor(new Color(r, g, b, a));
                    Thread.sleep(1000 / frameRate);
                }

                // === FINISHED ===
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Resetting Boss Fight.");
                    messageHolder.removeFromParent();
                    transitionNode.startInOutTransition(Color.black, 1.0f, 5000);
                    transitionNode.moveNodeToTop();

                    Timer resetDelayTimer = new Timer(2500, finished -> {
                        currentDragonHealth = DRAGON_HEALTH;
                        healthBar.resetHealth();
                        healthBar.updatePlayerHealthBar();
                        updateBossBar();
                        canRunAttackCycle = true;
                        damageCalculation = true;
                        startLevelClock();
                        attackCycle();
                        player.setPosition(playerSpawnPoint);
                    });

                    resetDelayTimer.setRepeats(false);
                    resetDelayTimer.start();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}