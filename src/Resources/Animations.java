package Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Animations {
    public static Map<String, String[]> PositionMarkerFrames = new HashMap<>();

    public static Map<String, String[]> PlayerFrames = new HashMap<>();
    public static Map<String, int[]> PlayerAnimationScales = new HashMap<>();
    public static Map<String, int[]> PlayerAnimationOffsets = new HashMap<>();

    public static Map<String, String[]> BarbarianFrames = new HashMap<>();

    public static Map<String, String[]> FireDragonFrames = new HashMap<>();

    public static Map<String, String[]> FireFlamesFrames = new HashMap<>();

    public static Map<String, String[]> SpotLightFrames = new HashMap<>();

    public static Map<String, String[]> AmbientFrames = new HashMap<>();

    public static Map<String, String[]> ExplosionFrames = new HashMap<>();

    static {
        PositionMarkerFrames.put("animated_marker", new String[]{
                "textures/maps/positionmarker/frame3.png",
                "textures/maps/positionmarker/frame2.png",
                "textures/maps/positionmarker/frame1.png",
                "textures/maps/positionmarker/frame0.png",
                "textures/maps/positionmarker/frame1.png",
                "textures/maps/positionmarker/frame2.png",
                "textures/maps/positionmarker/frame3.png",
        });

        PlayerFrames.put("idle", new String[]{
                "textures/maincharacter/idle/idle000.png",
                "textures/maincharacter/idle/idle001.png",
                "textures/maincharacter/idle/idle002.png",
                "textures/maincharacter/idle/idle003.png",
                "textures/maincharacter/idle/idle004.png",
                "textures/maincharacter/idle/idle005.png"
        });

        PlayerAnimationScales.put("idle", new int[]{77, 100});
        PlayerAnimationOffsets.put("idle", new int[]{50, 0});

        PlayerFrames.put("walk", new String[]{
                "textures/maincharacter/walk/walk000.png",
                "textures/maincharacter/walk/walk001.png",
                "textures/maincharacter/walk/walk002.png",
                "textures/maincharacter/walk/walk003.png",
                "textures/maincharacter/walk/walk004.png",
                "textures/maincharacter/walk/walk005.png"
        });

        PlayerAnimationScales.put("walk", new int[]{72, 100});
        PlayerAnimationOffsets.put("walk", new int[]{50, 0});

        PlayerFrames.put("attack", new String[]{
                "textures/maincharacter/attack/attack000.png",
                "textures/maincharacter/attack/attack001.png",
                "textures/maincharacter/attack/attack002.png",
                "textures/maincharacter/attack/attack003.png",
                "textures/maincharacter/attack/attack004.png",
                "textures/maincharacter/attack/attack005.png"
        });

        PlayerAnimationScales.put("attack", new int[]{180, 100});
        PlayerAnimationOffsets.put("attack", new int[]{0, 0});

        BarbarianFrames.put("idle", new String[]{
                "textures/barbarian/idle/idle00.png",
                "textures/barbarian/idle/idle01.png",
                "textures/barbarian/idle/idle02.png",
                "textures/barbarian/idle/idle03.png",
                "textures/barbarian/idle/idle04.png",
                "textures/barbarian/idle/idle05.png",
                "textures/barbarian/idle/idle06.png",
                "textures/barbarian/idle/idle07.png",
        });

        BarbarianFrames.put("run", new String[]{
                "textures/barbarian/run/run00.png",
                "textures/barbarian/run/run01.png",
                "textures/barbarian/run/run02.png",
                "textures/barbarian/run/run03.png",
                "textures/barbarian/run/run04.png",
                "textures/barbarian/run/run05.png",
                "textures/barbarian/run/run06.png",
                "textures/barbarian/run/run07.png",
        });

        BarbarianFrames.put("attack", new String[]{
                "textures/barbarian/attack/attack00.png",
                "textures/barbarian/attack/attack01.png",
                "textures/barbarian/attack/attack02.png",
        });

        BarbarianFrames.put("dead", new String[]{
                "textures/barbarian/dead/dead00.png",
                "textures/barbarian/dead/dead01.png",
                "textures/barbarian/dead/dead02.png",
        });

        FireDragonFrames.put("fly", new String[]{
                "textures/firedragon/fly/fly000.png",
                "textures/firedragon/fly/fly001.png",
                "textures/firedragon/fly/fly002.png"
        });

        FireDragonFrames.put("idle", new String[]{
                "textures/firedragon/idle/tile000.png",
                "textures/firedragon/idle/tile001.png",
                "textures/firedragon/idle/tile002.png",
                "textures/firedragon/idle/tile003.png",
                "textures/firedragon/idle/tile004.png",
        });

        FireDragonFrames.put("attack1", new String[]{
                "textures/firedragon/attack1/attack000.png",
                "textures/firedragon/attack1/attack001.png",
                "textures/firedragon/attack1/attack002.png",
                "textures/firedragon/attack1/attack001.png",
                "textures/firedragon/attack1/attack002.png",
                "textures/firedragon/attack1/attack003.png"
        });

        FireDragonFrames.put("attack2", new String[]{
                "textures/firedragon/attack2/attack000.png",
                "textures/firedragon/attack2/attack001.png",
                "textures/firedragon/attack2/attack002.png",
                "textures/firedragon/attack2/attack003.png",
                "textures/firedragon/attack2/attack004.png",
                "textures/firedragon/attack2/attack005.png",
                "textures/firedragon/attack2/attack006.png",
                "textures/firedragon/attack2/attack007.png"
        });

        FireDragonFrames.put("damaged", new String[]{
                "textures/firedragon/damaged/damaged000.png",
                "textures/firedragon/damaged/damaged001.png",
                "textures/firedragon/damaged/damaged002.png",
                "textures/firedragon/damaged/damaged003.png",
                "textures/firedragon/damaged/damaged004.png",
                "textures/firedragon/damaged/damaged004.png"
        });

        FireDragonFrames.put("dead", new String[]{
                "textures/firedragon/dead/dead000.png",
                "textures/firedragon/dead/dead001.png",
                "textures/firedragon/dead/dead002.png",
                "textures/firedragon/dead/dead003.png",
                "textures/firedragon/dead/dead004.png"
        });

        FireDragonFrames.put("dragonsbreath", new String[]{});

        FireDragonFrames.put("firestrike", new String[]{
                "textures/firedragon/firestrike/frame_00_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_01_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_02_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_03_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_04_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_05_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_06_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_07_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_08_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_09_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_10_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_11_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_10_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_09_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_08_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_07_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_06_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_05_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_04_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_03_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_02_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_01_delay-0.06s.png",
                "textures/firedragon/firestrike/frame_00_delay-0.06s.png",
        });

        FireDragonFrames.put("firestrikeimpact", new String[]{
                "textures/firedragon/firestrikeimpact/frame_00_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_01_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_02_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_03_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_04_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_05_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_06_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_07_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_08_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_09_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_10_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_11_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_12_delay-0.1s.png",
                "textures/firedragon/firestrikeimpact/frame_13_delay-0.1s.png"
        });

        FireDragonFrames.put("eggcracking", new String[]{
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg000.png",
                "textures/teaser/eggcrack/Egg001.png",
                "textures/teaser/eggcrack/Egg000.png",
        });

        FireFlamesFrames.put("flames1", new String[]{
                "textures/fire/flames1/frame00.png",
                "textures/fire/flames1/frame01.png",
                "textures/fire/flames1/frame02.png",
                "textures/fire/flames1/frame03.png",
                "textures/fire/flames1/frame04.png",
                "textures/fire/flames1/frame05.png",
                "textures/fire/flames1/frame06.png",
                "textures/fire/flames1/frame07.png",

        });

        FireFlamesFrames.put("flames2", new String[]{
                "textures/fire/flames2/frame00.png",
                "textures/fire/flames2/frame01.png",
                "textures/fire/flames2/frame02.png",
                "textures/fire/flames2/frame03.png",
                "textures/fire/flames2/frame04.png",
                "textures/fire/flames2/frame05.png",
                "textures/fire/flames2/frame06.png",
                "textures/fire/flames2/frame07.png",

        });

        SpotLightFrames.put("dim_flicker", new String[]{
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight07.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight07.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight08.png",
                "textures/light/spotlight/SpotLight07.png",
        });

        AmbientFrames.put("fireflies", new String[]{});

        ExplosionFrames.put("dynamite_explosion", new String[]{
                "textures/explosions/dynamiteexplosion/tile000.png",
                "textures/explosions/dynamiteexplosion/tile001.png",
                "textures/explosions/dynamiteexplosion/tile002.png",
                "textures/explosions/dynamiteexplosion/tile003.png",
                "textures/explosions/dynamiteexplosion/tile004.png",
                "textures/explosions/dynamiteexplosion/tile005.png",
                "textures/explosions/dynamiteexplosion/tile005.png"
        });
    }

    public static void initialize() {
        // fireflies frames
        ArrayList<String> firefliesFrames = new ArrayList<>(){};
        for (int i = 0; i < 20; i++) {
            String numString = Integer.toString(i);
            if (numString.length() == 1) {
                numString = "0" + numString;
            }
            String framePath = "textures/fireflies/frame_" + numString + "_delay-0.1s_converted.png";
            firefliesFrames.add(framePath);
        }
        String[] firefliesArray = firefliesFrames.toArray(new String[20]);
        AmbientFrames.put("fireflies", firefliesArray);

        // dragonsbreath frames
        ArrayList<String> dragonsbreathFrames = new ArrayList<>(){};
        for (int i = 0; i < 30; i++) {
            String numString = Integer.toString(i);
            if (numString.length() == 1) {
                numString = "0" + numString;
            }
            String framePath = "textures/firedragon/dragonsbreath/frame_" + numString + "_delay-0.02s.png";
            dragonsbreathFrames.add(framePath);
        }
        String[] dragonsbreathArray = dragonsbreathFrames.toArray(new String[20]);
        FireDragonFrames.put("dragonsbreath", dragonsbreathArray);
    }
}