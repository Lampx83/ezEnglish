package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import activity.MainActivity;
import controlvariable.MyGlobal;
import entity.Translate;
import entity_display.MTranslate;
import others.MyPoint;


public class TranslateHandler extends DataBaseHandler {
    public TranslateHandler(Context context) {
        super(context, "translate.db", null, MyGlobal.db_ver);
    }

    public long insert(Translate object) {
        if (!checkIdExist(object)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            if (object.translateid == 0)
                values.put(TRANSLATEID, System.currentTimeMillis());
            else
                values.put(TRANSLATEID, object.translateid);

            values.put(TRANSLATE_LANGFROM, object.langfrom);
            values.put(TRANSLATE_LANGTO, object.langto);
            values.put(TRANSLATE_TEXT, object.text);
            values.put(TRANSLATE_RESULT, object.result);
            values.put(TRANSLATE_DEVICE, object.device);
            values.put(TRANSLATE_SERVER, object.server);
            values.put(TRANSLATE_TIME, object.time);
            values.put(TRANSLATE_DATE, object.date);
            values.put(TRANSLATE_IMAGE, object.image);

            values.put(TRANSLATE_LATITUDE, MainActivity.latitude);
            values.put(TRANSLATE_LONGITUDE, MainActivity.longitude);
            values.put(TRANSLATE_TTS, object.tts);
            values.put(TRANSLATE_SCORE, object.score);
            values.put(TRANSLATE_STEP, object.step);
            values.put(TRANSLATE_STEPON, object.stepon);
            values.put(TRANSLATE_CONTEXT, object.learn_context);
            values.put(TRANSLATE_USERID, object.user_id);
            values.put(TRANSLATE_USERNAME, object.user_name);


            long translateid = db.insert(TABLE_TRANSLATE, null, values);
            db.close();
            return translateid;
        }
        return -1;
    }

    public synchronized void update(Translate object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSLATE_LANGFROM, object.langfrom);
        values.put(TRANSLATE_LANGTO, object.langto);
        values.put(TRANSLATE_TEXT, object.text);
        values.put(TRANSLATE_RESULT, object.result);
        values.put(TRANSLATE_DEVICE, object.device);
        values.put(TRANSLATE_SERVER, object.server);
        values.put(TRANSLATE_TIME, object.time);
        values.put(TRANSLATE_DATE, object.date);
        values.put(TRANSLATE_IMAGE, object.image);
        values.put(TRANSLATE_LATITUDE, MainActivity.latitude);

        values.put(TRANSLATE_LONGITUDE, MainActivity.longitude);
        values.put(TRANSLATE_TTS, object.tts);
        values.put(TRANSLATE_SCORE, object.score);
        values.put(TRANSLATE_STEP, object.step);
        values.put(TRANSLATE_STEPON, object.stepon);
        values.put(TRANSLATE_NOTE, object.note);
        values.put(TRANSLATE_PRACTICE_TIMES, object.practice_times);

        values.put(TRANSLATE_ADD, object.add);
        values.put(TRANSLATE_EDIT, object.edit);
        values.put(TRANSLATE_DELETE, object.delete);

        db.update(TABLE_TRANSLATE, values, TRANSLATEID + " = ?", new String[]{"" + object.translateid});
        db.close();
    }


    public synchronized Translate getByID(long translateid) {
        Translate object = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "";
        sql += "SELECT * FROM " + TABLE_TRANSLATE;
        sql += " WHERE " + TRANSLATEID + " = '" + translateid + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            object = new Translate();
            object.translateid = translateid;
            object.langfrom = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGFROM));
            object.langto = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGTO));
            object.text = cursor.getString(cursor.getColumnIndex(TRANSLATE_TEXT));
            object.result = cursor.getString(cursor.getColumnIndex(TRANSLATE_RESULT));
            object.device = cursor.getInt(cursor.getColumnIndex(TRANSLATE_DEVICE));
            object.server = cursor.getString(cursor.getColumnIndex(TRANSLATE_SERVER));
            object.time = cursor.getLong(cursor.getColumnIndex(TRANSLATE_TIME));
            object.date = cursor.getString(cursor.getColumnIndex(TRANSLATE_DATE));
            object.image = cursor.getString(cursor.getColumnIndex(TRANSLATE_IMAGE));
            object.latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LATITUDE)));

            object.longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LONGITUDE)));
            object.tts = cursor.getInt(cursor.getColumnIndex(TRANSLATE_TTS));
            object.score = cursor.getInt(cursor.getColumnIndex(TRANSLATE_SCORE));
            object.step = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEP));
            object.stepon = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEPON));
            object.learn_context = cursor.getString(cursor.getColumnIndex(TRANSLATE_CONTEXT));
            object.user_id = cursor.getString(cursor.getColumnIndex(TRANSLATE_USERID));
            object.user_name = cursor.getString(cursor.getColumnIndex(TRANSLATE_USERNAME));
            object.note = cursor.getString(cursor.getColumnIndex(TRANSLATE_NOTE));
            object.practice_times = cursor.getInt(cursor.getColumnIndex(TRANSLATE_PRACTICE_TIMES));

            object.add = cursor.getInt(cursor.getColumnIndex(TRANSLATE_ADD));
            object.delete = cursor.getInt(cursor.getColumnIndex(TRANSLATE_DELETE));
            object.edit = cursor.getInt(cursor.getColumnIndex(TRANSLATE_EDIT));

        }
        cursor.close();
        db.close();
        return object;
    }


    public ArrayList<MTranslate> getAllBy(String where, String limitoffset) {
        ArrayList<MTranslate> recordsList = new ArrayList<MTranslate>();
        String sql = "";
        sql += "SELECT ";
        sql += TABLE_TRANSLATE + ".*,";
        sql += TABLE_CARD + "." + CARD_TERM + ",";
        sql += TABLE_CARD + "." + BOX;
        sql += " FROM " + TABLE_TRANSLATE + " LEFT JOIN " + TABLE_CARD + " ON " + TABLE_CARD + "." + TRANSLATEID + "=" + TABLE_TRANSLATE + "." + TRANSLATEID;
        sql += " " + where;
        sql += " ORDER BY " + TRANSLATE_TIME + " DESC";
        sql += " " + limitoffset;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                MTranslate object = new MTranslate();
                object.translateid = cursor.getLong(cursor.getColumnIndex(TRANSLATEID));
                object.langfrom = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGFROM));
                object.langto = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGTO));
                object.text = cursor.getString(cursor.getColumnIndex(TRANSLATE_TEXT));
                object.result = cursor.getString(cursor.getColumnIndex(TRANSLATE_RESULT));
                object.device = cursor.getInt(cursor.getColumnIndex(TRANSLATE_DEVICE));
                object.server = cursor.getString(cursor.getColumnIndex(TRANSLATE_SERVER));
                object.time = cursor.getLong(cursor.getColumnIndex(TRANSLATE_TIME));
                object.date = cursor.getString(cursor.getColumnIndex(TRANSLATE_DATE));
                object.image = cursor.getString(cursor.getColumnIndex(TRANSLATE_IMAGE));
                object.latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LATITUDE)));

                if (cursor.getString(cursor.getColumnIndex(CARD_TERM)) != null) {
                    object.mark = 1;
                }

                object.longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LONGITUDE)));
                object.tts = cursor.getInt(cursor.getColumnIndex(TRANSLATE_TTS));
                object.score = cursor.getInt(cursor.getColumnIndex(TRANSLATE_SCORE));
                object.step = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEP));
                object.stepon = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEPON));
                object.learn_context = cursor.getString(cursor.getColumnIndex(TRANSLATE_CONTEXT));
                object.user_id = cursor.getString(cursor.getColumnIndex(TRANSLATE_USERID));

                recordsList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

    public ArrayList<Translate> getAllByforJson(String where, String limitoffset) {
        ArrayList<Translate> recordsList = new ArrayList<Translate>();
        String sql = "";
        sql += "SELECT * FROM " + TABLE_TRANSLATE;
        sql += " " + where;
        sql += " ORDER BY " + TRANSLATEID + " DESC";
        sql += " " + limitoffset;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Translate object = new Translate();
                object.translateid = cursor.getInt(cursor.getColumnIndex(TRANSLATEID));
                object.langfrom = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGFROM));
                object.langto = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGTO));
                object.text = cursor.getString(cursor.getColumnIndex(TRANSLATE_TEXT));
                object.result = cursor.getString(cursor.getColumnIndex(TRANSLATE_RESULT));
                object.time = cursor.getLong(cursor.getColumnIndex(TRANSLATE_TIME));
                object.longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LONGITUDE)));
                object.latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LATITUDE)));
                object.learn_context = cursor.getString(cursor.getColumnIndex(TRANSLATE_CONTEXT));
                object.image = cursor.getString(cursor.getColumnIndex(TRANSLATE_IMAGE));
                object.note = cursor.getString(cursor.getColumnIndex(TRANSLATE_NOTE));
                object.tts = cursor.getInt(cursor.getColumnIndex(TRANSLATE_TTS));
                object.score = cursor.getInt(cursor.getColumnIndex(TRANSLATE_SCORE));
                object.practice_times = cursor.getInt(cursor.getColumnIndex(TRANSLATE_PRACTICE_TIMES));
                object.step = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEP));
                object.stepon = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEPON));

                object.delete = cursor.getInt(cursor.getColumnIndex(TRANSLATE_DELETE));
                object.add = cursor.getInt(cursor.getColumnIndex(TRANSLATE_ADD));
                object.edit = cursor.getInt(cursor.getColumnIndex(TRANSLATE_EDIT));

                // object.mark = cursor.getInt(cursor.getColumnIndex(TABLE_TRANSLATE+"."+MARK));
                if (object.image != null)
                    if (object.image.startsWith("img"))
                        object.image = "dropbox";

                recordsList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }


    public ArrayList<MyPoint> getAllByforMap(String where, String limitoffset) {
        ArrayList<MyPoint> recordsList = new ArrayList<MyPoint>();
        String sql = "";
        sql += "SELECT * FROM " + TABLE_TRANSLATE;
        sql += " " + where;
        sql += " ORDER BY " + TRANSLATEID + " DESC";
        sql += " " + limitoffset;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                MyPoint object = new MyPoint(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LATITUDE))), Double.parseDouble(cursor.getString(cursor.getColumnIndex(TRANSLATE_LONGITUDE))));
                if (object.getPosition().latitude == 0)
                    continue;
                object.translateid = cursor.getLong(cursor.getColumnIndex(TRANSLATEID));
                object.langfrom = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGFROM));
                object.langto = cursor.getString(cursor.getColumnIndex(TRANSLATE_LANGTO));
                object.text = cursor.getString(cursor.getColumnIndex(TRANSLATE_TEXT));
                object.result = cursor.getString(cursor.getColumnIndex(TRANSLATE_RESULT));
                object.device = cursor.getInt(cursor.getColumnIndex(TRANSLATE_DEVICE));
                object.server = cursor.getString(cursor.getColumnIndex(TRANSLATE_SERVER));

                object.time = cursor.getLong(cursor.getColumnIndex(TRANSLATE_TIME));
                object.date = cursor.getString(cursor.getColumnIndex(TRANSLATE_DATE));
                object.image = cursor.getString(cursor.getColumnIndex(TRANSLATE_IMAGE));
                object.tts = cursor.getInt(cursor.getColumnIndex(TRANSLATE_TTS));
                object.score = cursor.getInt(cursor.getColumnIndex(TRANSLATE_SCORE));
                object.step = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEP));
                object.stepon = cursor.getInt(cursor.getColumnIndex(TRANSLATE_STEPON));

                recordsList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

    public void deleteBy(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSLATE, selection, selectionArgs);
    }

    public synchronized boolean checkIdExist(Translate object) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean check = false;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TRANSLATE + " WHERE " + TRANSLATEID + "= " + object.translateid, null);
        if (c != null && c.getCount() != 0) {
            check = true;
        }
        c.close();
        db.close();
        return check;
    }

    public synchronized int getNumber(String select, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        int number = 0;
        Cursor c = db.rawQuery("SELECT " + select + " FROM " + TABLE_TRANSLATE + " WHERE " + where, null);
        if (c != null && c.getCount() != 0 && c.moveToFirst()) {

            number = c.getInt(0);
        }
        c.close();
        db.close();
        return number;
    }

}
