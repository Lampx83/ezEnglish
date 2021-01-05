package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import controlvariable.MyGlobal;
import entity_display.MTerms;
import flashcard.Image;

public class TermsHandler extends DataBaseHandler implements IdatabaseMethod {

    public TermsHandler(Context context) {
        super(context, "translate.db", null, MyGlobal.db_ver);
    }


    public synchronized void add(MTerms s) {  //Neu co roi thi update (cai nay khong duoc bo di
        if (!checkIdExist(Long.parseLong(s.getItemID()))) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CARD_DEFINITION, s.getDefinition());
            values.put(CARD_ID, Long.parseLong(s.getItemID()));
            values.put(CARD_TERM, s.getTerm());
            values.put(TRANSLATE_LANGFROM, s.langfrom);
            values.put(TRANSLATE_LANGTO, s.langto);
            values.put(MARK, s.mark);
            values.put(BOOKMARK_TIME, s.bookmarkTime);
            if (s.getImage() != null) {
                values.put(CARD_IMAGE, s.getImage().getUrl());
                values.put(CARD_IMAGE_WIDTH, s.getImage().getWidth());
                values.put(CARD_IMAGE_HEIGHT, s.getImage().getHeight());
            }
            values.put(TRANSLATEID, s.translateID);
            values.put(BOX, s.box); // -1 is default
            db.insert(TABLE_CARD, null, values);
            db.close();
        } else {
            update(s);
        }
    }

    public synchronized void update(MTerms s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CARD_DEFINITION, s.getDefinition());
        values.put(CARD_TERM, s.getTerm());
        values.put(MARK, s.mark);
        values.put(TRANSLATE_LANGFROM, s.langfrom);
        values.put(TRANSLATE_LANGTO, s.langto);
        if (s.getImage() != null) {
            values.put(CARD_IMAGE, s.getImage().getUrl());
            values.put(CARD_IMAGE_WIDTH, s.getImage().getWidth());
            values.put(CARD_IMAGE_HEIGHT, s.getImage().getHeight());
        }
        values.put(BOOKMARK_TIME, s.bookmarkTime);
        values.put(BOX, s.box);
        db.update(TABLE_CARD, values, CARD_ID + " = ?", new String[]{s.getItemID()});
        db.close();
    }


    @Override
    public synchronized ArrayList<MTerms> getAllBy(String selection, String[] selectionArgs, String orderBy) {
        ArrayList<MTerms> list = new ArrayList<MTerms>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARD, new String[]{CARD_DEFINITION, CARD_ID, CARD_TERM, MARK, CARD_IMAGE, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, BOOKMARK_TIME, BOX, TRANSLATE_LANGFROM, TRANSLATE_LANGTO, TRANSLATEID}, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                MTerms object = new MTerms();
                object.setDefinition(cursor.getString(0));
                object.setItemID("" + cursor.getLong(1));
                object.setTerm(cursor.getString(2));
                object.mark = cursor.getInt(3);
                object.type = 4;
                try {
                    Image image = new Image();
                    image.setUrl(cursor.getString(4));
                    image.setWidth(cursor.getString(5));
                    image.setHeight(cursor.getString(6));
                    object.setImage(image);
                } catch (Exception ex) {

                }

                object.bookmarkTime = cursor.getInt(7);
                object.box = cursor.getInt(8);
                object.langfrom = cursor.getString(9);
                object.langto = cursor.getString(10);
                object.translateID = cursor.getLong(cursor.getColumnIndex(TRANSLATEID));
                list.add(object);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized boolean checkIdExist(long ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean check = false;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CARD + " WHERE " + CARD_ID + "= ?", new String[]{"" + ID});
        if (c != null && c.getCount() != 0) {
            check = true;
        }
        c.close();
        db.close();
        return check;
    }

    public synchronized boolean checkTranslateIdExist(long translateID) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean check = false;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CARD + " WHERE " + TRANSLATEID + "= ?", new String[]{"" + translateID});
        if (c != null && c.getCount() != 0) {
            check = true;
        }
        c.close();
        db.close();
        return check;
    }

    public synchronized boolean checkTermExist(String term) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean check = false;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CARD + " WHERE " + CARD_TERM + "= ?", new String[]{term});
        if (c != null && c.getCount() != 0) {
            check = true;
        }
        c.close();
        db.close();
        return check;
    }

    public synchronized MTerms getByID(long ID) { // ID luon la so nguyen nhung de string cho de xuly
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARD, new String[]{CARD_DEFINITION, CARD_ID, CARD_TERM, MARK, CARD_IMAGE, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, BOOKMARK_TIME, BOX, TRANSLATE_LANGFROM, TRANSLATE_LANGTO, TRANSLATEID}, CARD_ID + "=?", new String[]{"" + ID}, null, null, null);
        MTerms object = null;
        if (cursor.moveToFirst()) {
            object = new MTerms();
            object.setDefinition(cursor.getString(0));
            object.setItemID("" + cursor.getLong(1));
            object.setTerm(cursor.getString(2));
            object.mark = cursor.getInt(3);
            try {
                Image image = new Image();
                image.setUrl(cursor.getString(4));
                image.setWidth(cursor.getString(5));
                image.setHeight(cursor.getString(6));
                object.setImage(image);
            } catch (Exception ex) {

            }

            object.bookmarkTime = cursor.getInt(7);
            object.box = cursor.getInt(8);
            object.langfrom = cursor.getString(9);
            object.langto = cursor.getString(10);
            object.translateID = cursor.getLong(cursor.getColumnIndex(TRANSLATEID));
        }
        cursor.close();
        db.close();
        return object;
    }

    public synchronized MTerms getByTranslateID(long translateID) { // ID luon la so nguyen nhung de string cho de xuly
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARD, new String[]{CARD_DEFINITION, CARD_ID, CARD_TERM, MARK, CARD_IMAGE, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, BOOKMARK_TIME, BOX, TRANSLATE_LANGFROM, TRANSLATE_LANGTO}, TRANSLATEID + "=?", new String[]{"" + translateID}, null, null, null);
        MTerms object = null;
        if (cursor.moveToFirst()) {
            object = new MTerms();
            object.setDefinition(cursor.getString(0));
            object.setItemID("" + cursor.getLong(1));
            object.setTerm(cursor.getString(2));
            object.mark = cursor.getInt(3);
            try {
                Image image = new Image();
                image.setUrl(cursor.getString(4));
                image.setWidth(cursor.getString(5));
                image.setHeight(cursor.getString(6));
                object.setImage(image);
            } catch (Exception ex) {

            }

            object.bookmarkTime = cursor.getInt(7);
            object.box = cursor.getInt(8);
            object.langfrom = cursor.getString(9);
            object.langto = cursor.getString(10);
        }
        cursor.close();
        db.close();
        return object;
    }


    public int getCount(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_CARD, new String[]{CARD_ID}, selection, selectionArgs, null, null, null);
        if (c == null || c.getCount() == 0) {
            return 0;
        } else {
            return c.getCount();
        }
    }

    @Override
    public void DeleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARD, null, null);
    }

    public void deleteBy(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARD, selection, selectionArgs);
    }

    public synchronized int getNumber(String select, String where) {
        SQLiteDatabase db = this.getReadableDatabase();
        int number = 0;
        Cursor c = db.rawQuery("SELECT " + select + " FROM " + TABLE_CARD + " JOIN " + TABLE_TRANSLATE + " ON " + TABLE_CARD + "." + TRANSLATEID + "=" + TABLE_TRANSLATE + "." + TRANSLATEID + " WHERE " + where, null);
        if (c != null && c.getCount() != 0 && c.moveToFirst()) {

            number = c.getInt(0);
        }
        c.close();
        db.close();
        return number;
    }
}