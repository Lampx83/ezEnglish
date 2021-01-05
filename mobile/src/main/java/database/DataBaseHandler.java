package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DataBaseHandler extends SQLiteOpenHelper {

    // GENERAL
    public static final String MARK = "Mark"; // Mark=1 => Bookmark , Mark=2 => Master of it, Mark=3 => It isnot useful
    public static final String BOOKMARK_TIME = "BookmarkTime";
    public static final String BOX = "Box";

    // Translate
    public final static String TABLE_TRANSLATE = "translate";
    public final static String TRANSLATEID = "translateid";
    public final static String TRANSLATE_LANGFROM = "langfrom";
    public final static String TRANSLATE_LANGTO = "langto";
    public final static String TRANSLATE_TEXT = "text";
    public final static String TRANSLATE_RESULT = "result";
    public final static String TRANSLATE_DEVICE = "device";
    public final static String TRANSLATE_SERVER = "server";
    public final static String TRANSLATE_TIME = "time";
    public final static String TRANSLATE_DATE = "date";
    public final static String TRANSLATE_LATITUDE = "latitude";
    public final static String TRANSLATE_LONGITUDE = "longitude";
    public final static String TRANSLATE_IMAGE = "image";
    public final static String TRANSLATE_SCORE = "score";
    public final static String TRANSLATE_TTS = "tts";
    public final static String TRANSLATE_STEP = "step";
    public final static String TRANSLATE_STEPON = "stepon";
    public final static String TRANSLATE_CONTEXT = "context";
    public final static String TRANSLATE_USERID = "user_id";
    public final static String TRANSLATE_USERNAME = "user_name";
    public final static String TRANSLATE_NOTE = "note";
    public final static String TRANSLATE_PRACTICE_TIMES = "practice_times";

    public final static String TRANSLATE_ADD = "translate_add";
    public final static String TRANSLATE_DELETE = "translate_delete";
    public final static String TRANSLATE_EDIT = "translate_edit";


    // Pedometer
    public final static String TABLE_PEDOMETER = "pedometer";
    public final static String PEDOMETER_DATE = "date";
    public final static String PEDOMETER_STEP = "step";
    public final static String PEDOMETER_STEPON = "stepon";

    // CARD
    public static final String TABLE_CARD = "Cards";
    public static final String CARD_ID = "CardID";
    public static final String CARD_TERM = "CardTerm";
    public static final String CARD_DEFINITION = "CardDefinition";
    public static final String CARD_IMAGE = "CardImage";
    public static final String CARD_IMAGE_WIDTH = "CardWidth";
    public static final String CARD_IMAGE_HEIGHT = "CardHeight";

    //LOG
    public static final String TABLE_USERLOG = "Logs";
    public static final String USERLOG_TIME = "LogTime";
    public static final String USERLOG_CODE = "LogCode";
    public static final String USERLOG_ID = "LogID";


    public DataBaseHandler(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE;

        CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_CARD + "(" + CARD_ID + " TEXT PRIMARY KEY,";
        CREATE_TABLE += "" + CARD_TERM + " TEXT UNIQUE," + CARD_DEFINITION + " TEXT," + CARD_IMAGE + " TEXT," + MARK + " INTEGER," + CARD_IMAGE_WIDTH + " INTEGER," + CARD_IMAGE_HEIGHT + " INTEGER," + BOOKMARK_TIME + " INTEGER,";
        CREATE_TABLE += TRANSLATE_LANGFROM + " TEXT, ";
        CREATE_TABLE += TRANSLATE_LANGTO + " TEXT, ";
        CREATE_TABLE += TRANSLATEID + " INTEGER, ";
        CREATE_TABLE += BOX + " INTEGER";
        CREATE_TABLE += ")";
        db.execSQL(CREATE_TABLE);


        // TRANSLATED
        CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_TRANSLATE;
        CREATE_TABLE += " ( ";
        CREATE_TABLE += TRANSLATEID + " INTEGER PRIMARY KEY, ";
        CREATE_TABLE += TRANSLATE_LANGFROM + " TEXT, ";
        CREATE_TABLE += TRANSLATE_LANGTO + " TEXT, ";
        CREATE_TABLE += TRANSLATE_TEXT + " TEXT, ";
        CREATE_TABLE += TRANSLATE_RESULT + " TEXT, ";
        CREATE_TABLE += TRANSLATE_DEVICE + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_SERVER + " TEXT, ";
        CREATE_TABLE += TRANSLATE_IMAGE + " TEXT, ";
        CREATE_TABLE += TRANSLATE_TIME + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_DATE + " TEXT, ";
//        CREATE_TABLE += MARK + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_LATITUDE + " TEXT, ";
        CREATE_TABLE += TRANSLATE_LONGITUDE + " TEXT, ";
        CREATE_TABLE += TRANSLATE_TTS + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_SCORE + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_STEP + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_STEPON + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_CONTEXT + " TEXT, ";
        CREATE_TABLE += TRANSLATE_USERID + " TEXT, ";
        CREATE_TABLE += TRANSLATE_USERNAME + " TEXT, ";
        CREATE_TABLE += TRANSLATE_NOTE + " TEXT, ";
        CREATE_TABLE += TRANSLATE_PRACTICE_TIMES + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_ADD + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_EDIT + " INTEGER, ";
        CREATE_TABLE += TRANSLATE_DELETE + " INTEGER ";
        CREATE_TABLE += " ) ";
        db.execSQL(CREATE_TABLE);

        // PEDOMETER
        CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_PEDOMETER;
        CREATE_TABLE += " ( ";
        CREATE_TABLE += PEDOMETER_DATE + " TEXT PRIMARY KEY , ";
        CREATE_TABLE += PEDOMETER_STEP + " INTEGER, ";
        CREATE_TABLE += PEDOMETER_STEPON + " INTEGER ";
        CREATE_TABLE += " ) ";
        db.execSQL(CREATE_TABLE);

        // LOGS
        CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_USERLOG;
        CREATE_TABLE += " ( ";
        CREATE_TABLE += USERLOG_TIME + " INTEGER PRIMARY KEY , ";
        CREATE_TABLE += USERLOG_CODE + " INTEGER, ";
        CREATE_TABLE += USERLOG_ID + " TEXT ";
        CREATE_TABLE += " ) ";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
//            db.execSQL("ALTER TABLE " + TABLE_TRANSLATE + " ADD COLUMN " + TRANSLATE_CONTEXT + " TEXT");
//            db.execSQL("ALTER TABLE " + TABLE_TRANSLATE + " ADD COLUMN " + TRANSLATE_USERID + " TEXT");
//            db.execSQL("ALTER TABLE " + TABLE_TRANSLATE + " ADD COLUMN " + TRANSLATE_USERNAME + " TEXT");
//            db.execSQL("ALTER TABLE " + TABLE_TRANSLATE + " ADD COLUMN " + TRANSLATE_NOTE + " TEXT");
//            db.execSQL("ALTER TABLE " + TABLE_TRANSLATE + " ADD COLUMN " + TRANSLATE_PRACTICE_TIMES + " TEXT");
        }
    }
}
