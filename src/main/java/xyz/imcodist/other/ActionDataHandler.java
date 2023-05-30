package xyz.imcodist.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import xyz.imcodist.data.ActionData;
import xyz.imcodist.data.ActionDataJSON;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActionDataHandler {
    public static List<ActionData> actions = new ArrayList<>();

    public static void initialize() {
        load();
    }

    public static void add(ActionData action) {
        actions.add(action);
        save();
    }
    public static void remove(ActionData action) {
        actions.remove(action);
        save();
    }

    private static void load() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickmenu_data.json");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ActionDataJSON>>(){}.getType();

        try (FileReader fileReader = new FileReader(file)) {
            List<ActionDataJSON> actionDataJSONS = gson.fromJson(fileReader, listType);

            for (ActionDataJSON action : actionDataJSONS) {
                actions.add(ActionData.fromJSON(action));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void save() {
        List<ActionDataJSON> actionDataJSONS = new ArrayList<>();

        for (ActionData action : actions) {
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
