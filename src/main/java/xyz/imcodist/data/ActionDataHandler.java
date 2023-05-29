package xyz.imcodist.data;

import java.util.ArrayList;
import java.util.List;

public class ActionDataHandler {
    public static List<ActionData> actions = new ArrayList<>();

    public static void initialize() {

    }

    public static void add(ActionData action) {
        actions.add(action);
        save();
    }
    public static void remove(ActionData action) {
        actions.remove(action);
    }

    private static void save() {
        for (ActionData action : actions) {

        }
    }
}
