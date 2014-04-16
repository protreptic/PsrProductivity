package ru.magnat.sfs.android;

public class Log {
	 	public static final boolean LOG = true;

	    public static void i(String tag, String string) {
	        if (LOG) android.util.Log.i(tag, string);
	    }
	    public static void e(String tag, String string) {
	        if (LOG) android.util.Log.e(tag, string);
	    }
	    public static void d(String tag, String string) {
	        if (LOG) android.util.Log.d(tag, string);
	    }
	    public static void v(String tag, String string) {
	        if (LOG) android.util.Log.v(tag, string);
	    }
	    public static void w(String tag, String string) {
	        if (LOG) android.util.Log.w(tag, string);
	    }
	    public static int e(String tag, String string, Throwable tr) {
	    	if (LOG) return android.util.Log.e(tag, string,tr);
	    	return 0;
	    }
	    public static int d(String tag, String string, Throwable tr){
	    	if (LOG) return android.util.Log.d(tag, string,tr);
	    	return 0;
	    }
	    

}
