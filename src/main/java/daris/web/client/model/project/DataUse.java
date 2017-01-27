package daris.web.client.model.project;

import java.util.ArrayList;
import java.util.List;

import arc.gui.gwt.widget.combo.ComboBoxEntry;

public enum DataUse {

    UNSPECIFIED, SPECIFIC, EXTENDED;

    public String toString() {
        return name().toLowerCase();
    }

    public static DataUse fromString(String str) {
        if (str != null) {
            DataUse[] vs = values();
            for (DataUse v : vs) {
                if (v.name().equalsIgnoreCase(str)) {
                    return v;
                }
            }
        }
        return null;
    }

    public static List<ComboBoxEntry<DataUse>> comboBoxEntries() {
        DataUse[] vs = values();
        List<ComboBoxEntry<DataUse>> entries = new ArrayList<ComboBoxEntry<DataUse>>(vs.length);
        for (DataUse v : vs) {
            entries.add(new ComboBoxEntry<DataUse>(v));
        }
        return entries;
    }

}
