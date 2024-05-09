package xyz.imcodist.quickmenu.data.command_actions;

public class BaseActionData {
    public String getJsonType() {
        return "base";
    }
    public String getJsonValue() {
        return "";
    }

    public String getTypeString() { return "ACT"; }
    public String getString() {
        return "uh oh why are you seeing this";
    }

    public void run() {}
}
