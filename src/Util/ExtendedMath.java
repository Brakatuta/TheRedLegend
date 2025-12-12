package Util;

import java.util.Random;


public class ExtendedMath {
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clampInt(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double getDistanceBetweenPositions(int[] pos0, int[] pos1) {
        return Math.sqrt(Math.pow((pos1[0] - pos0[0]), 2) + Math.pow((pos1[1] - pos0[1]), 2));
    }

    public static int randIRange(int num0, int num1) {
        Random rand = new Random();
        return rand.nextInt(num0, num1);
    }
}
