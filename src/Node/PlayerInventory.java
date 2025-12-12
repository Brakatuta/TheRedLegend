package Node;

import Util.GameWindow;
import Util.Globals;
import Util.Tween;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Timer;


public class PlayerInventory extends Node {
    private static KeyListener inventoryKeyListener;

    private Node inventoryWindow;

    private LabelNode playerHealthLabel;
    private LabelNode playerSwordLabel;

    private Node coinsSlot;
    private Node silverSlot;
    private Node goldSlot;
    private Node crystalSlot;
    private Node diamondsSlot;
    private Node strengthPotionSlot;
    private Node resistancePotionSlot;
    private Node speedPotionSlot;

    private Node potionEffectWindow;

    private boolean isOpen = false;

    public void openClose() {
        Tween inventoryTween = new Tween();
        if (!isOpen) {
            this.setVisibility(true);
            inventoryTween.interpolatePosition(this, new int[]{0, 0}, 0.25);
            isOpen = true;
        } else {
            inventoryTween.interpolatePosition(this, new int[]{0, 1500}, 0.25);
            Timer debounceTimer = new Timer(250, finished -> this.setVisibility(false));
            debounceTimer.setRepeats(false);
            debounceTimer.start();
            isOpen = false;
        }
    }

    public void addInventoryListener() {
        inventoryKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_E) {
                    openClose();
                }
            }
        };
        GameWindow.MainWindow.addKeyListener(inventoryKeyListener);
    }

    public void removeInventoryListener() {
        if (inventoryKeyListener != null) {
            GameWindow.MainWindow.removeKeyListener(inventoryKeyListener);
            inventoryKeyListener = null;
        }
    }

    public PlayerInventory() {
        super(new int[]{1912, 1192}, new int[]{0, 0}, Color.white, false);

        setPosition(new int[]{0, 1500});

        addInventoryListener();

        int[] inventoryWindowSize = new int[]{(int)(1912 * 0.75), (int)(1192 * 0.75)};
        int[] inventoryWindowPosition = new int[]{(1912 - inventoryWindowSize[0]) / 2, (1192 - inventoryWindowSize[1]) / 2};

        inventoryWindow = new Node(inventoryWindowSize, inventoryWindowPosition, new Color(153, 99, 47, 255), true);
        addChild(inventoryWindow);

        Node inventoryWindowInner = new Node(
                new int[]{inventoryWindowSize[0] - 16, inventoryWindowSize[1] - 16},
                new int[]{8, 8},
                new Color(66, 40, 25, 255),
                true
        );

        inventoryWindow.addChild(inventoryWindowInner);

        int[] inventoryTopBannerSize = new int[]{inventoryWindowSize[0] / 3, 100};
        int[] inventoryTopBannerPosition =  new int[]{(inventoryWindowSize[0] - inventoryWindowSize[0] / 3) / 2, -25};

        Node inventoryTopBanner = new Node(inventoryTopBannerSize, inventoryTopBannerPosition, new Color(153, 99, 47, 255), true);
        inventoryWindow.addChild(inventoryTopBanner);

        Node inventoryTopBannerInner = new Node(
                new int[]{inventoryTopBannerSize[0] - 16, inventoryTopBannerSize[1] - 16},
                new int[]{8, 8},
                new Color(66, 40, 25, 255),
                true
        );
        inventoryTopBanner.addChild(inventoryTopBannerInner);

        LabelNode bannerLabel = new LabelNode(
                "Your Inventory",
                new int[]{0, 8},
                new int[]{inventoryTopBannerSize[0] - 16, inventoryTopBannerSize[1] - 16},
                new Color(225, 166, 107, 255),
                50
        );
        inventoryTopBannerInner.addChild(bannerLabel);

        Node inventorySidePlayerPanel = new Node(new int[]{470, inventoryWindowSize[1] - 85}, new int[]{inventoryWindowSize[0] - 470 - 8, 85 - 8}, new Color(153, 99, 47, 255), true);
        inventoryWindow.addChild(inventorySidePlayerPanel);

        Node inventorySidePlayerPanelInner = new Node(new int[]{470 - 16, inventoryWindowSize[1] - 85 - 16}, new int[]{8, 8}, new Color(66, 40, 25, 255), true);
        inventorySidePlayerPanel.addChild(inventorySidePlayerPanelInner);

        Node playerEquipmentBannerBackground = new Node(new int[]{(470 - 16) / 2, 65}, new int[]{((470 - 16) - (470 - 16) / 2) / 2, 8}, new Color(153, 99, 47, 255), true);
        inventorySidePlayerPanelInner.addChild(playerEquipmentBannerBackground);

        LabelNode playerEquipmentLabel = new LabelNode("Equipment", new int[]{0, 0}, new int[]{(470 - 16) / 2, 65}, new Color(225, 166, 107, 255), 30);
        playerEquipmentBannerBackground.addChild(playerEquipmentLabel);

        // sword slot
        Node swordSlot = new Node(new int[]{256, 256}, new int[]{16, 516}, new Color(225, 166, 107, 255), true);
        inventorySidePlayerPanelInner.addChild(swordSlot);

        ImageNode swordImage = new ImageNode("textures/items/SwordUpgrade.png", new int[]{256, 256}, new int[]{0, 0});
        swordSlot.addChild(swordImage);

        Node swordLevelLabelBackground = new Node(new int[]{256 - 16, 64}, new int[]{8, 256 - 64 - 8}, new Color(66, 40, 25, 255), true);
        swordSlot.addChild(swordLevelLabelBackground);

        playerSwordLabel = new LabelNode(
                "Attack: " + Globals.currentMinDamage + " - " + Globals.currentMaxDamage,
                new int[]{0, 0},
                new int[]{256 - 16, 64},
                new Color(225, 166, 107, 255),
                30
        );
        swordLevelLabelBackground.addChild(playerSwordLabel);
        //

        ImageNode playerImage = new ImageNode("textures/maincharacter/idle/idle000.png",  new int[]{231, 300}, new int[]{100, 300});
        inventorySidePlayerPanelInner.addChild(playerImage);

        // armor slot
        Node armorSlot = new Node(new int[]{256, 256}, new int[]{99 + 85, 100}, new Color(225, 166, 107, 255), true);
        inventorySidePlayerPanelInner.addChild(armorSlot);

        ImageNode armorImage = new ImageNode("textures/items/ArmorUpgrade.png", new int[]{256, 256}, new int[]{0, 0});
        armorSlot.addChild(armorImage);

        Node armorLevelLabelBackground = new Node(new int[]{256 - 16, 64}, new int[]{8, 256 - 64 - 8}, new Color(66, 40, 25, 255), true);
        armorSlot.addChild(armorLevelLabelBackground);

        playerHealthLabel = new LabelNode(
                "Health: " + Globals.currentHealthMax + " ❤",
                new int[]{0, 0},
                new int[]{256 - 16, 64},
                new Color(225, 166, 107, 255),
                30
        );
        armorLevelLabelBackground.addChild(playerHealthLabel);
        //

        // potion window
        potionEffectWindow = new Node(
                new int[]{875, 200},
                new int[]{inventoryWindowPosition[0] + 45, inventoryWindowPosition[1] + 675},
                new Color(153, 99, 47, 255),
                true
        );
        addChild(potionEffectWindow);

        Node potionWindowInner = new Node(
                new int[]{875 - 16, 200 - 15},
                new int[]{8, 8},
                new Color(66, 40, 25, 255),
                true
        );
        potionEffectWindow.addChild(potionWindowInner);

        ImageNode potionShowcase = new ImageNode("textures/items/NoPotionActive.png", new int[]{200, 200}, new int[]{0, 0});
        potionWindowInner.addChild(potionShowcase);

        LabelNode currentActivePotionInfoLabel = new LabelNode(
                "No active potion Effect...",
                new int[]{235, 0},
                new int[]{875 - 16 - 235, 64},
                new Color(66, 40, 25, 255),
                35
        );
        currentActivePotionInfoLabel.setOutline(new Color(225, 166, 107, 255), 5.0f);
        potionWindowInner.addChild(currentActivePotionInfoLabel);

        LabelNode currentActivePotionEffectInfoLabel = new LabelNode(
                "No potion effect is active. So there is no effect applied to you.",
                new int[]{235, 64},
                new int[]{875 - 16 - 235, 64},
                new Color(66, 40, 25, 255),
                25
        );
        currentActivePotionEffectInfoLabel.setOutline(new Color(225, 166, 107, 255), 4.0f);
        potionWindowInner.addChild(currentActivePotionEffectInfoLabel);

        LabelNode currentPotionTimeLeft = new LabelNode(
                "0 seconds left.",
                new int[]{235, 128},
                new int[]{875 - 16 - 235, 64},
                new Color(210, 25, 236, 255),
                25
        );
        currentPotionTimeLeft.setOutline(new Color(37, 4, 46, 255), 4.0f);
        potionWindowInner.addChild(currentPotionTimeLeft);
        //

        // inventory slots
        coinsSlot = createInventorySlot("textures/items/Coins.png", "Coins", new int[]{45, 100});
        inventoryWindow.addChild(coinsSlot);

        silverSlot = createInventorySlot("textures/items/Silver.png", "Silver", new int[]{45 + 200 + 25, 100});
        inventoryWindow.addChild(silverSlot);

        goldSlot = createInventorySlot("textures/items/Gold.png", "Gold", new int[]{45 + 200 * 2 + 50, 100});
        inventoryWindow.addChild(goldSlot);

        crystalSlot = createInventorySlot("textures/items/Crystal.png", "Crystal", new int[]{45 + 200 * 3 + 75, 100});
        inventoryWindow.addChild(crystalSlot);

        diamondsSlot = createInventorySlot("textures/items/Diamonds.png", "Diamonds", new int[]{45, 100 + 200 + 25});
        inventoryWindow.addChild(diamondsSlot);

        strengthPotionSlot = createInventorySlot("textures/items/PotionOfStrength.png", "Potion of Strength", new int[]{45 + 200 + 25, 100 + 200 + 25});
        inventoryWindow.addChild(strengthPotionSlot);
        Button strengthPotionButton = (Button)strengthPotionSlot.getAllChildren().get(3).getAllChildren().get(0);
        strengthPotionButton.setOnPressed(button -> {
            applyPotionEffect("strength");
        });

        resistancePotionSlot = createInventorySlot("textures/items/PotionOfResistance.png", "Potion of Resistance", new int[]{45 + 200 * 2 + 50, 100 + 200 + 25});
        inventoryWindow.addChild(resistancePotionSlot);
        Button resistancePotionButton = (Button)resistancePotionSlot.getAllChildren().get(3).getAllChildren().get(0);
        resistancePotionButton.setOnPressed(button -> {
            applyPotionEffect("resistance");
        });

        speedPotionSlot = createInventorySlot("textures/items/PotionOfSpeed.png", "Potion of Speed", new int[]{45 + 200 * 3 + 75, 100 + 200 + 25});
        inventoryWindow.addChild(speedPotionSlot);
        Button speedPotionButton = (Button)speedPotionSlot.getAllChildren().get(3).getAllChildren().get(0);
        speedPotionButton.setOnPressed(button -> {
            applyPotionEffect("speed");
        });
        //
    }

    private Node createInventorySlot(String itemShowCasePath, String itemName, int[] containerPosition) {
        Node itemContainer = new Node(new int[]{200, 200}, containerPosition, new Color(153, 99, 47, 255), true);

        ImageNode itemShowcase = new ImageNode(itemShowCasePath, new int[]{192, 192}, new int[]{4, 64});
        itemContainer.addChild(itemShowcase);

        Node itemNameLabelBackground = new Node(new int[]{200, 64}, new int[]{0, 0}, new Color(225, 166, 107, 255), true);
        itemContainer.addChild(itemNameLabelBackground);

        LabelNode itemNameLabel = new LabelNode(
                getItemString(itemName),
                new int[]{0, 0},
                new int[]{200, 64},
                new Color(225, 166, 107, 255),
                25
        );
        itemNameLabel.setOutline(new Color(66, 40, 25, 255),4.0f);
        itemContainer.addChild(itemNameLabel);

        // usability for potions
        if (itemShowCasePath.toLowerCase().contains("potion")) {
            itemShowcase.setPosition(new int[]{4, 0});
            itemNameLabelBackground.setVisibility(false);

            Node useButtonBackground = new Node(new int[]{200, 64}, new int[]{0, 136}, new Color(225, 166, 107, 255), true);
            itemContainer.addChild(useButtonBackground);

            Button useItemButton = new Button(
                    new int[]{200 - 16, 64 -16},
                    new int[]{8, 8},
                    new Color(18, 112, 35, 255),
                    true,
                    "use Potion",
                    25,
                    new Color(39, 234, 42, 255)
            );
            useButtonBackground.addChild(useItemButton);
        }

        return itemContainer;
    }

    private String getItemString(String itemName) {
        int itemAmount = 0;

        switch (itemName) {
            case "Coins":
                itemAmount = Globals.coins;
                break;
            case "Silver":
                itemAmount = Globals.silver;
                break;
            case "Gold":
                itemAmount = Globals.gold;
                break;
            case "Crystal":
                itemAmount = Globals.crystals;
                break;
            case "Diamonds":
                itemAmount = Globals.diamonds;
                break;
            case "Potion of Strength":
                itemAmount = Globals.strengthPotion;
                break;
            case "Potion of Resistance":
                itemAmount = Globals.resistancePotion;
                break;
            case "Potion of Speed":
                itemAmount = Globals.speedPotion;
                break;
            default:
                itemAmount = -1;
        }

        return itemName + " (" + itemAmount + ")";
    }

    private void applyPotionEffect(String effectType) {
        // Prevent using another potion while one is active
        if (Globals.strengthEffectActive || Globals.resistanceEffectActive || Globals.speedEffectActive) return;

        String potionImagePath = "textures/items/NoPotionActive.png";
        String potionName = "";
        String info = "";
        int durationMS = 1000;
        Runnable onPotionEnd = () -> {};

        switch (effectType) {
            case "strength":
                if (Globals.strengthPotion >= 1) {
                    Globals.strengthPotion -= 1;
                    potionImagePath = "textures/items/PotionOfStrength.png";
                    potionName = "Potion of Strength";
                    info = "This potion increases your strength for 30 seconds.";
                    durationMS = 30000;

                    Globals.strengthEffectActive = true;
                    Globals.currentMinDamage += 15;
                    Globals.currentMaxDamage += 25;

                    onPotionEnd = () -> {
                        Globals.strengthEffectActive = false;
                        Globals.currentMinDamage -= 15;
                        Globals.currentMaxDamage -= 25;
                    };
                } else return;
                break;

            case "resistance":
                if (Globals.resistancePotion >= 1) {
                    Globals.resistancePotion -= 1;
                    potionImagePath = "textures/items/PotionOfResistance.png";
                    potionName = "Potion of Resistance";
                    info = "This potion increases your health for 60 seconds.";
                    durationMS = 45000;

                    Globals.resistanceEffectActive = true;
                    Globals.currentHealthMax += 100;

                    onPotionEnd = () -> {
                        Globals.resistanceEffectActive = false;
                        Globals.currentHealthMax -= 100;
                    };
                } else return;
                break;

            case "speed":
                if (Globals.speedPotion >= 1) {
                    Globals.speedPotion -= 1;
                    potionImagePath = "textures/items/PotionOfSpeed.png";
                    potionName = "Potion of Speed";
                    info = "This potion makes you faster for 60 seconds.";
                    durationMS = 45000;

                    Globals.speedEffectActive = true;
                    Globals.currentSpeedMultiplier = 1.5;

                    onPotionEnd = () -> {
                        Globals.speedEffectActive = false;
                        Globals.currentSpeedMultiplier = 1.0;
                    };
                } else return;
                break;
        }

        // Update potion window visuals
        ImageNode potionShowcase = (ImageNode)potionEffectWindow.getAllChildren().get(0).getAllChildren().get(0);
        potionShowcase.swapImage(potionImagePath);

        LabelNode currentActivePotionInfoLabel = (LabelNode)potionEffectWindow.getAllChildren().get(0).getAllChildren().get(1);
        currentActivePotionInfoLabel.setText(potionName);

        LabelNode currentActivePotionEffectInfoLabel = (LabelNode)potionEffectWindow.getAllChildren().get(0).getAllChildren().get(2);
        currentActivePotionEffectInfoLabel.setText(info);

        LabelNode currentPotionTimeLeft = (LabelNode)potionEffectWindow.getAllChildren().get(0).getAllChildren().get(3);

        // Timer setup
        AtomicInteger timeLeft = new AtomicInteger(durationMS / 1000);
        currentPotionTimeLeft.setText(timeLeft + " Seconds left");

        Runnable finalOnPotionEnd = onPotionEnd;

        Timer durationTimer = new Timer(1000, e -> {
            int remaining = timeLeft.decrementAndGet();
            if (remaining > 0) {
                currentPotionTimeLeft.setText(remaining + " Seconds left");
            } else {
                ((Timer)e.getSource()).stop();

                // Reset potion visuals
                potionShowcase.swapImage("textures/items/NoPotionActive.png");
                currentActivePotionInfoLabel.setText("No active potion Effect...");
                currentActivePotionEffectInfoLabel.setText("No potion effect is active. So there is no effect applied to you.");
                currentPotionTimeLeft.setText("0 seconds left.");

                // Revert stats
                finalOnPotionEnd.run();

                // Refresh inventory labels
                updateInventory();
            }
        });

        durationTimer.setInitialDelay(1000);
        durationTimer.start();

        // Immediately refresh inventory (after consumption)
        updateInventory();
    }

    public void updateInventory() {
        playerHealthLabel.setText("Health: " + Globals.currentHealthMax + " ❤");
        playerSwordLabel.setText("Attack: " + Globals.currentMinDamage + " - " + Globals.currentMaxDamage);

        ((LabelNode) coinsSlot.getAllChildren().get(2)).setText(getItemString("Coins"));
        ((LabelNode) silverSlot.getAllChildren().get(2)).setText(getItemString("Silver"));
        ((LabelNode) goldSlot.getAllChildren().get(2)).setText(getItemString("Gold"));
        ((LabelNode) crystalSlot.getAllChildren().get(2)).setText(getItemString("Crystal"));
        ((LabelNode) diamondsSlot.getAllChildren().get(2)).setText(getItemString("Diamonds"));
        ((LabelNode) strengthPotionSlot.getAllChildren().get(2)).setText(getItemString("Potion of Strength"));
        ((LabelNode) resistancePotionSlot.getAllChildren().get(2)).setText(getItemString("Potion of Resistance"));
        ((LabelNode) speedPotionSlot.getAllChildren().get(2)).setText(getItemString("Potion of Speed"));
    }
}