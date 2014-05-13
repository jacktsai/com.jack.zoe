package com.jack.zoe.tos;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class TosCardNames {

    class DbHelper extends SQLiteOpenHelper {

        private static final int VERSION = 1;
        private static final String NAME = "tos_cards.db";

        DbHelper(Context context) {
            super(context, NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    }

    private final List<String> cardNames = new ArrayList<String>();

    public TosCardNames(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream tos_card_names = assetManager.open("tos_card_names.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(tos_card_names);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            cardNames.add(line);
        }

        inputStreamReader.close();
    }

    public String findNameByMonsterId(int monsterId) {
        return cardNames.get(monsterId);
    }
}
