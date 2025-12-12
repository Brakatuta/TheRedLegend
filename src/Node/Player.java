package Node;

import Physics.*;
import Resources.Animations;
import Util.Globals;
import Util.Tween;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class Player extends Node {
    public int[] playerSize;

    private final int[] DEFAULT_SIZE = new int[]{180, 100};
    private double scaleX;
    private double scaleY;

    static public double DEFAULT_MOVE_SPEED = 10.0;
    public double moveSpeed = 10.0;

    public boolean canMove = true;
    public boolean canMoveHorizontalOnly = false;

    public ArrayList<int[]> lastPositions = new ArrayList<>();

    public int[] lastMovePosition = new int[]{0, 0};

    final Thread collisionCheckThread;

    public Sprite character;

    public String currentAnimation = "idle";

    public String[] idleFrames = Animations.PlayerFrames.get("idle");
    public int[] idleFramesScale = Animations.PlayerAnimationScales.get("idle");
    public int[] idleFramesOffset = Animations.PlayerAnimationOffsets.get("idle");

    public String[] walkFrames = Animations.PlayerFrames.get("walk");
    public int[] walkFramesScale = Animations.PlayerAnimationScales.get("walk");
    public int[] walkFramesOffset = Animations.PlayerAnimationOffsets.get("walk");

    public String[] attackFrames = Animations.PlayerFrames.get("attack");
    public int[] attackFramesScale = Animations.PlayerAnimationScales.get("attack");
    public int[] attackFramesOffset = Animations.PlayerAnimationOffsets.get("attack");

    Tween moveTween = null;


    private int[] scaleIntArray(int[] original, double scaleX, double scaleY) {
        int[] scaled = new int[original.length];

        for (int i = 0; i < original.length; i++) {
            scaled[i] = (int) Math.round(original[i] * (i % 2 == 0 ? scaleX : scaleY));
        }

        return scaled;
    }

    public Player(int[] size, int[] relativePosition) {
        super(size, relativePosition, Color.white, false);

        this.lastMovePosition = relativePosition;

        this.playerSize = size;

        // Calculate scaling factors
        this.scaleX = (double) size[0] / DEFAULT_SIZE[0];
        this.scaleY = (double) size[1] / DEFAULT_SIZE[1];

        // Scale animation frame data
        int[] scaledIdleScale = scaleIntArray(idleFramesScale, scaleX, scaleY);
        int[] scaledIdleOffset = scaleIntArray(idleFramesOffset, scaleX, scaleY);

        idleFramesScale = scaledIdleScale;
        idleFramesOffset = scaledIdleOffset;

        int[] scaledWalkScale = scaleIntArray(walkFramesScale, scaleX, scaleY);
        int[] scaledWalkOffset = scaleIntArray(walkFramesOffset, scaleX, scaleY);

        walkFramesScale = scaledWalkScale;
        walkFramesOffset = scaledWalkOffset;

        int[] scaledAttackScale = scaleIntArray(attackFramesScale, scaleX, scaleY);
        int[] scaledAttackOffset = scaleIntArray(attackFramesOffset, scaleX, scaleY);

        attackFramesScale = scaledAttackScale;
        attackFramesOffset = scaledAttackOffset;

        // Start with idle animation
        character = new Sprite(idleFrames, idleFramesScale, idleFramesOffset, new int[]{0, 0}, 100, true);
        addChild(character);

        // Scale collider size and offset
        int[] scaledColliderSize = scaleIntArray(new int[]{50, 60}, scaleX, scaleY);
        int[] scaledColliderOffset = scaleIntArray(new int[]{60, 50}, scaleX, scaleY);

        RectangleCollider playerCollider = new RectangleCollider(scaledColliderSize, scaledColliderOffset, 1, false);
        addChild(playerCollider);

        collisionCheckThread = new Thread(() -> {
            while (true) {
                Boolean playerCollision = CollisionsHandler.isPlayerCollidingWithWorld();

                //System.out.println(playerCollision);

                if (playerCollision) {
                    //int[] collisionPoint = CollisionsHandler.getPlayerWorldCollisionPoint();
                    //System.out.println("Collision with Player: " + collisionPoint[0] + " | " + collisionPoint[1]);

                    if (lastPositions.size() > 3) {
                        setPosition(lastPositions.get(lastPositions.size() - 3)); // jump back some physic frames
                    }

                    if (!canMove && moveTween.isRunning()) {
                        moveTween.stop();
                        canMove = true;
                        character.switchToAnimation(idleFrames, idleFramesScale, idleFramesOffset, 100, true);
                        currentAnimation = "idle";
                    }
                } else {
                    while (lastPositions.size() > 25) {
                        // keep a size of 25 (last 25 physic frames)
                        lastPositions.remove(0);
                    }
                    int[] currentPosition = getPosition();
                    if (currentPosition[0] == -1000 && currentPosition[1] == -1000) {
                        return;
                    }
                    lastPositions.add(currentPosition);
                }


                playerCollider.updatePolygon(playerCollider.getGlobalPosition(), new int[]{-1000, -1000});

                try {
                    Thread.sleep(16); // ~60fps
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        collisionCheckThread.start();
    }

    public void onClickMovePlayer(int[] position) {
        if (!canMove) {
            return;
        }

        canMove = false;

        int[] currentCharacterPosition = super.getPosition();

        double travelTime = 2.0 * (DEFAULT_MOVE_SPEED / (moveSpeed * Globals.currentSpeedMultiplier));
        double travelDistanceUnit = 800.0; // const

        double distance = Math.sqrt(Math.pow((position[0] - currentCharacterPosition[0]), 2) + Math.pow((position[1] - currentCharacterPosition[1]), 2));
        double tweenTime = travelTime * (distance / travelDistanceUnit);

        // adjust to take into account player rect
        int offsetX = (int) (DEFAULT_SIZE[0] * 0.5 * scaleX); // e.g. 90 * scaleX
        int offsetY = (int) (DEFAULT_SIZE[1] * 0.5 * scaleY); // e.g. 50 * scaleY

        position = new int[]{position[0] - playerSize[0] + offsetX, position[1] - playerSize[1] + offsetY};

        if (moveTween == null) {
            moveTween = new Tween();

            moveTween.addTweenListener(() -> {
                character.switchToAnimation(idleFrames, idleFramesScale, idleFramesOffset, 100, true);
                currentAnimation = "idle";
                canMove = true;
            });
        }

        if (canMoveHorizontalOnly) {
            position[1] = lastMovePosition[1];
        }

        lastMovePosition = position;

        character.mirrorSprite = (Math.signum(currentCharacterPosition[0] - position[0]) == -1);

        character.switchToAnimation(walkFrames, walkFramesScale, walkFramesOffset, 100, true);
        currentAnimation = "walk";
        moveTween.interpolatePosition(this, position, tweenTime);
    }

    public void playAttackAnimation() {
        if (!canMove) return;

        canMove = false;

        character.switchToAnimation(attackFrames, attackFramesScale, attackFramesOffset, 100, false);
        currentAnimation = "attack";

        int attackDuration = attackFrames.length * 100;

        Timer attackTimer = new Timer(attackDuration, event -> {
            character.switchToAnimation(idleFrames, idleFramesScale, idleFramesOffset, 100, true);
            currentAnimation = "idle";

            canMove = true;

            ((Timer) event.getSource()).stop();
        });

        attackTimer.setRepeats(false);
        attackTimer.start();
    }
}