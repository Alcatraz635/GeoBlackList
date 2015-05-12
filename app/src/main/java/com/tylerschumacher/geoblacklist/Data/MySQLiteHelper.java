package com.tylerschumacher.geoblacklist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBBlock.db";
    public static final String ADDRESS_TABLE_NAME = "addresses";
    public static final String ADDRESS_COLUMN_ID = "id";
    public static final String ADDRESS_COLUMN_ADDRESS = "address";
    public static final String ADDRESS_COLUMN_NUMBER_ONE = "number_one";
    public static final String ADDRESS_COLUMN_NUMBER_TWO = "number_two";
    public static final String ADDRESS_COLUMN_NUMBER_THREE = "number_three";
    public static final String ADDRESS_COLUMN_NUMBER_FOUR = "number_four";
    public static final String ADDRESS_COLUMN_NUMBER_FIVE = "number_five";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                          "create table addresses " +
                                  "(id integer primary key, address text, number_one text, number_two text, number_three text, number_four text, number_five text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS addresses");
        onCreate(db);
    }

    public long insertAddress(String addr, String num1, String num2, String num3, String num4, String num5) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("address", addr);
        contentValues.put("number_one", num1);
        contentValues.put("number_two", num2);
        contentValues.put("number_three", num3);
        contentValues.put("number_four", num4);
        contentValues.put("number_five", num5);

        return db.insert("addresses", null, contentValues);
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from addresses where id=" + id + "", null);
        return res;
    }
    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "addresses");
        return numRows;
    }
    public boolean updateAddress(Integer id, String addr, String num1, String num2, String num3, String num4, String num5) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", addr);
        contentValues.put("number_one", num1);
        contentValues.put("number_two", num2);
        contentValues.put("number_three", num3);
        contentValues.put("number_four", num4);
        contentValues.put("number_five", num5);
        db.update("addresses", contentValues, "id = ? ", new String[] {
                                                                              Integer.toString(id)
        });
        return true;
    }

    public Integer deleteAddress(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("addresses",
                                "id = ? ",
                                new String[] {
                                                     Integer.toString(id)
                                });
    }
    public ArrayList getAllAddresses() {
        ArrayList < String > array_list = new ArrayList < > ();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from addresses", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getPosition()));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}