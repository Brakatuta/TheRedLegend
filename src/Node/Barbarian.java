package Node;

import Node.Levels.LevelController;
import Node.Levels.Level_TheAmbush;
import Resources.Animations;

import Physics.*;
import Resources.Animations;
import Util.ExtendedMath;
import Util.Globals;
import Util.Tween;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Barbarian extends Node {
    public String[] idleFrames = Animations.BarbarianFrames.get("idle");
    public String[] runFrames = Animations.BarbarianFrames.get("run");
    public String[] attackFrames = Animations.BarbarianFrames.get("attack");
    public String[] deadFrames = Animations.BarbarianFrames.get("dead");

    private final double travelTime = 2.0;

    private int[] spriteFrameSize = new int[]{240, 135};
    private Sprite character;

    public RectangleCollider barbarianCollider;
    public RectangleCollider barbarianDamageCollider;
    public boolean canDamage = true;

    private final int MAX_HEALTH = 150;
    public int health = 150;
    private LabelNode healthInfo;

    private Tween moveTween = null;
    private boolean isMoving = false;
    public boolean isAttacking = false;
    private final double MIN_ATTACK_DISTANCE = 150.0;
    public int DAMAGE = 5;

    private boolean timeOutAI = false;

    public Barbarian(int[] relativePosition) {
        super(new int[]{240, 135}, relativePosition, Color.white, false);

        // Start with idle animation
        character = new Sprite(idleFrames, spriteFrameSize, new int[]{0, 0}, new int[]{0, 0}, 100, true);
        addChild(character);

        barbarianCollider = new RectangleCollider(spriteFrameSize, new int[]{0, 0}, 3, false);
        addChild(barbarianCollider);

        barbarianDamageCollider = new RectangleCollider(spriteFrameSize, new int[]{0, 0}, 4, false);
        addChild(barbarianDamageCollider);

        healthInfo = new LabelNode(
                health + "/" + MAX_HEALTH,
                new int[]{0, -25},
                new int[]{240, 64},
                new Color(232, 124, 14, 255),
                20
        );
        healthInfo.setOutline(new Color(174, 52, 8, 255), 3.0f);
        addChild(healthInfo);
        healthInfo.setVisibility(false);
    }

    public void runToPosition(int[] destination) {
        if (isMoving || isAttacking) {
            return;
        }

        isMoving = true;

        int[] currentCharacterPosition = super.getPosition();

        double travelDistanceUnit = 500.0; // const

        double distance = Math.sqrt(Math.pow((destination[0] - currentCharacterPosition[0]), 2) + Math.pow((destination[1] - currentCharacterPosition[1]), 2));
        double tweenTime = travelTime * (distance / travelDistanceUnit);

        if (moveTween == null) {
            moveTween = new Tween();

            moveTween.addTweenListener(() -> {
                character.switchToAnimation(idleFrames, spriteFrameSize, new int[]{0, 0}, 100, true);
                isMoving = false;
            });
        }

        character.mirrorSprite = (Math.signum(currentCharacterPosition[0] - destination[0]) == -1);

        character.switchToAnimation(runFrames, spriteFrameSize, new int[]{0, 0}, 100, true);
        moveTween.interpolatePosition(this, destination, tweenTime);
    }

    public void initFightingInstinct() {
        healthInfo.setVisibility(true);
    }

    public void AIRunner() {
        if (timeOutAI) {
            return;
        }

        timeOutAI = true;

        if (health > 0) {
            int[] currentPosition = getGlobalPosition();
            int[] playerPosition = LevelController.currentPlayer.getGlobalPosition();

            //System.out.println(ExtendedMath.getDistanceBetweenPositions(currentPosition, playerPosition));

            if (ExtendedMath.getDistanceBetweenPositions(currentPosition, playerPosition) < MIN_ATTACK_DISTANCE) {
                moveTween.stop();
                isMoving = false;
                isAttacking = true;
                character.switchToAnimation(attackFrames, spriteFrameSize, new int[]{0, 0}, 100, false);

                Timer delayDecisionTimer2 = new Timer(500, finished2 -> {
                    isAttacking = false;
                    timeOutAI = false;
                });

                delayDecisionTimer2.setRepeats(false);
                delayDecisionTimer2.start();
            } else {
                int[] newPosition = new int[]{playerPosition[0] + ExtendedMath.randIRange(-150, 150), playerPosition[1] + ExtendedMath.randIRange(-150, 150)};
                runToPosition(newPosition);

                Timer delayDecisionTimer2 = new Timer(ExtendedMath.randIRange(350, 950), finished2 -> {
                    timeOutAI = false;
                });

                delayDecisionTimer2.setRepeats(false);
                delayDecisionTimer2.start();
            }
        } else {
            die();
        }
    }

    public void dealDamage(int amount) {
        if (!canDamage) {
            return;
        }

        canDamage = false;
        health = ExtendedMath.clampInt(health - amount, 0, MAX_HEALTH);

        //System.out.println(health);

        healthInfo.setText(health + "/" + MAX_HEALTH);

        if (health == 0) {
            timeOutAI = true;
            Level_TheAmbush level = (Level_TheAmbush)LevelController.currentLevel;
            level.barbariansKOd++;
            level.updateKOCounter();
            die();
        } else {
            Timer delayTimer = new Timer(450, finished -> {
                canDamage = true;
            });

            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    private void die() {
        if (moveTween != null) {
            moveTween.stop();
            moveTween = null;
        }

        character.switchToAnimation(deadFrames, spriteFrameSize, new int[]{0, 0}, 333, false);
    }
}
