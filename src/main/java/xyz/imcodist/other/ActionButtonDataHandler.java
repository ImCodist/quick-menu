package xyz.imcodist.other;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import xyz.imcodist.data.ActionButtonData;
import xyz.imcodist.data.ActionButtonDataJSON;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActionButtonDataHandler {
    public static List<ActionButtonData> actions = new ArrayList<>();

    public static void initialize() {
        load();
    }

    public static void add(ActionButtonData action) {
        actions.add(action);
        save();
    }
    public static void remove(ActionButtonData action) {
        actions.remove(action);
        save();
    }

    public static void load() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickmenu_data.json");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ActionButtonDataJSON>>(){}.getType();

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                List<ActionButtonDataJSON> actionDataJSONS = gson.fromJson(fileReader, listType);

                for (ActionButtonDataJSON action : actionDataJSONS) {
                    actions.add(ActionButtonData.fromJSON(action));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void save() {
        List<ActionButtonDataJSON> actionDataJSONS = new ArrayList<>();

        for (ActionButtonData action : actions) {
            actionDataJSONS.add(action.toJSON());
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(actionDataJSONS);

        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickmenu_data.json");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
