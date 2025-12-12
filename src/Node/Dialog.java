package Node;

import Resources.DialogLine;
import Resources.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


public class Dialog extends Node {
    private String dialogType = "";
    private int dialogSize = 0;
    private int dialogLineIndex = 0;
    private int chosenOption = -1;
    public int lastChosenOption = 0;

    final Color backgroundColor;

    final LabelNode dialogLabel;

    private Boolean buttonDebounce = false;

    final Button nextButton;

    final Node sidePanelOptionsWindow;

    final Button Option1Button;
    final Button Option2Button;
    final Button Option3Button;

    private String reaction0;
    private String reaction1;
    private String reaction2;

    boolean nextButtonDebounce = false;


    // finished signal
    public interface DialogFinishedListener {
        void onDialogFinished(int lastChosenOption);
    }

    private final java.util.List<DialogFinishedListener> listeners = new ArrayList<>();

    public void addDialogFinishedListener(DialogFinishedListener listener) {
        listeners.add(listener);
    }

    private void fireDialogFinishedEvent(int option) {
        for (DialogFinishedListener listener : listeners) {
            listener.onDialogFinished(option);
        }
    }

    // progress signal
    public interface DialogProgressListener {
        void onDialogProgress(int progress);
    }

    private final java.util.List<DialogProgressListener> progress_listeners = new ArrayList<>();

    public void addDialogProgressListener(DialogProgressListener listener) {
        progress_listeners.add(listener);
    }

    private void fireDialogProgressEvent(int progress) {
        for (DialogProgressListener listener: progress_listeners) {
            listener.onDialogProgress(progress);
        }
    }
    //

    public Dialog(int[] size, int[] relativePosition, Color backColor, Color topColor, int borderThickness, String dialog, Color dialogTextColor, int dialogTextSize) {
        // allow overlapping of the next button (size adjustment)
        super(new int[]{size[0] + size[0] / 2, size[1] + size[1] / 6}, relativePosition, Color.white, false);

        backgroundColor = backColor;

        dialogType = dialog;
        dialogSize = DialogUtils.getDialogSize(dialog);

        Node backFill = new Node(new int[]{size[0], size[1] + size[1] / 6}, new int[]{0, 0}, backgroundColor, true);
        addChild(backFill);

        // Panel layout must be null to allow absolute positioning
        panel.setLayout(null);

        // Inner dialog panel (actual visible dialog)
        int[] frontPanelSize = new int[]{size[0] - borderThickness, size[1] - borderThickness};
        int[] frontPanelPosition = new int[]{borderThickness / 2, borderThickness / 2};

        Node frontPanel = new Node(frontPanelSize, frontPanelPosition, topColor, true);
        addChild(frontPanel);

        // Next Button setup
        int[] buttonSize = new int[]{size[0] / 4, size[1] / 4};
        int[] buttonPosition = new int[]{
                (size[0] - buttonSize[0]) / 2,
                size[1] - (buttonSize[1] / 2)  // overlap by half the button height
        };

        int[] nextButtonBackgroundSize = new int[]{(int)(buttonSize[0] * 1.25), (int)(buttonSize[1] * 1.25)};
        int[] nextButtonBackgroundPosition = new int[]{
                buttonPosition[0] - (nextButtonBackgroundSize[0] - buttonSize[0]) / 2,
                buttonPosition[1] - (nextButtonBackgroundSize[1] - buttonSize[1]) / 2
        };

        Node nextButtonBackground = new Node(nextButtonBackgroundSize, nextButtonBackgroundPosition, backgroundColor, true);
        addChild(nextButtonBackground);

        nextButton = new Button(buttonSize, buttonPosition, topColor, true, "Next", dialogTextSize - 5, backgroundColor);
        nextButton.getPanel().setBounds(buttonPosition[0], buttonPosition[1], buttonSize[0], buttonSize[1]);
        addChild(nextButton);

        dialogLabel = new LabelNode("test", new int[]{8, 8}, new int[]{frontPanelSize[0] - 4, frontPanelSize[1] - 4}, dialogTextColor, dialogTextSize);
        addChild(dialogLabel);

        try {
            renderNextDialogLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        nextButton.setOnPressed(button -> {
            if (nextButtonDebounce) {
                return;
            }

            nextButtonDebounce = true;

            Timer nextButtonDebounceTimer = new Timer(350, finished -> {
                nextButtonDebounce = false;
            });

            nextButtonDebounceTimer.setRepeats(false);
            nextButtonDebounceTimer.start();

            nextButton.setBackgroundColor(Color.decode("#d4b28c"));
            try {
                renderNextDialogLine();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            new Timer(150, event -> {
                nextButton.setBackgroundColor(topColor);
            }).start();
        });
        //

        // Dialog Options Window construction
        int[] SidePanelSize = new int[]{
                (int)(size[0] / 2.25),
                (int)(size[1] * 0.8)
        };
        int[] SidePanelPosition = new int[]{
                (int)(size[0] + (size[0] * 0.025)),
                (int)(size[1] * 0.175)
        };

        sidePanelOptionsWindow = new Node(
                new int[]{SidePanelSize[0] + borderThickness, SidePanelSize[1] + borderThickness},
                new int[]{SidePanelPosition[0] - borderThickness / 2, SidePanelPosition[1] - borderThickness / 2},
                backgroundColor,
                true
        );
        addChild(sidePanelOptionsWindow);

        Node SidePanelOptionsForeground = new Node(SidePanelSize, new int[]{borderThickness / 2, borderThickness / 2}, topColor, true);
        sidePanelOptionsWindow.addChild(SidePanelOptionsForeground);

        int[] SidePanelInfoLabelWindowSize = new int[]{
                (int)(SidePanelSize[0] / 1.15),
                SidePanelSize[1] / 4
        };

        Node SidePanelInfoLabelWindow = new Node(SidePanelInfoLabelWindowSize, new int[]{(int)(SidePanelSize[0] / 10.25), 0}, backgroundColor, true);
        sidePanelOptionsWindow.addChild(SidePanelInfoLabelWindow);

        Node SidePanelInfoLabelFront = new Node(
                new int[]{SidePanelInfoLabelWindowSize[0] - borderThickness / 3, SidePanelInfoLabelWindowSize[1] - borderThickness / 3},
                new int[]{(int)((double)borderThickness / 6), borderThickness / 6},
                topColor,
                true
        );
        SidePanelInfoLabelWindow.addChild(SidePanelInfoLabelFront);

        LabelNode SidePanelInfoLabel = new LabelNode(
                "Choose Option",
                new int[]{0, 0},
                new int[]{SidePanelInfoLabelWindowSize[0] - borderThickness / 3, SidePanelInfoLabelWindowSize[1] - borderThickness / 3},
                dialogTextColor,
                dialogTextSize + 5
        );
        SidePanelInfoLabelFront.addChild(SidePanelInfoLabel);

        Option1Button = new Button(
                new int[]{SidePanelInfoLabelWindowSize[0] - borderThickness / 3, SidePanelInfoLabelWindowSize[1] - borderThickness / 3},
                new int[]{(int)(SidePanelSize[0] / 10.25), (int)(SidePanelSize[1] * 0.275)},
                backgroundColor,
                true,
                "Choose this Option 1", // placeholder text
                (int)(dialogTextSize / 1.5),
                dialogTextColor
        );

        Option2Button = new Button(
                new int[]{SidePanelInfoLabelWindowSize[0] - borderThickness / 3, SidePanelInfoLabelWindowSize[1] - borderThickness / 3},
                new int[]{(int)(SidePanelSize[0] / 10.25), (int)(SidePanelSize[1] * 0.3 + (SidePanelInfoLabelWindowSize[1] - (double)borderThickness / 2.75))},
                backgroundColor,
                true,
                "Choose this Option 2", // placeholder text
                (int)(dialogTextSize / 1.5),
                dialogTextColor
        );

        Option3Button = new Button(
                new int[]{SidePanelInfoLabelWindowSize[0] - borderThickness / 3, SidePanelInfoLabelWindowSize[1] - borderThickness / 3},
                new int[]{(int)(SidePanelSize[0] / 10.25), (int)(SidePanelSize[1] * 0.3 + 2 * (SidePanelInfoLabelWindowSize[1] - (double)borderThickness / 5))},
                backgroundColor,
                true,
                "Choose this Option 3", // placeholder text
                (int)(dialogTextSize / 1.5),
                dialogTextColor
        );

        sidePanelOptionsWindow.addChild(Option1Button);
        sidePanelOptionsWindow.addChild(Option2Button);
        sidePanelOptionsWindow.addChild(Option3Button);

        Option1Button.setOnPressed(button -> {
            pressOptionButton(0);
        });

        Option2Button.setOnPressed(button -> {
            pressOptionButton(1);
        });

        Option3Button.setOnPressed(button -> {
            pressOptionButton(2);
        });

        sidePanelOptionsWindow.setVisibility(false);
        //
    }

    public void setFinishDialogText(String finishText) {
        nextButton.setText(finishText);
    }

    private void pressOptionButton(int buttonIndex) {
        if (chosenOption != -1) {
            return; // already chosen
        }

        chosenOption = buttonIndex;
        lastChosenOption = buttonIndex;

        try {
            DialogLine line = DialogUtils.getDialogLineByIndex(dialogType, dialogLineIndex);
            assert line != null;

            // Highlight the selected option button and show the answer
            switch (buttonIndex) {
                case 0:
                    Option1Button.setBackgroundColor(Color.decode("#d4b28c"));
                    dialogLabel.setText(line.speaker.split("-")[0] + ": " + reaction0);
                    break;
                case 1:
                    Option2Button.setBackgroundColor(Color.decode("#d4b28c"));
                    dialogLabel.setText(line.speaker.split("-")[0] + ": " + reaction1);
                    break;
                case 2:
                    Option3Button.setBackgroundColor(Color.decode("#d4b28c"));
                    dialogLabel.setText(line.speaker.split("-")[0] + ": " + reaction2);
                    break;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Hide options panel, advance dialog line, and render next line
        sidePanelOptionsWindow.setVisibility(false);
        nextButton.setVisibility(true);
    }

    private void renderNextDialogLine() throws Exception {
        if (buttonDebounce) {
            return;
        }

        if (dialogLineIndex == dialogSize) {
            fireDialogFinishedEvent(lastChosenOption);
            removeFromParent();
        } else if (dialogLineIndex == dialogSize - 1) {
            DialogLine line = DialogUtils.getDialogLineByIndex(dialogType, dialogLineIndex);
            assert line != null;
            dialogLabel.setText(line.speaker.split("-")[0] + ": " + line.value);
            nextButton.setText("Finish Dialog");
        } else {
            DialogLine line = DialogUtils.getDialogLineByIndex(dialogType, dialogLineIndex);
            if (line == null) {
                dialogLabel.setText("Dialog line is empty!");
            } else {
                if (line.value instanceof ArrayList<?>) { // show the matching response for the chosen User Dialog Answer Option
                    Option1Button.setBackgroundColor(backgroundColor);
                    Option2Button.setBackgroundColor(backgroundColor);
                    Option3Button.setBackgroundColor(backgroundColor);

                    @SuppressWarnings("unchecked")
                    java.util.List<String> dialogAnswers = (java.util.List<String>) line.value;
                    String answer = dialogAnswers.get(chosenOption);
                    dialogLabel.setText(line.speaker.split("-")[0] + ": " + answer);

                } else if (line.value instanceof Map) { // show the answer options the player can choose between
                    chosenOption = -1; // reset chosen option

                    @SuppressWarnings("unchecked")
                    Map<String, String> options = (Map<String, String>) line.value;

                    Option1Button.setText("null");

                    Option2Button.setText("null");

                    Option3Button.setVisibility(true);
                    Option3Button.setText("null");

                    Option1Button.setBackgroundColor(backgroundColor);
                    Option2Button.setBackgroundColor(backgroundColor);
                    Option3Button.setBackgroundColor(backgroundColor);

                    int index = 0;
                    for (Map.Entry<String, String> opt : options.entrySet()) {
                        switch (index) {
                            case 0:
                                Option1Button.setText(opt.getKey());
                                reaction0 = opt.getValue();
                                break;
                            case 1:
                                Option2Button.setText(opt.getKey());
                                reaction1 = opt.getValue();
                                break;
                            case 2:
                                Option3Button.setText(opt.getKey());
                                reaction2 = opt.getValue();
                                break;
                        }
                        index++;
                        if (index >= 3) break; // Only support up to 3 options
                    }

                    if (Option3Button.getText() == "null") {
                        Option3Button.setVisibility(false);
                    }

                    nextButton.setVisibility(false);
                    sidePanelOptionsWindow.setVisibility(true);

                } else if (line.value instanceof String) {
                   dialogLabel.setText(line.speaker.split("-")[0] + ": " + line.value);

                } else {
                    System.out.println(line.value + "    "  + line.value.getClass());
                }
            }
        }

        fireDialogProgressEvent(dialogLineIndex);
        dialogLineIndex += 1;

        new Timer(150, event -> {
            buttonDebounce = false;
        }).start();
    }
}