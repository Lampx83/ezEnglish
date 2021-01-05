package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import controlvariable.MyGlobal;
import entity.Pedometer;


public class PedometerHandler extends DataBaseHandler {
    public PedometerHandler(Context context) {
        super(context, "translate.db", null, MyGlobal.db_ver);
    }

    public void insert(Pedometer object) {
        if (!checkIdExist(object.date)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PEDOMETER_DATE, object.date);
            values.put(PEDOMETER_STEP, object.steps);
            values.put(PEDOMETER_STEPON, object.stepson);
            db.insert(TABLE_PEDOMETER, null, values);
            db.close();
        } else {
            update(object);
        }
    }

    public synchronized void update(Pedometer object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PEDOMETER_STEP, object.steps);
        values.put(PEDOMETER_STEPON, object.stepson);
        db.update(TABLE_PEDOMETER, values, PEDOMETER_DATE + " = ?", new String[]{object.date});
        db.close();
    }


    public synchronized Pedometer getByID(String date) {
        Pedometer object = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "";
        sql += "SELECT * FROM " + TABLE_PEDOMETER;
        sql += " WHERE " + PEDOMETER_DATE + " = '" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            object = new Pedometer();
            object.date = date;
            object.steps = cursor.getInt(cursor.getColumnIndex(PEDOMETER_STEP));
            object.stepson = cursor.getInt(cursor.getColumnIndex(PEDOMETER_STEPON));
        }
        cursor.close();
        db.close();
        return object;
    }


    public ArrayList<Pedometer> getAllBy(String where, String limitoffset) {
        ArrayList<Pedometer> recordsList = new ArrayList<Pedometer>();
        String sql = "";
        sql += "SELECT * FROM " + TABLE_PEDOMETER;
        sql += " " + where;
        sql += " ORDER BY " + PEDOMETER_DATE + " DESC";
        sql += " " + limitoffset;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Pedometer object = new Pedometer();
                object.date = cursor.getString(cursor.getColumnIndex(PEDOMETER_DATE));
                object.steps = cursor.getInt(cursor.getColumnIndex(PEDOMETER_STEP));
                object.stepson = cursor.getInt(cursor.getColumnIndex(PEDOMETER_STEPON));
                recordsList.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

    public synchronized boolean checkIdExist(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean check = false;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PEDOMETER + " WHERE " + PEDOMETER_DATE + "= '" + date + "'", null);
        if (c != null && c.getCount() != 0) {
            check = true;
        }
        c.close();
        db.close();
        return check;
    }

}
