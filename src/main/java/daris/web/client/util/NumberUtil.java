package daris.web.client.util;

public class NumberUtil {
	
	public static native String toFixed(double d, int fixed)/*-{
		return d.toFixed(fixed);
	}-*/;
	
	public static native String toPrecision(double d, int precision)/*-{
		return d.toPrecision(precision);
	}-*/;

}
