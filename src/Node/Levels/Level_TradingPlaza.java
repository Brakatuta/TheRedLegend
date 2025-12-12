package Node.Levels;

import Node.*;
import Node.Button;
import Node.Dialog;
import Physics.CollisionsHandler;
import Physics.RectangleCollider;
import Util.ExtendedMath;
import Util.Globals;

import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;


public class Level_TradingPlaza extends Level {
    static public final Map<String, List<Node>> levelStructure = new LinkedHashMap<>();
    private static final List<RectangleCollider> colliders = new ArrayList<>();

    private boolean inMine = false;

    private TransitionNode transitionNode;

    private Player player;

    private RectangleCollider shopCollider;
    private Button shopOpenButton;
    private Node shopWindow;
    private LabelNode coinsLabel;

    private final int SILVER_VALUE = 20;
    private final int GOLD_VALUE = 50;
    private final int CRYSTAL_VALUE = 75;
    private final int DIAMONDS_VALUE = 125;

    private final int STRENGTH_POTION_PRICE = 550;
    private final int RESISTANCE_POTION_PRICE = 450;
    private final int SPEED_POTION_PRICE = 250;

    private final int SWORD_UPGRADE_PRICE = 1150;
    private final int ARMOR_UPGRADE_PRICE = 1000;

    private RectangleCollider enterMineCollider;
    private Button enterMineButton;

    private RectangleCollider exitMineCollider;
    private Button exitMineButton;

    private PlayerInventory inventoryWindow;

    private Map<ImageNode, Object[]> mineralsData = new LinkedHashMap<>(); // mineral : [collider, max durability, current durability]

    public static final Map<String, Integer> MINERAL_STATS = Map.of(
            "textures/items/Silver.png", 100,
            "textures/items/Gold.png", 150,
            "textures/items/Crystal.png", 300,
            "textures/items/Diamonds.png", 500
    );

    private boolean mineralMiningDebounce = false;

    private RectangleCollider levelExitCollider;
    private Button levelExitButton;

    static {
        levelStructure.put("background", Arrays.asList(
                new ImageNode("textures/backgrounds/tradingplace/CaveLabyrinthBackground.png", new int[]{1912, 1162}, new int[]{0, 0}),
                new ImageNode("textures/backgrounds/tradingplace/TradingPlaceBackground.png", new int[]{1912, 1162}, new int[]{0, 0})
        ));

        levelStructure.put("minerals", Arrays.asList(
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{901, 189}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{980, 125}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{966, 477}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{497, 477}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{148, 680}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{1120, 541}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{1754, 998}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{1367, 942}),
                new ImageNode("textures/items/Silver.png", new int[]{64, 64}, new int[]{924, 680}),

                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{1662, 137}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{1424, 772}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{1353, 401}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{684, 689}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{433, 609}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{178, 156}),
                new ImageNode("textures/items/Gold.png", new int[]{64, 64}, new int[]{551, 125}),

                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{309, 1005}),
                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{465, 1005}),
                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{1132, 1005}),
                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{1676, 467}),
                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{1056, 689}),
                new ImageNode("textures/items/Crystal.png", new int[]{64, 64}, new int[]{1194, 399}),

                new ImageNode("textures/items/Diamonds.png", new int[]{64, 64}, new int[]{901, 998}),
                new ImageNode("textures/items/Diamonds.png", new int[]{64, 64}, new int[]{1726, 778}),
                new ImageNode("textures/items/Diamonds.png", new int[]{64, 64}, new int[]{212, 924}),
                new ImageNode("textures/items/Diamonds.png", new int[]{64, 64}, new int[]{710, 964})
        ));

        levelStructure.put("buttons", Arrays.asList(
                new Button(new int[]{240, 70}, new int[]{240, 605}, Color.decode("#8b523e"), true, "Trade", 25, Color.decode("#c2a760")),
                new Button(new int[]{240, 70}, new int[]{425, 420}, Color.decode("#8b523e"), true, "Enter Mine", 35, Color.decode("#c2a760")),
                new Button(new int[]{240, 70}, new int[]{1158, 10}, Color.decode("#8b523e"), true, "Exit Mine", 35, Color.decode("#c2a760")),
                new Button(new int[]{300, 70}, new int[]{1566, 805}, Color.decode("#8b523e"), true, "Exit Trading Plaza", 35, Color.decode("#c2a760"))
        ));

        levelStructure.put("player", Arrays.asList(
                new Player(new int[]{(int)(180 * 0.85), (int)(100 * 0.85)}, new int[]{554, 652})
        ));

        levelStructure.put("dialog", Arrays.asList(
                new Dialog(
                        new int[]{600, 250},
                        new int[]{650, 600},
                        Color.decode("#e0ab6e"),
                        Color.decode("#916b3f"),
                        15,
                        "TradingPlaza-Enter",
                        Color.decode("#301c05"),
                        25
                )
        ));

        levelStructure.put("transition", Arrays.asList(
                new TransitionNode(new int[]{1912, 1162}, new int[]{0, 0})
        ));

        // colliders 0 to 15 trading plaza //
        colliders.add(new RectangleCollider(new int[]{967, 358}, new int[]{0, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{650, 163}, new int[]{0, 358}, 0, false));
        colliders.add(new RectangleCollider(new int[]{190, 182}, new int[]{1091, 206}, 0, false));
        colliders.add(new RectangleCollider(new int[]{192, 204}, new int[]{1337, 37}, 0, false));
        colliders.add(new RectangleCollider(new int[]{231, 173}, new int[]{1656, 22}, 0, false));
        colliders.add(new RectangleCollider(new int[]{191, 195}, new int[]{1297, 598}, 0, false));
        colliders.add(new RectangleCollider(new int[]{206, 189}, new int[]{1488, 722}, 0, false));
        colliders.add(new RectangleCollider(new int[]{98, 320}, new int[]{1789, 822}, 0, false));
        colliders.add(new RectangleCollider(new int[]{371, 293}, new int[]{0, 521}, 0, false));
        colliders.add(new RectangleCollider(new int[]{315, 52}, new int[]{0, 814}, 0, false));
        colliders.add(new RectangleCollider(new int[]{229, 51}, new int[]{0, 866}, 0, false));
        colliders.add(new RectangleCollider(new int[]{130, 51}, new int[]{0, 917}, 0, false));
        colliders.add(new RectangleCollider(new int[]{45, 194}, new int[]{0, 968}, 0, false));
        colliders.add(new RectangleCollider(new int[]{1842, 20}, new int[]{45, 1142}, 0, false));
        colliders.add(new RectangleCollider(new int[]{945, 22}, new int[]{967, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{25, 1140}, new int[]{1887, 22}, 0, false));
        //                                //
        colliders.add(new RectangleCollider(new int[]{360, 363}, new int[]{130, 605}, 2, false)); // shop
        colliders.add(new RectangleCollider(new int[]{335, 230}, new int[]{371, 521}, 2, false)); // mine enter

        // colliders 18 to 38 mine        //
        colliders.add(new RectangleCollider(new int[]{1196, 125}, new int[]{0, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{553, 125}, new int[]{1359, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{163, 55}, new int[]{1196, 0}, 0, false));
        colliders.add(new RectangleCollider(new int[]{172, 593}, new int[]{1740, 125}, 0, false));
        colliders.add(new RectangleCollider(new int[]{85, 351}, new int[]{1827, 718}, 0, false));
        colliders.add(new RectangleCollider(new int[]{141, 944}, new int[]{0, 125}, 0, false));
        colliders.add(new RectangleCollider(new int[]{173, 298}, new int[]{312, 284}, 0, false));
        colliders.add(new RectangleCollider(new int[]{167, 333}, new int[]{517, 550}, 0, false));
        colliders.add(new RectangleCollider(new int[]{74, 169}, new int[]{304, 725}, 0, false));
        colliders.add(new RectangleCollider(new int[]{255, 274}, new int[]{630, 125}, 0, false));
        colliders.add(new RectangleCollider(new int[]{67, 270}, new int[]{826, 799}, 0, false));
        colliders.add(new RectangleCollider(new int[]{194, 139}, new int[]{836, 541}, 0, false));
        colliders.add(new RectangleCollider(new int[]{571, 130}, new int[]{1030, 269}, 0, false));
        colliders.add(new RectangleCollider(new int[]{95, 308}, new int[]{1258, 399}, 0, false));
        colliders.add(new RectangleCollider(new int[]{90, 199}, new int[]{1030, 399}, 0, false));
        colliders.add(new RectangleCollider(new int[]{90, 189}, new int[]{1030, 753}, 0, false));
        colliders.add(new RectangleCollider(new int[]{24, 155}, new int[]{1030, 598}, 0, false));
        colliders.add(new RectangleCollider(new int[]{500, 106}, new int[]{1120, 836}, 0, false));
        colliders.add(new RectangleCollider(new int[]{252, 177}, new int[]{1488, 541}, 0, false));
        colliders.add(new RectangleCollider(new int[]{132, 118}, new int[]{1488, 718}, 0, false));
        colliders.add(new RectangleCollider(new int[]{1912, 93}, new int[]{0, 1069}, 2, false));
        //                                //
        colliders.add(new RectangleCollider(new int[]{215, 215}, new int[]{1170, 55}, 2, false)); // mine exit

        colliders.add(new RectangleCollider(new int[]{342, 342}, new int[]{1545, 818}, 2, false)); // level exit
    }

    public Level_TradingPlaza(int[] size, int[] relativePosition, Color backgroundColor, int levelId) {
        super(size, relativePosition, backgroundColor, levelId);
    }

    public void loadLevel() {
        LevelController.loadLevel(this, levelStructure, colliders);

        // init minerals
        for (Node mineralNode: levelStructure.get("minerals")) {
            ImageNode mineral = (ImageNode)mineralNode;
            int mineralDurability = MINERAL_STATS.get(mineral.pathToImage);

            RectangleCollider mineralCollider = new RectangleCollider(new int[]{64, 64}, mineral.getPosition(), 2, false);
            addChild(mineralCollider);

            mineralsData.put(mineral, new Object[]{mineralCollider, mineralDurability, mineralDurability});

            LabelNode mineralDurabilityLabel = new LabelNode(
                    mineralDurability + "/" + mineralDurability,
                    new int[]{0, 0},
                    new int[]{64, 64},
                    new Color(255, 205, 0, 255),
                    18
            );
            mineralDurabilityLabel.setOutline(new Color(60, 34, 13, 255), 2.0f);
            mineral.addChild(mineralDurabilityLabel);
        }
        //

        transitionNode = (TransitionNode)levelStructure.get("transition").get(0);

        constructShop();
        shopWindow.setVisibility(false);

        inventoryWindow = new PlayerInventory();
        addChild(inventoryWindow);

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

        player = (Player)levelStructure.get("player").get(0);
        LevelController.initializePlayer(player);

        dialog.addDialogFinishedListener(lastChosenOption -> {
            startLevelClock();
        });

        shopCollider = colliders.get(16);
        shopOpenButton = (Button)levelStructure.get("buttons").get(0);
        shopOpenButton.setVisibility(false);

        shopOpenButton.setOnPressed(button -> {
            shopOpenButton.setVisibility(false);
            shopWindow.setVisibility(true);
            updateCoins();
        });

        enterMineCollider = colliders.get(17);
        enterMineButton = (Button)levelStructure.get("buttons").get(1);
        enterMineButton.setVisibility(false);

        enterMineButton.setOnPressed(button -> {
            System.out.println("Entering Mine...");
            setMine(true);
        });

        exitMineCollider = colliders.get(39);
        exitMineButton = (Button)levelStructure.get("buttons").get(2);

        exitMineButton.setOnPressed(button -> {
            System.out.println("Existing Mine...");
            setMine(false);
        });

        setMine(false);

        levelExitCollider = colliders.get(40);
        levelExitButton = (Button)levelStructure.get("buttons").get(3);
        levelExitButton.setVisibility(false);

        levelExitButton.setOnPressed(button -> {
            System.out.println("Existing Trading Plaza...");
            onLevelFinished.onFinished(levelID, 0);
        });
    }

    // shop system
    private void constructShop() {
        int[] shopWindowSize = new int[]{(int)(1912 * 0.75), (int)(1192 * 0.75)};
        int[] shopWindowPosition = new int[]{(1912 - shopWindowSize[0]) / 2, (1192 - shopWindowSize[1]) / 2};

        shopWindow = new Node(shopWindowSize, shopWindowPosition, new Color(153, 99, 47, 255), true);
        addChild(shopWindow);

        Node shopWindowInner = new Node(
                new int[]{shopWindowSize[0] - 16, shopWindowSize[1] - 16},
                new int[]{8, 8},
                new Color(66, 40, 25, 255),
                true
        );

        shopWindow.addChild(shopWindowInner);

        int[] shopTopBannerSize = new int[]{shopWindowSize[0] / 3, 100};
        int[] shopTopBannerPosition =  new int[]{(shopWindowSize[0] - shopWindowSize[0] / 3) / 2, -25};

        Node shopTopBanner = new Node(shopTopBannerSize, shopTopBannerPosition, new Color(153, 99, 47, 255), true);
        shopWindow.addChild(shopTopBanner);

        Node shopTopBannerInner = new Node(
                new int[]{shopTopBannerSize[0] - 16, shopTopBannerSize[1] - 16},
                new int[]{8, 8},
                new Color(66, 40, 25, 255),
                true
        );
        shopTopBanner.addChild(shopTopBannerInner);

        LabelNode bannerLabel = new LabelNode(
                "Waldos Shop",
                new int[]{0, 8},
                new int[]{shopTopBannerSize[0] - 16, shopTopBannerSize[1] - 16},
                new Color(225, 166, 107, 255),
                50
        );
        bannerLabel.setOutline(new Color(255, 234, 2, 255), 6.0f);
        shopTopBannerInner.addChild(bannerLabel);

        int[] coinsLabelBackgroundSize = new int[]{shopWindowSize[0] / 5, 100};
        int[] coinsLabelBackgroundPosition = new int[]{50, -25};

        Node coinsLabelBackground = new Node(coinsLabelBackgroundSize, coinsLabelBackgroundPosition, new Color(153, 99, 47, 255), true);
        shopWindow.addChild(coinsLabelBackground);

        coinsLabel = new LabelNode(
                "Coins: " + Globals.coins,
                new int[]{-10, 24},
                new int[]{coinsLabelBackgroundSize[0] - 50, coinsLabelBackgroundSize[1] - 16},
                new Color(223, 187, 9, 255),
                35
        );
        coinsLabelBackground.addChild(coinsLabel);

        ImageNode coinsIcon = new ImageNode("textures/items/Coins.png", new int[]{64, 64}, new int[]{224, 36});
        coinsLabelBackground.addChild(coinsIcon);

        // sell frame
        int[] mineralsSellFrameSize = new int[]{shopWindowSize[0] / 4, shopWindowSize[1] - 90};
        Node mineralsSellFrame = new Node(mineralsSellFrameSize, new int[]{shopWindowSize[0] - shopWindowSize[0] / 4, 90}, new Color(153, 99, 47, 255), true);
        shopWindow.addChild(mineralsSellFrame);

        Node mineralsSellFrameInner = new Node(mineralsSellFrameSize, new int[]{16, 8}, new Color(66, 40, 25, 255), true);
        mineralsSellFrame.addChild(mineralsSellFrameInner);

        // sell item containers
        Node silverContainer = createItemContainer("textures/items/Silver.png", "Silver", SILVER_VALUE, new int[]{24, 24});
        mineralsSellFrameInner.addChild(silverContainer);

        Node goldContainer = createItemContainer("textures/items/Gold.png", "Gold", GOLD_VALUE, new int[]{24, 40 + 128});
        mineralsSellFrameInner.addChild(goldContainer);

        Node crystalContainer = createItemContainer("textures/items/Crystal.png", "Crystal", CRYSTAL_VALUE, new int[]{24, 60 + 128 * 2});
        mineralsSellFrameInner.addChild(crystalContainer);

        Node diamondsContainer = createItemContainer("textures/items/Diamonds.png", "Diamonds", DIAMONDS_VALUE, new int[]{24, 80 + 128 * 3});
        mineralsSellFrameInner.addChild(diamondsContainer);
        //

        Node sellButtonBackground = new Node(new int[]{mineralsSellFrameSize[0] - 50, 85}, new int[]{18, mineralsSellFrameSize[1] - 105}, new Color(153, 99, 47, 255), true);
        mineralsSellFrameInner.addChild(sellButtonBackground);

        Button sellButton = new Button(
                new int[]{mineralsSellFrameSize[0] - 66, 69},
                new int[]{8, 8},
                new Color(18, 112, 35, 255),
                true,
                "Sell all minerals",
                35,
                new Color(39, 234, 42, 255)
        );
        sellButtonBackground.addChild(sellButton);
        sellButton.setOnPressed(button -> {
            sellButton.removeOnPressed();

            Globals.coins += (
                    SILVER_VALUE * Globals.silver +
                    GOLD_VALUE * Globals.gold +
                    CRYSTAL_VALUE * Globals.crystals +
                    DIAMONDS_VALUE * Globals.diamonds
            );

            Globals.silver = 0;
            Globals.gold = 0;
            Globals.crystals = 0;
            Globals.diamonds = 0;

            updateCoins();
            inventoryWindow.updateInventory();
        });

        // purchase items
        Node strengthPotionContainer = createItemPurchaseContainer("textures/items/PotionOfStrength.png", "Potion of Strength", STRENGTH_POTION_PRICE, new int[]{120, 125});
        shopWindow.addChild(strengthPotionContainer);
        Button strengthPotionPurchaseButton = (Button)strengthPotionContainer.getAllChildren().get(2).getAllChildren().get(0);
        strengthPotionPurchaseButton.setOnPressed(button -> {
            System.out.println("Purchasing Potion of Strength");
            if (Globals.coins >= STRENGTH_POTION_PRICE) {
                Globals.strengthPotion += 1;
                Globals.coins -= STRENGTH_POTION_PRICE;
                updateCoins();
                inventoryWindow.updateInventory();
            }
        });

        Node resistancePotionContainer = createItemPurchaseContainer("textures/items/PotionOfResistance.png", "Potion of Resistance", RESISTANCE_POTION_PRICE, new int[]{120 + 40 + 256, 125});
        shopWindow.addChild(resistancePotionContainer);
        Button resistancePotionPurchaseButton = (Button)resistancePotionContainer.getAllChildren().get(2).getAllChildren().get(0);
        resistancePotionPurchaseButton.setOnPressed(button -> {
            System.out.println("Purchasing Potion of Resistance");
            if (Globals.coins >= RESISTANCE_POTION_PRICE) {
                Globals.resistancePotion += 1;
                Globals.coins -= RESISTANCE_POTION_PRICE;
                updateCoins();
                inventoryWindow.updateInventory();
            }
        });

        Node speedPotionContainer = createItemPurchaseContainer("textures/items/PotionOfSpeed.png", "Potion of Speed", SPEED_POTION_PRICE, new int[]{120 + 80 + 256 * 2, 125});
        shopWindow.addChild(speedPotionContainer);
        Button speedPotionPurchaseButton = (Button)speedPotionContainer.getAllChildren().get(2).getAllChildren().get(0);
        speedPotionPurchaseButton.setOnPressed(button -> {
            System.out.println("Purchasing Potion of Speed");
            if (Globals.coins >= SPEED_POTION_PRICE) {
                Globals.speedPotion += 1;
                Globals.coins -= SPEED_POTION_PRICE;
                updateCoins();
                inventoryWindow.updateInventory();
            }
        });


        Node armorUpgradeContainer = createItemPurchaseContainer("textures/items/ArmorUpgrade.png", "Armor Upgrade", ARMOR_UPGRADE_PRICE, new int[]{120, 125 + 256 + 40});
        shopWindow.addChild(armorUpgradeContainer);
        Button armorUpgradePurchaseButton = (Button)armorUpgradeContainer.getAllChildren().get(2).getAllChildren().get(0);
        armorUpgradePurchaseButton.setOnPressed(button -> {
            System.out.println("Purchasing Armor Upgrade");
            if (Globals.coins >= ARMOR_UPGRADE_PRICE) {
                Globals.currentHealthMax += 50;
                Globals.coins -= ARMOR_UPGRADE_PRICE;
                updateCoins();
                inventoryWindow.updateInventory();
            }
        });

        Node swordUpgradeContainer = createItemPurchaseContainer("textures/items/SwordUpgrade.png", "Sword Upgrade", SWORD_UPGRADE_PRICE, new int[]{120 + 40 + 256, 125 + 265 + 40});
        shopWindow.addChild(swordUpgradeContainer);
        Button swordUpgradePurchaseButton = (Button)swordUpgradeContainer.getAllChildren().get(2).getAllChildren().get(0);
        swordUpgradePurchaseButton.setOnPressed(button -> {
            System.out.println("Purchasing Sword Upgrade");
            if (Globals.coins >= SWORD_UPGRADE_PRICE) {
                Globals.currentMinDamage += 10;
                Globals.currentMaxDamage += 10;
                Globals.coins -= SWORD_UPGRADE_PRICE;
                updateCoins();
                inventoryWindow.updateInventory();
            }
        });
        //

        int[] shopCloseButtonBackgroundSize = new int[]{shopWindowSize[0] / 7, 100};
        int[] shopCloseButtonBackgroundPosition =  new int[]{(shopWindowSize[0] - shopCloseButtonBackgroundSize[0]), -25};

        Node closeButtonBackground = new Node(shopCloseButtonBackgroundSize, shopCloseButtonBackgroundPosition, new Color(153, 99, 47, 255), true);
        shopWindow.addChild(closeButtonBackground);

        Button closeButton = new Button(
                new int[]{shopCloseButtonBackgroundSize[0] - 16, shopCloseButtonBackgroundSize[1] - 16},
                new int[]{8, 25},
                new Color(71, 23, 19, 255),
                true,
                "Close",
                45,
                new Color(170, 57, 52, 255)
        );
        closeButtonBackground.addChild(closeButton);
        closeButton.setOnPressed(button -> {
            System.out.println("Closing Waldos Shop");
            shopWindow.setVisibility(false);
        });

        shopWindow.moveNodeToTop();
    }

    private Node createItemContainer(String itemShowCasePath, String itemName, int sellValue, int[] containerPosition) {
        Node itemContainer = new Node(new int[]{295, 128}, containerPosition, new Color(153, 99, 47, 255), true);

        Node containerDisplay = new ImageNode(itemShowCasePath, new int[]{128, 128}, new int[]{0, 0});
        itemContainer.addChild(containerDisplay);

        Node labelsBackground = new Node(
                new int[]{180, 112},
                new int[]{124, 8},
                new Color(66, 40, 25, 255),
                true
        );
        itemContainer.addChild(labelsBackground);

        LabelNode itemNameLabel = new LabelNode(
                itemName,
                new int[]{128, 0},
                new int[]{167, 64},
                new Color(225, 166, 107, 255),
                35
        );
        itemContainer.addChild(itemNameLabel);

        LabelNode itemSellValueLabel = new LabelNode(
                "Value: " + sellValue,
                new int[]{108, 64},
                new int[]{167, 64},
                new Color(18, 112, 35, 255),
                25
        );
        itemContainer.addChild(itemSellValueLabel);

        ImageNode coinsIcon = new ImageNode("textures/items/Coins.png", new int[]{40, 40}, new int[]{167 + 128 - 40, 71});
        itemContainer.addChild(coinsIcon);

        return itemContainer;
    }

    private Node createItemPurchaseContainer(String itemShowCasePath, String itemName, int purchaseValue, int[] containerPosition) {
        Node itemContainer = new Node(new int[]{256, 256}, containerPosition, new Color(153, 99, 47, 255), true);

        ImageNode itemShowcase = new ImageNode(itemShowCasePath, new int[]{192, 192}, new int[]{32, 64});
        itemContainer.addChild(itemShowcase);

        LabelNode itemNameLabel = new LabelNode(
                itemName,
                new int[]{0, 0},
                new int[]{256, 64},
                new Color(66, 40, 25, 255),
                25
        );
        itemNameLabel.setOutline(new Color(225, 166, 107, 255), 4.0f);
        itemContainer.addChild(itemNameLabel);

        Node purchaseBackground = new Node(new int[]{256 - 16, 64}, new int[]{8, 192 - 8}, new Color(66, 40, 25, 255), true);
        itemContainer.addChild(purchaseBackground);

        Button purchaseButton = new Button(
                new int[]{256 -16 - 64, 64 - 16},
                new int[]{8, 8},
                new Color(18, 112, 35, 255),
                true,
                "Buy | " + purchaseValue,
                25,
                new Color(39, 234, 42, 255)
        );
        purchaseBackground.addChild(purchaseButton);

        ImageNode coinsImage = new ImageNode("textures/items/Coins.png", new int[]{64, 64}, new int[]{176, 0});
        purchaseBackground.addChild(coinsImage);

        return itemContainer;
    }

    private void updateCoins() {
        coinsLabel.setText("Coins: " + Globals.coins);
    }
    //

    private void setMine(boolean state) {
        transitionNode.moveNodeToTop();

        inMine = state;

        // Backgrounds
        levelStructure.get("background").get(0).setVisibility(state);   // cave background
        levelStructure.get("background").get(1).setVisibility(!state);  // plaza background

        // minerals
        for (Node mineralNode: levelStructure.get("minerals")) {
            if (mineralNode instanceof ImageNode && mineralNode.getPanel() != null) {
                mineralNode.setVisibility(state);
            }
        }

        // Buttons
        enterMineButton.setVisibility(!state);
        exitMineButton.setVisibility(state);

        // Colliders
        CollisionsHandler.clearWorldPolygons();
        for (int i = 0; i <= 15; i++) colliders.get(i).setEnabled(!state);   // plaza walls
        for (int i = 18; i <= 37; i++) colliders.get(i).setEnabled(state);   // mine walls

        // Shop & mine entry/exit zones
        colliders.get(16).setEnabled(!state); // shop
        colliders.get(17).setEnabled(!state); // mine entry
        colliders.get(38).setEnabled(state);  // mine exit

        // Move player after short transition
        Timer delayTimer = new Timer(50, finished -> {
            if (state) {
                for (int i = 0; i <= 25; i++) { player.lastPositions.add(new int[]{1245, 165}); }
                player.setPosition(new int[]{1245, 165});
            } else {
                for (int i = 0; i <= 25; i++) { player.lastPositions.add(new int[]{554, 652}); }
                player.setPosition(new int[]{554, 652});
            }
        });

        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void _process() {
        boolean collidingWithShopArea = CollisionsHandler.isPlayerCollidingWithActionArea(shopCollider.getPolygon());
        boolean collidingWithMineArea = CollisionsHandler.isPlayerCollidingWithActionArea(enterMineCollider.getPolygon());
        boolean collidingWithExitMineArea = CollisionsHandler.isPlayerCollidingWithActionArea(exitMineCollider.getPolygon());
        boolean collidingWithLevelExitArea = CollisionsHandler.isPlayerCollidingWithActionArea(levelExitCollider.getPolygon());

        if (inMine) {
            shopOpenButton.setVisibility(false);
            enterMineButton.setVisibility(false);
            exitMineButton.setVisibility(collidingWithExitMineArea);
            levelExitButton.setVisibility(false);
            testForMineralCollecting();
        } else {
            shopOpenButton.setVisibility(collidingWithShopArea);
            enterMineButton.setVisibility(collidingWithMineArea);
            exitMineButton.setVisibility(false);
            levelExitButton.setVisibility(collidingWithLevelExitArea);
        }
    }

    private void testForMineralCollecting() {
        boolean inAttack = player.currentAnimation.equals("attack");
        if (!inAttack || mineralMiningDebounce) return;

        int[] playerCornerPosition = player.getGlobalPosition();
        int[] playerCenterPosition = new int[]{playerCornerPosition[0] + player.getSize()[0] / 2, playerCornerPosition[1] + player.getSize()[1] / 2};

        for (ImageNode mineral: mineralsData.keySet()) {
            Object[] mineralData = mineralsData.get(mineral);
            RectangleCollider mineralCollider = (RectangleCollider)mineralData[0];
            int maxDurability = (int) mineralData[1]; // max durability
            int currentDurability = (int) mineralData[2]; // current durability

            int[] mineralPosition = mineral.getGlobalPosition();
            double distanceToMineral = ExtendedMath.getDistanceBetweenPositions(mineralPosition, playerCenterPosition);
            boolean colliding = CollisionsHandler.isPlayerCollidingWithActionArea(mineralCollider.getPolygon());

            if (colliding || distanceToMineral < 115.0) {
                mineralMiningDebounce = true;

                currentDurability = ExtendedMath.clampInt(currentDurability - 25, 0, maxDurability);
                mineralData[2] = currentDurability;

                if (currentDurability == 0) {
                    mineralCollider.destroy();
                    mineralsData.remove(mineral);
                    mineral.destroy();
                    switch (mineral.pathToImage) {
                        case "textures/items/Silver.png":
                            Globals.silver += 1;
                            break;
                        case "textures/items/Gold.png":
                            Globals.gold += 1;
                            break;
                        case "textures/items/Crystal.png":
                            Globals.crystals += 1;
                            break;
                        case "textures/items/Diamonds.png":
                            Globals.diamonds += 1;
                            break;
                        default:
                            System.out.println("Invalid mineral..");
                            break;
                    }
                    inventoryWindow.updateInventory();
                } else {
                    LabelNode mineralLabel = (LabelNode)mineral.getAllChildren().get(0);
                    mineralLabel.setText(currentDurability + "/" + maxDurability);
                }

                Timer debounceTimer = new Timer(500, finished -> {
                    mineralMiningDebounce = false;
                });

                debounceTimer.setRepeats(false);
                debounceTimer.start();

                break;
            }
        }
    }
}
