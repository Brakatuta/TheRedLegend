package Node.Levels;

import Resources.Animations;
import Util.Tween;

import Node.Node;
import Node.LabelNode;
import Node.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Level extends Node {
    public int levelID = 0;
    public String levelHeadline = "Chapter 0";

    // level finished signal
    public LevelListener onLevelFinished; // Callback interface

    public void setOnLevelFinished(LevelListener listener) {
        this.onLevelFinished = listener;
    }

    // Callback interface
    public interface LevelListener {
        void onFinished(int level_id, int lastChosenOption);
    }
    //

    ActionListener run = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            _process();
        }
    };

    final Timer levelClock = new Timer(16, run);

    public void startLevelClock() {
        levelClock.setRepeats(true);
        levelClock.start();
    }

    public void stopLevelClock() {
        levelClock.stop();
    }

    public Level(int[] size, int[] relativePosition, Color backgroundColor, int levelID) {
        super(size, relativePosition, backgroundColor, false);
        this.levelID = levelID;

    }

    public void showLevelHeadline(String headLine) {
        int[] size = getSize();
        int[] headlineBackFillSize = new int[]{size[0] / 3, size[1] / 13};
        int[] headlinePosition = new int[]{(size[0] / 2) - (headlineBackFillSize[0] / 2), - 85};

        Node headlineBackFill = new Node(
                headlineBackFillSize,
                headlinePosition,
                Color.decode("#e0ab6e"),
                true
        );

        Node headlineFrontFill = new Node(
                new int[]{headlineBackFillSize[0] - 15, headlineBackFillSize[1] - 15},
                new int[]{15 / 2, 15 / 2},
                Color.decode("#916b3f"),
                true
        );
        headlineBackFill.addChild(headlineFrontFill);

        LabelNode headLineLabel = new LabelNode(headLine,
                new int[]{0, 0},
                new int[]{headlineBackFillSize[0] - 15, headlineBackFillSize[1] - 15},
                new Color(225, 166, 107, 255),
                35
        );
        headLineLabel.setOutline(new Color(66, 40, 25, 255),6.0f);
        headlineFrontFill.addChild(headLineLabel);

        headlineBackFill.setPosition(new int[]{headlinePosition[0], headlinePosition[1] - 150});

        getParent().addChild(headlineBackFill);

        Timer showUpTimer = new Timer(500, event -> {
            Tween headlineTween = new Tween();
            headlineTween.interpolatePosition(headlineBackFill, new int[]{headlinePosition[0], headlinePosition[1] + 150}, 0.35);
        });

        showUpTimer.setRepeats(false);
        showUpTimer.start();

        Timer HideTimer = new Timer(2750, event -> {
            Tween headlineTween = new Tween();
            headlineTween.interpolatePosition(headlineBackFill, new int[]{headlinePosition[0], headlinePosition[1] - 150}, 0.35);
        });

        HideTimer.setRepeats(false);
        HideTimer.start();
    }

    public void spawnPositionMarkersSequenced(ArrayList<int[]> markerPositions, int spawnDelayInMS, int showAmountMilliseconds) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            final int amountToSpawn = markerPositions.size();
            int amountSpawned = 0;

            @Override
            public void run() {
                spawnPositionMarker(markerPositions.get(amountSpawned), showAmountMilliseconds);
                amountSpawned++;

                if (amountSpawned >= amountToSpawn) {
                    scheduler.shutdown();
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, spawnDelayInMS, spawnDelayInMS, TimeUnit.MILLISECONDS);
    }

    public void spawnPositionMarker(int[] positionToMark, int showAmountMilliseconds) {
        positionToMark = new int[]{positionToMark[0] - 32, positionToMark[1] -64};

        Sprite positionMarker = new Sprite(Animations.PositionMarkerFrames.get("animated_marker"), new int[]{64, 64}, positionToMark, new int[]{0, 0}, 200, true);
        getParent().addChild(positionMarker);

        Timer delayTimer = new Timer(showAmountMilliseconds, event -> {
            getParent().removeChild(positionMarker);
        });

        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void Debug_finishLevelWithOption(int option) {
        Timer debugFinishLevelTimer = new Timer(500, event -> {
            onLevelFinished.onFinished(levelID, option);
        });

        debugFinishLevelTimer.setRepeats(false);
        debugFinishLevelTimer.start();
    }

    // load (overwrite)
    public void loadLevel() {

    }

    // unload (overwrite)
    public void unloadLevel() {
        stopLevelClock();
        LevelController.killCurrentPlayer();
        LevelController.unloadLevel(this);
    }

    // run this every 16 ms (60Fps)
    public void _process() {

    }
}
