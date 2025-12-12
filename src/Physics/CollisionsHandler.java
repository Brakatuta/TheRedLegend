package Physics;

import java.awt.Point;
import java.util.*;


public class CollisionsHandler {
    // Use List<Polygon> for easier adding/removing
    public static Map<String, List<Polygon>> Polygons = new HashMap<>();

    public static int[] lastPlayerWorldCollisionPoint;

    static {
        Polygons.put("world", new ArrayList<>());
        Polygons.put("player", new ArrayList<>());
        Polygons.put("action", new ArrayList<>());
        Polygons.put("enemy", new ArrayList<>());
        Polygons.put("enemy_damage_player_area", new ArrayList<>());
    }

    // Add a single polygon to a group
    public static void addPolygonToGroup(int group, Polygon polygon) {
        switch (group) {
            case 0:
                Polygons.get("world").add(polygon);
                //System.out.println("Added to world: " + polygon);
                break;
            case 1:
                if (!Polygons.get("player").isEmpty()) {
                    Polygons.get("player").set(0, polygon); // only one polygon because player is a single object
                } else {
                    Polygons.get("player").add(polygon);
                }
                //System.out.println("Added to player: " + polygon.getPoints().toString());
                break;
            case 2:
                Polygons.get("action").add(polygon);
                break;
            case 3:
                Polygons.get("enemy").add(polygon);
                break;
            case 4:
                Polygons.get("enemy_damage_player_area").add(polygon);
                break;
            default:
                System.out.println("Unknown Collision Group: " + group);
        }
    }

    public static void removePolygonFromGroup(int group, Polygon polygon) {
        switch (group) {
            case 0:
                Polygons.get("world").remove(polygon);
                break;
            case 1:
                Polygons.get("player").remove(polygon);
                break;
            case 2:
                Polygons.get("action").remove(polygon);
                break;
            case 3:
                Polygons.get("enemy").remove(polygon);
                break;
            case 4:
                Polygons.get("enemy_damage_player_area").remove(polygon);
                break;
            default:
                System.out.println("Unknown Collision Group: " + group);
        }
    }

    public static void clearWorldPolygons() {
        Polygons.get("world").clear();
    }

    public static boolean isPlayerCollidingWithWorld() {
        boolean playerCollision = false;

        try {
            // Defensive copies to avoid ConcurrentModificationException
            List<Polygon> playerPolygons = new ArrayList<>(Polygons.getOrDefault("player", List.of()));
            List<Polygon> worldPolygons = new ArrayList<>(Polygons.getOrDefault("world", List.of()));

//            System.out.println("-----------------------------------");
//            for (Polygon polygon: worldPolygons) {
//                System.out.println(polygon.getPoints());
//            }

            for (Polygon playerPolygon : playerPolygons) {
                if (playerCollision) break;

                for (Polygon worldPolygon : worldPolygons) {
                    if (playerPolygon.overlapsWith(worldPolygon)) {
                        Point collisionPoint = playerPolygon.getOverlapPoint(worldPolygon);
                        if (collisionPoint != null) {
                            lastPlayerWorldCollisionPoint = new int[]{collisionPoint.x, collisionPoint.y};
                        }
                        playerCollision = true;
                        break;
                    }
                }
            }
        }
        catch (ConcurrentModificationException e) {
            System.err.println("[WARN] Polygons modified during collision check — retrying next frame.");
            // Optionally log stack trace for debugging:
            // e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("[ERROR] Collision check failed: " + e.getMessage());
            e.printStackTrace();
        }

        return playerCollision;
    }

    public static Boolean isPlayerCollidingWithActionArea(Polygon actionArea) {
        boolean actionCollision = false;

        for (Polygon playerPolygon: Polygons.get("player")) {
            if (playerPolygon.overlapsWith(actionArea)) {
                actionCollision = true;
                break;
            }
        }

        return actionCollision;
    }

    public static int[] getPlayerWorldCollisionPoint() {
        return lastPlayerWorldCollisionPoint;
    }
}