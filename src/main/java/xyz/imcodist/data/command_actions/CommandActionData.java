package xyz.imcodist.data.command_actions;

public class CommandActionData extends BaseActionData {
    public String command = "";

    @Override
    public String getJsonType() {
        return "cmd";
    }

    @Override
    public String getJsonValue() {
        return command;
    }

    @Override
    public String getString() {
        return command;
    }
}
