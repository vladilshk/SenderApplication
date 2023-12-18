package com.vovai.lab_2.repostitory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RequestHistoryRepository extends SQLiteOpenHelper {

    public RequestHistoryRepository(Context context) {
        super(context, "sender_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "requests" + " (" + "id"
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + "value"
                + " REAL, " + "date" + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "requests");
        onCreate(db);
    }

    public void insert(SQLiteDatabase db, double value, String date) {
        db.execSQL("INSERT OR IGNORE INTO " + "requests" + " (" + "value"
                + ", " + "date"  + ") VALUES (" + value + ", \"" + date + "\");");
    }

    public void delete(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + "requests" + " WHERE _id IN (SELECT MIN(_id) FROM " + "requests" + ");");
    }

    public List<String> getHistory() {
        List<String> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Выбираем все записи из таблицы "requests"
        Cursor cursor = db.rawQuery("SELECT * FROM requests", null);

        try {
            while (cursor.moveToNext()) {
                // Получаем значение и дату из записи и добавляем в список
                double value = cursor.getDouble(cursor.getColumnIndex("value"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                historyList.add("Value: " + value + ", Date: " + date);
            }
        } finally {
            cursor.close();
        }

        return historyList;
    }
}

