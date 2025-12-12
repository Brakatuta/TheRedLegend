package Util;


public class Globals {
    public static boolean allowChoosingSettings = true;
    public static boolean useGermanLanguage = false;
    public static String HERO_NAME = "Lukas"; // add small window to game start to let user set a name

    // items
    public static int gold = 0;
    public static int silver = 0;
    public static int crystals = 0;
    public static int diamonds = 0;
    public static int strengthPotion = 0;
    public static int resistancePotion = 0;
    public static int speedPotion = 0;
    //

    public static boolean strengthEffectActive = false;
    public static boolean resistanceEffectActive = false;
    public static boolean speedEffectActive = false;

    // player stats
    public static int coins = 0;
    public static int currentHealthMax = 100;
    public static int currentMinDamage = 25;
    public static int currentMaxDamage = 50;
    public static double currentSpeedMultiplier = 1.0;
    //

    public static void resetPlayerStats() {
        if (strengthEffectActive) {
            currentMinDamage -= 15;
            currentMaxDamage -= 25;
        }

        if (resistanceEffectActive) {
            currentHealthMax -= 100;
        }

        if (speedEffectActive) {
            currentSpeedMultiplier = 1.0;
        }
    }
}
