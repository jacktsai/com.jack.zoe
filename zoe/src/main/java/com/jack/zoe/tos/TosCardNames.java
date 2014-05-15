package com.jack.zoe.tos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jack.zoe.util.J;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;

public class TosCardNames {
    private static final String TAG = TosCardNames.class.getSimpleName();

    class DbHelper extends SQLiteOpenHelper {

        private static final int VERSION = 1;
        private static final String NAME = "tos_cards.db";

        DbHelper(Context context) {
            super(context, NAME, null, VERSION);
            this.copyDatabase(context);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public Dictionary<Integer, String> getCardMap() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT _id, CardName FROM App_Game001_Custom", null);
            Dictionary<Integer, String> cardMap = new Hashtable<Integer, String>();

            if (cursor.moveToFirst()) {
                do {
                    cardMap.put(cursor.getInt(0), cursor.getString(1));
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            return cardMap;
        }

        public Card findById(int monsterId) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Number, CardName, Rarity, Race FROM App_Game001_Custom WHERE _id = ?", new String[] {Integer.toString(monsterId)});

            try {
                if (cursor.moveToFirst()) {
                    Card result = new Card();
                    result.number = cursor.getString(0);
                    result.name = cursor.getString(1);
                    result.rarity = cursor.getInt(2);
                    result.race = cursor.getString(3);
                    return result;
                }
            } finally {
                cursor.close();
                db.close();
            }

            return null;
        }

        private void copyDatabase(Context context) {
            J.d2(TAG, "begin copy database");
            try {
                File targetPath = context.getDatabasePath(NAME);
                targetPath.getParentFile().mkdir();
                OutputStream output = new FileOutputStream(targetPath, false);
                InputStream input = context.getAssets().open(NAME);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            J.d2(TAG, "end copy database");
        }
    }

    private DbHelper db;

    public TosCardNames(Context context) {
        this.db = new DbHelper(context);
    }

    public Card findNameByMonsterId(int monsterId) {
        return db.findById(monsterId);
    }

    public class Card {
        public String number;
        public String name;
        public int rarity;
        public String race;
    }
}
