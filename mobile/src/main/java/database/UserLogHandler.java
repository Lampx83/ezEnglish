package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import controlvariable.MyGlobal;
import entity.UserLog;


public class UserLogHandler extends DataBaseHandler {
    public UserLogHandler(Context context) {
        super(context, "translate.db", null, MyGlobal.db_ver);
    }

    public void insert(UserLog object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERLOG_TIME, System.currentTimeMillis());
        values.put(USERLOG_CODE, object.code);
        values.put(USERLOG_ID, object.id);
        db.insert(TABLE_USERLOG, null, values);
        db.close();
    }


    public synchronized int getNumber(String select, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        int number = 0;
        Cursor c = db.rawQuery("SELECT " + select + " FROM " + TABLE_USERLOG + " WHERE " + where, null);
        if (c != null && c.getCount() != 0 && c.moveToFirst()) {

            number = c.getInt(0);
        }
        c.close();
        db.close();
        return number;
    }

//    public ArrayList<UserLog> getAllBy(String where, String limitoffset) {
//        ArrayList<UserLog> recordsList = new ArrayList<UserLog>();
//        String sql = "";
//        sql += "SELECT * FROM " + TABLE_PEDOMETER;
//        sql += " " + where;
//        sql += " ORDER BY " + PEDOMETER_DATE + " DESC";
//        sql += " " + limitoffset;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) {
//            do {
//                UserLog object = new UserLog();
//                object.time = cursor.getInt(cursor.getColumnIndex(PEDOMETER_DATE));
//                object.code = cursor.getInt(cursor.getColumnIndex(PEDOMETER_STEP));
//                object.id = cursor.getString(cursor.getColumnIndex(PEDOMETER_STEPON));
//                recordsList.add(object);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return recordsList;
//    }


}
