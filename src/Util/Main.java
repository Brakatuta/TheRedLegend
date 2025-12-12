package Util;

import Node.*;
import Node.Button;
import Node.Dialog;
import Node.Levels.*;
import Physics.*;
import Resources.Animations;
import Resources.DialogUtils;

import java.awt.*;
import java.util.Scanner;
import javax.swing.Timer;


// Entry point
class App {
    private static Node rootNode;

    // level finished signal (main app signal)
    private LevelListener onLevelFinished;

    public void setOnLevelFinished(LevelListener listener) {
        this.onLevelFinished = listener;
    }

    public interface LevelListener {
        void onFinished(Level level, int levelId, int lastChosenOption);
    }
    //

    private static TransitionNode levelTransitionNode;

    public static void main(String[] args) throws Exception {
        String windowScale = "0.9";
        if (args.length > 0) {
            windowScale = args[0];
        }
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.uiScale", windowScale);

        // start up settings
        if (Globals.allowChoosingSettings) {
            System.out.println();
            System.out.println();

            System.out.println("████████╗██╗░░██╗███████╗  ██████╗░███████╗██████╗░  ██╗░░░░░███████╗░██████╗░███████╗███╗░░██╗██████╗░");
            System.out.println("╚══██╔══╝██║░░██║██╔════╝  ██╔══██╗██╔════╝██╔══██╗  ██║░░░░░██╔════╝██╔════╝░██╔════╝████╗░██║██╔══██╗");
            System.out.println("░░░██║░░░███████║█████╗░░  ██████╔╝█████╗░░██║░░██║  ██║░░░░░█████╗░░██║░░██╗░█████╗░░██╔██╗██║██║░░██║");
            System.out.println("░░░██║░░░██╔══██║██╔══╝░░  ██╔══██╗██╔══╝░░██║░░██║  ██║░░░░░██╔══╝░░██║░░╚██╗██╔══╝░░██║╚████║██║░░██║");
            System.out.println("░░░██║░░░██║░░██║███████╗  ██║░░██║███████╗██████╔╝  ███████╗███████╗╚██████╔╝███████╗██║░╚███║██████╔╝");
            System.out.println("░░░╚═╝░░░╚═╝░░╚═╝╚══════╝  ╚═╝░░╚═╝╚══════╝╚═════╝░  ╚══════╝╚══════╝░╚═════╝░╚══════╝╚═╝░░╚══╝╚═════╝░");

            System.out.println();
            System.out.println();

            System.out.println("----------------------------------------");
            System.out.println("Point and Click to navigate your player.\nUse F Key to attack enemies.\nUse E to open your inventory.");
            System.out.println("----------------------------------------");
            System.out.println("Have fun!");

            System.out.println("||       Settings     ||");
            System.out.println();
            System.out.println("Press y to use German");
            System.out.print("y/n: ");

            Scanner userInput = new Scanner(System.in);
            String useGerman = userInput.next().toLowerCase();

            switch (useGerman) {
                case "y":
                    Globals.useGermanLanguage = true;
                    break;
                case "n":
                    Globals.useGermanLanguage = false;
                    break;
                default:
                    System.out.println("Incorrect option. Continue with english language...");
                    Globals.useGermanLanguage = false;
                    break;
            }

            System.out.println();

            System.out.println("|| Set your Hero Name ||");
            System.out.print("Name: ");

            userInput = new Scanner(System.in);
            Globals.HERO_NAME = userInput.nextLine();

            System.out.println("Your Hero is named '" + Globals.HERO_NAME + "'.");
            System.out.println("||                    ||");
        }

        DialogUtils.initialize();

        // Create App instance
        App app = new App();

        // Setup callback
        app.levelManaging(); // connects the signal

        Animations.initialize();
        GameWindow.initializeWindow();
        GameWindow.repositionWindow(GameWindow.ScreenCenter);

        rootNode = new Node(
                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                new int[]{0, 0},
                Color.LIGHT_GRAY,
                true
        );

        GameWindow.MainWindow.add(rootNode.panel);
        GameWindow.MainWindow.repaint();

        Level_Prolog level0 = new Level_Prolog(
                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                new int[]{0, 0},
                Color.blue,
                0
        );

        app.initializeLevel(level0, "Prolog: A great Adventure");
//        level0.Debug_finishLevelWithOption(0);
//
//        Level_TheAmbush level1 = new Level_TheAmbush(
//                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
//                new int[]{0, 0},
//                Color.blue,
//                5
//        );
//
//        transitionLevelEffect();
//        Timer loadDelayTimer = new Timer(500, event -> {
//            app.initializeLevel(level1, "Chapter 5: The Ambush");
//        });
//        loadDelayTimer.setRepeats(false);
//        loadDelayTimer.start();
    }

    public void initializeLevel(Level level, String headlineText) {
        rootNode.addChild(level);
        level.loadLevel();

        level.showLevelHeadline(headlineText);

        // Connect level finish signal to app's listener
        level.setOnLevelFinished((int level_id, int lastChosenOption) -> {
            if (onLevelFinished != null) {
                onLevelFinished.onFinished(level, level_id, lastChosenOption);
            }
        });
    }

    public void levelManaging() {
        this.setOnLevelFinished((Level oldLevel, int levelId, int lastChosenOption) -> {
            System.out.println("Main App received signal from Level Id " + levelId + " with last chosen Option: " + lastChosenOption);
            Globals.resetPlayerStats(); // reset temporary effects and stats

            Timer unloadLevelTimer = new Timer(500, event -> {
                oldLevel.unloadLevel(); // clear level
            });

            unloadLevelTimer.setRepeats(false);
            unloadLevelTimer.start();

            // main switch
            switch (levelId) {
                case 0:
                    System.out.println("Prolog finished!");
                    transitionLevelEffect();

                    Timer loadDelayTimer = new Timer(500, event -> {
                        Level_TheBeginning level1 = new Level_TheBeginning(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                1
                        );

                        initializeLevel(level1, "Chapter 1: The Breakout");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
                case 1:
                    System.out.println("Chapter 1 finished!");
                    transitionLevelEffect();

                    if (lastChosenOption == 0) {
                        loadDelayTimer = new Timer(500, event -> {
                            Level_VillageBasement level2 = new Level_VillageBasement(
                                    new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                    new int[]{0, 0},
                                    Color.blue,
                                    2
                            );

                            initializeLevel(level2, "Chapter 2: Basement");
                            levelTransitionNode.moveNodeToTop();
                        });

                        loadDelayTimer.setRepeats(false);
                        loadDelayTimer.start();
                        break;
                    } else {
                        System.out.println("Skipping Chapter 2! Negotiated!");
                        transitionLevelEffect();

                        loadDelayTimer = new Timer(500, event -> {
                            Level_Mineshaft level3 = new Level_Mineshaft(
                                    new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                    new int[]{0, 0},
                                    Color.blue,
                                    3
                            );

                            initializeLevel(level3, "Chapter 3: Forest");
                            levelTransitionNode.moveNodeToTop();
                        });

                        loadDelayTimer.setRepeats(false);
                        loadDelayTimer.start();
                        break;
                    }
                case 2:
                    System.out.println("Chapter 2 finished!");
                    transitionLevelEffect();

                    loadDelayTimer = new Timer(500, event -> {
                        Level_Mineshaft level3 = new Level_Mineshaft(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                3
                        );

                        initializeLevel(level3, "Chapter 3: Forest");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
                case 3:
                    System.out.println("Chapter 3 finished!");
                    transitionLevelEffect();

                    loadDelayTimer = new Timer(500, event -> {
                        Level_TradingPlaza level4 = new Level_TradingPlaza(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                4
                        );

                        initializeLevel(level4, "Chapter 4: Trading Plaza");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
                case 4:
                    System.out.println("Chapter 4 finished!");
                    transitionLevelEffect();

                    loadDelayTimer = new Timer(500, event -> {
                        Level_TheAmbush level5 = new Level_TheAmbush(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                5
                        );

                        initializeLevel(level5, "Chapter 5: The Ambush");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
                case 5:
                    System.out.println("Chapter 5 finished!");
                    transitionLevelEffect();

                    loadDelayTimer = new Timer(500, event -> {
                        Level_DragonFight level6 = new Level_DragonFight(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                5
                        );

                        initializeLevel(level6, "Chapter 6: The Great Fight");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
                case 6:
                    System.out.println("Chapter 6 finished!");
                    transitionLevelEffect();

                    loadDelayTimer = new Timer(500, event -> {
                        Level_Epilogue level7 = new Level_Epilogue(
                                new int[]{GameWindow.WINDOW_SIZE.width, GameWindow.WINDOW_SIZE.height},
                                new int[]{0, 0},
                                Color.blue,
                                7
                        );

                        initializeLevel(level7, "Epilogue: The Reunion");
                        levelTransitionNode.moveNodeToTop();
                    });

                    loadDelayTimer.setRepeats(false);
                    loadDelayTimer.start();
                    break;
            }
        });
    }

    public static void transitionLevelEffect() {
        if (levelTransitionNode == null) {
            levelTransitionNode = new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0});
            rootNode.addChild(levelTransitionNode);

            levelTransitionNode.setBackgroundColor(Color.black);
            levelTransitionNode.setAlpha(0.0f);
        }

        levelTransitionNode.moveNodeToTop();

        levelTransitionNode.setOnTransitionFinished(new TransitionNode.TransitionListener() {
            @Override
            public void onFinished(TransitionNode node) {
                // Disconnect to avoid looping
                levelTransitionNode.removeTransitionFinishedListener();
            }
        });

        levelTransitionNode.startInOutTransition(Color.black, 1.0f, 1000);
    }

    public static void test() {
        RectangleCollider testcollider1 = new RectangleCollider(new int[]{80, 100}, new int[]{300, 300}, 0, true);
        rootNode.addChild(testcollider1);

        RectangleCollider testcollider2 = new RectangleCollider(new int[]{140, 100}, new int[]{360, 370}, 0, true);
        rootNode.addChild(testcollider2);

        //RectangleCollider testcollider3 = new RectangleCollider(new int[]{50, 60}, new int[]{495, 343}, 1, true);
        //root.addChild(testcollider3);

        //Boolean collision = CollisionsHandler.isPlayerCollidingWithWorld();
        //System.out.println("Collision detected: " + collision);

        LabelNode testLabel = new LabelNode("Hello World This is quite a big String that you need to print out", new int[]{240, 240}, new int[]{240, 240}, Color.black, 25);
        rootNode.addChild(testLabel);

        Button myButton = new Button(new int[]{100, 40}, new int[]{10, 10}, Color.blue, true, "TestButton", 25, Color.BLACK);
        rootNode.addChild(myButton);

        myButton.setOnPressed(button -> {
            System.out.println("Button pressed! Text: " + button.getText());
        });

        Dialog testDialog = new Dialog(
                new int[]{600, 250},
                new int[]{650, 600},
                Color.decode("#e0ab6e"),
                Color.decode("#916b3f"),
                15,
                "theBeginning",
                Color.decode("#301c05"),
                25
        );

        rootNode.addChild(testDialog);
    }
}
