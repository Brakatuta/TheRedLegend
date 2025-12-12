package Node;

import Node.Levels.LevelController;
import Physics.RectangleCollider;
import Resources.Animations;
import Util.ExtendedMath;
import Util.GameWindow;
import Util.Tween;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Dragon extends Node {
    private int[] rootPosition;

    public double DRAGON_SCALE = 5.0;

    public Sprite character;
    public RectangleCollider dragonAttackCollider;
    public RectangleCollider dragonAttackPlayerCollider;

    public ArrayList<RectangleCollider> dragonsBreathColliders = new ArrayList<>(){};
    public ArrayList<RectangleCollider> dragonFireStrikeColliders = new ArrayList<>(){};

    public boolean stunned = false;

    String[] idleFrames = Animations.FireDragonFrames.get("idle");
    int[] idleFramesSize = new int[]{};
    int[] idleFramesOffset = new int[]{0, 0};

    String[] attackFrames1 = Animations.FireDragonFrames.get("attack1");
    int[] attackFrames1Size;
    int[] attackFrames1Offset;

    String[] attackFrames2 = Animations.FireDragonFrames.get("attack2");
    int[] attackFrames2Size;
    int[] attackFrames2Offset;

    String[] damagedFrames = Animations.FireDragonFrames.get("damaged");
    int[] damagedFramesSize = new int[]{};
    int[] damagedFramesOffset = new int[]{};

    String[] deadFrames = Animations.FireDragonFrames.get("dead");
    int[] deadFramesSize = new int[]{};
    int[] deadFramesOffset = new int[]{};

    String[] dragonsbreathFrames = Animations.FireDragonFrames.get("dragonsbreath");


    private int[] scaleIntArray(int[] original, double scaleX, double scaleY) {
        int[] scaled = new int[original.length];

        for (int i = 0; i < original.length; i++) {
            scaled[i] = (int) Math.round(original[i] * (i % 2 == 0 ? scaleX : scaleY));
        }

        return scaled;
    }

    public Dragon(int[] size, int[] relativePosition) {
        super(size, relativePosition, Color.orange, false);

        rootPosition = getGlobalPosition();

        character = new Sprite(idleFrames, new int[]{(int)(89 * DRAGON_SCALE), (int)(160 * DRAGON_SCALE)}, new int[]{0, 0}, new int[]{0, 0}, 450, true);
        character.mirrorImage(true);
        addChild(character);

        dragonAttackCollider = new RectangleCollider(size, getGlobalPosition(), 3, false);
        LevelController.currentLevel.addChild(dragonAttackCollider);

        dragonAttackPlayerCollider = new RectangleCollider(new int[]{0, 0}, getGlobalPosition(), 4, false);
        LevelController.currentLevel.addChild(dragonAttackPlayerCollider);

        idleFramesSize = scaleIntArray(new int[]{89, 160}, DRAGON_SCALE, DRAGON_SCALE);

        attackFrames1Size = scaleIntArray(new int[]{198, 160}, DRAGON_SCALE, DRAGON_SCALE);
        attackFrames1Offset = scaleIntArray(new int[]{0, 0}, DRAGON_SCALE, DRAGON_SCALE);

        attackFrames2Size = scaleIntArray(new int[]{210, 160}, DRAGON_SCALE, DRAGON_SCALE);
        attackFrames2Offset = scaleIntArray(new int[]{0, -15}, DRAGON_SCALE, DRAGON_SCALE);

        damagedFramesSize = scaleIntArray(new int[]{140, 160}, DRAGON_SCALE, DRAGON_SCALE);
        damagedFramesOffset = scaleIntArray(new int[]{0, -12}, DRAGON_SCALE, DRAGON_SCALE);

        deadFramesSize = scaleIntArray(new int[]{120, 160}, DRAGON_SCALE, DRAGON_SCALE);
        deadFramesOffset = scaleIntArray(new int[]{0, -10}, DRAGON_SCALE, DRAGON_SCALE);
    }

    public void basicAttack() {
        setPosition(new int[]{getGlobalPosition()[0] - 350, getGlobalPosition()[1]});

        setSize(attackFrames1Size[0], attackFrames1Size[1]);
        character.switchToAnimation(attackFrames1, attackFrames1Size, attackFrames1Offset, 200, false);

        dragonAttackCollider.setPosition(new int[]{getGlobalPosition()[0] + attackFrames1Offset[0], getGlobalPosition()[1] + attackFrames1Offset[1]});
        dragonAttackCollider.updatePolygon(
                new int[]{getGlobalPosition()[0] + attackFrames1Offset[0], getGlobalPosition()[1] + attackFrames1Offset[1]},
                attackFrames1Size
        );

        dragonAttackPlayerCollider.setPosition(new int[]{getGlobalPosition()[0] + attackFrames1Offset[0], getGlobalPosition()[1] + attackFrames1Offset[1]});
        dragonAttackPlayerCollider.updatePolygon(
                new int[]{getGlobalPosition()[0] + attackFrames1Offset[0], getGlobalPosition()[1] + attackFrames1Offset[1]},
                new int[]{(int)(attackFrames1Size[0] / 1.85), attackFrames1Size[1]}
        );

        Timer dragonsBreathSpawnDelayTimer = new Timer(400, finished -> {
            int amount = ExtendedMath.randIRange(2, 5);

            for (int i = 0; i < amount; i++) {
                int[] randomSpawnPosition = new int[]{ExtendedMath.randIRange(1000, 1900), 25};
                spawnDragonsBreath(randomSpawnPosition);
            }
        });

        dragonsBreathSpawnDelayTimer.setRepeats(false);
        dragonsBreathSpawnDelayTimer.start();

        resetToIdleAfterDelay(1500);
    }

    private void spawnDragonsBreath(int[] spawnPosition) {
        Node currentLevel = LevelController.currentLevel;

        int[] dragonsBreathSize = new int[]{239 / 2, 239 / 2};
        Node dragonsBreath = new Node(dragonsBreathSize, spawnPosition, Color.white, false);
        currentLevel.addChild(dragonsBreath);

        Sprite dragonsBreathSprite = new Sprite(dragonsbreathFrames, dragonsBreathSize, new int[]{0, 0}, new int[]{0, 0}, 20, true);
        dragonsBreath.addChild(dragonsBreathSprite);

        int[] destinationPosition = new int[]{spawnPosition[0] - 1000, GameWindow.WINDOW_SIZE.height - 239 / 2};

        Tween dragonsBreathTween = new Tween();
        dragonsBreathTween.interpolatePosition(dragonsBreath, destinationPosition, 2.5);
        dragonsBreathTween.addTweenListener(() -> {
            RectangleCollider collider = new RectangleCollider(dragonsBreathSize, dragonsBreath.getGlobalPosition(), 4, false);
            dragonsBreathColliders.add(collider);
            currentLevel.addChild(collider);

            Timer removalDelayTimer = new Timer(500, finished -> {
                dragonsBreathColliders.remove(collider);
                collider.removeFromParent();
                dragonsBreath.removeFromParent();
            });

            removalDelayTimer.setRepeats(false);
            removalDelayTimer.start();
        });
    }

    public void fireStrikeAttack() {
        setPosition(new int[]{getGlobalPosition()[0] - 350, getGlobalPosition()[1]});

        setSize(attackFrames2Size[0], attackFrames2Size[1]);
        character.switchToAnimation(attackFrames2, attackFrames2Size, attackFrames2Offset, 200, false);

        dragonAttackCollider.setPosition(new int[]{getGlobalPosition()[0] + attackFrames2Offset[0], getGlobalPosition()[1] + attackFrames2Offset[1]});
        dragonAttackCollider.updatePolygon(
                new int[]{getGlobalPosition()[0] + attackFrames2Offset[0], getGlobalPosition()[1] + attackFrames2Offset[1]},
                attackFrames2Size
        );

        dragonAttackPlayerCollider.setPosition(new int[]{getGlobalPosition()[0] + attackFrames2Offset[0] + 600, getGlobalPosition()[1] + attackFrames2Offset[1]});
        dragonAttackPlayerCollider.updatePolygon(
                new int[]{getGlobalPosition()[0] + attackFrames2Offset[0] + 600, getGlobalPosition()[1] + attackFrames2Offset[1]},
                new int[]{(int)(attackFrames2Size[0] / 4), attackFrames2Size[1]}
        );

        Timer fireStrikeSpawnDelayTimer = new Timer(600, finished -> {
            int amount = ExtendedMath.randIRange(1, 3);

            for (int i = 0; i < amount; i++) {
                int[] randomSpawnPosition = new int[]{ExtendedMath.randIRange(0, 1400), 25};
                ImageNode warningSign = new ImageNode("textures/WarningSign.png", new int[]{128, 128}, new int[]{randomSpawnPosition[0] + 32, 900 + 128});
                LevelController.currentLevel.addChild(warningSign);

                Timer spawnDelayTimer = new Timer(1000, timeout -> {
                    warningSign.removeFromParent();
                    SpawnFireStrike(randomSpawnPosition);
                });

                spawnDelayTimer.setRepeats(false);
                spawnDelayTimer.start();
            }
        });

        fireStrikeSpawnDelayTimer.setRepeats(false);
        fireStrikeSpawnDelayTimer.start();

        resetToIdleAfterDelay(1800);
    }

    public void SpawnFireStrike(int[] spawnPosition) {
        Node currentLevel = LevelController.currentLevel;

        Node fireStrike = new Node(new int[]{140, 1280}, spawnPosition, Color.WHITE, false);
        currentLevel.addChild(fireStrike);

        Sprite fireStrikeTexture1 = new Sprite(Animations.FireDragonFrames.get("firestrike"), new int[]{142, 640}, new int[]{0, 0},new int[]{0, 0}, 60, true);
        fireStrike.addChild(fireStrikeTexture1);

        Sprite fireStrikeTexture2 = new Sprite(Animations.FireDragonFrames.get("firestrike"), new int[]{142, 640}, new int[]{0, 640},new int[]{0, 0}, 60, true);
        fireStrike.addChild(fireStrikeTexture2);

        Timer delayImpactTimer = new Timer(250, finished -> {
            Sprite strikeImpact = new Sprite(
                    Animations.FireDragonFrames.get("firestrikeimpact"),
                    new int[]{256, 256},
                    new int[]{spawnPosition[0] - 57, 900},
                    new int[]{0, 0},
                    100,
                    false
            );
            fireStrike.removeFromParent();
            currentLevel.addChild(strikeImpact);

            RectangleCollider collider = new RectangleCollider(
                    new int[]{128, 128},
                    new int[]{spawnPosition[0] + 20, 900 + 100},
                    4,
                    false
            );
            dragonFireStrikeColliders.add(collider);
            currentLevel.addChild(collider);

            Timer colliderRemovalDelayTimer = new Timer(350, completed -> {
                dragonFireStrikeColliders.remove(collider);
                collider.removeFromParent();
            });

            colliderRemovalDelayTimer.setRepeats(false);
            colliderRemovalDelayTimer.start();

            Timer strikeRemovalDelayTimer = new Timer(1400, completed -> {
                strikeImpact.removeFromParent();
            });

            strikeRemovalDelayTimer.setRepeats(false);
            strikeRemovalDelayTimer.start();
        });

        delayImpactTimer.setRepeats(false);
        delayImpactTimer.start();
    }

    private void resetToIdleAfterDelay(int dealyMS) {
        Timer attackDurationTimer = new Timer(dealyMS, finished -> {
            resetToIdle();
        });

        attackDurationTimer.setRepeats(false);
        attackDurationTimer.start();
    }

    private void resetToIdle() {
        setPosition(rootPosition);
        setSize(idleFramesSize[0], idleFramesSize[1]);
        character.switchToAnimation(idleFrames, idleFramesSize, idleFramesOffset, 250, true);

        dragonAttackCollider.setPosition(rootPosition);
        dragonAttackCollider.updatePolygon(getGlobalPosition(), idleFramesSize);

        dragonAttackPlayerCollider.setPosition(rootPosition);
        dragonAttackPlayerCollider.updatePolygon(getGlobalPosition(), new int[]{0, 0});
    }

    public void stunEffect() {
        stunned = true;
        setSize(damagedFramesSize[0], damagedFramesSize[1]);
        character.switchToAnimation(damagedFrames, damagedFramesSize, damagedFramesOffset, 100, false);

        Timer stunDurationTimer = new Timer(600, finished -> {
            stunned = false;
            resetToIdle();
        });

        stunDurationTimer.setRepeats(false);
        stunDurationTimer.start();
    }

    public void deathEffect() {
        setPosition(new int[]{getGlobalPosition()[0] - 65, getGlobalPosition()[1]});
        setSize(deadFramesSize[0], deadFramesSize[1]);
        character.switchToAnimation(deadFrames, deadFramesSize, deadFramesOffset, 450, false);
    }
}
