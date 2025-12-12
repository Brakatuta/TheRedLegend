package Node.Levels;

import Node.*;
import Node.Dialog;
import Physics.RectangleCollider;
import Resources.Animations;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;


public class Level_Epilogue extends Level {
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    private TransitionNode transitionNode;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/teaser/DarkCaveBackground.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/VillageBackgroundHappyCrowd.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/maincharacter/idle/idle000.png", new int[]{96, 125}, new int[]{621, 984})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "Epilogue",
                        Color.decode("#301c05"),
                        25
                )
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));
    }

    public Level_Epilogue(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        transitionNode = (TransitionNode)levelStructure.get("transition").get(0);

        Dialog dialog = (Dialog)levelStructure.get("dialog").get(0);
        dialog.setVisibility(false);

        transitionNode.setBackgroundColor(Color.black);
        transitionNode.setAlpha(1.0f);

        transitionNode.startTransition(Color.black, 0.0f, 2500);
        transitionNode.setOnTransitionFinished(finished -> {
            transitionNode.removeTransitionFinishedListener();
            dialog.setVisibility(true);
            dialog.moveNodeToTop();
        });

        dialog.addDialogFinishedListener(lastChosenOption -> {
            gameEnd();
        });
    }

    private void gameEnd() {
        transitionNode.startTransition(Color.black, 1.0f, 2500);
        Timer transitionInTimer = new Timer(2500, finished -> {
            displayEndTitle();

            Timer replacementTimer = new Timer(4000, timeout -> {
                levelStructure.get("background").get(1).setVisibility(false);
                levelStructure.get("background").get(2).setVisibility(false);
            });

            replacementTimer.setRepeats(false);
            replacementTimer.start();

            Timer endGameFadeOut = new Timer(10000, timeout -> {
                transitionNode.startTransition(Color.black, 0.0f, 2500);
            });

            endGameFadeOut.setRepeats(false);
            endGameFadeOut.start();

            Timer animateEggDelayTimer = new Timer(10000, timeout -> {
                Sprite egg = new Sprite(Animations.FireDragonFrames.get("eggcracking"), new int[]{520, 640}, new int[]{748, 610}, new int[]{0, 0}, 150, false);
                addChild(egg);
                transitionNode.moveNodeToTop();
            });

            animateEggDelayTimer.setRepeats(false);
            animateEggDelayTimer.start();

            Timer endGameFade = new Timer(14000, timeout -> {
                transitionNode.startTransition(Color.black, 1.0f, 5000);
            });

            endGameFade.setRepeats(false);
            endGameFade.start();
        });

        transitionInTimer.setRepeats(false);
        transitionInTimer.start();
    }

    private void displayEndTitle() {
        Color colorStart = new Color(255, 255, 255, 0);
        Color colorMiddle = new Color(255, 255, 255, 255);

        Node messageHolder = new Node(new int[]{1912, 500}, new int[]{0, 300}, Color.WHITE, false);
        addChild(messageHolder);
        messageHolder.moveNodeToTop();

        LabelNode endMessage = new LabelNode(
                "THE END",
                new int[]{0, 0},
                new int[]{1912, 500},
                colorStart,
                150
        );
        messageHolder.addChild(endMessage);

        int fadeInDuration = 3000;
        int holdDuration = 4000;
        int fadeOutDuration = 3000;
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

                    endMessage.setColor(new Color(r, g, b, a));
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

                    endMessage.setColor(new Color(r, g, b, a));
                    Thread.sleep(1000 / frameRate);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
