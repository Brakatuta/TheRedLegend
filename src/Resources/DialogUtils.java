package Resources;

import Util.Globals;
import Util.JsonReader;
import java.io.File;
import java.util.*;


public class DialogUtils {
    private static File jsonFile = new File("src/Resources/Dialogs.json");

    private static JsonReader reader;

    public static void initialize() {
        if (Globals.useGermanLanguage) {
            jsonFile = new File("src/Resources/DialogsGerman.json");
        }
        try {
            reader = new JsonReader(jsonFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDialogSize(String dialogType) {
        try {
            return reader.getObjectSize(dialogType);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static DialogLine getDialogLineByIndex(String dialogType, int index) {
        try {
            Map<String, Object> dialogNode = reader.getObject(dialogType);
            if (dialogNode == null) throw new IllegalArgumentException("Dialog not found: " + dialogType);

            int i = 0;
            for (Map.Entry<String, Object> entry : dialogNode.entrySet()) {
                if (i == index) {
                    return new DialogLine(entry.getKey(), entry.getValue());
                }
                i++;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}