package xyz.imcodist.quickmenu.data;

import net.minecraft.component.type.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;

public class ActionButtonDataJSON {
    public String name;
    public ArrayList<ArrayList<String>> actions;
    public String icon;
    public ModelData modelData;
    public ArrayList<Integer> keybind = new ArrayList<>();

    public static class ModelData {
        public ModelData(CustomModelDataComponent originalComponent) {
            this.floats = originalComponent.floats();
            this.flags = originalComponent.flags();
            this.strings = originalComponent.strings();
            this.colors = originalComponent.colors();
        }

        public ModelData() {}

        public CustomModelDataComponent toComponent() {
            return new CustomModelDataComponent(this.floats, this.flags, this.strings, this.colors);
        }

        public List<Float> floats;
        public List<Boolean> flags;
        public List<String> strings;
        public List<Integer> colors;
    }
}
