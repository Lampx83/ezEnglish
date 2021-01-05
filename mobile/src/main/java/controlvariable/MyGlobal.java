package controlvariable;

public class MyGlobal {
    public final static String APP_VER = "Ver6";
    public final static int db_ver = 7;
    public final static int fivedp = 16;
    public static String PHIENBAN;
    public static String image_folder;
    public static Boolean screen_small = true;
    public static String record_folder;
    //Boolean
    public static Boolean showTermFirst = false;


    // Float
    public static Float ScreenScale; // Nexus 7 la 1.334, HTC One la 1, HTC ONE V la 1
    public static Float scaledDensity; // Nexus 7 la 1.331, HTC One la 3 , HTC ONE V la 1.72
    public static Float font_size = 1.0f;

    //String
    public static String user_id;
    public static String user_name;


    public static String lang1 = "zh_TW";
    //public static String lang1 = "vi";
    public static String lang2 = "en";

    public static final int TIME_TO_UPDATE = 300;
    public static final int TIME_TO_UPLOAD = 600;


    //DEFAULT VALUE
    public static boolean ENABLEPEDO = false;
    public static final boolean AUTOBOOKMARK = false;
    public static String others = "Others";
}
