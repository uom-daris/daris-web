package daris.web.client.model.project;

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

}
