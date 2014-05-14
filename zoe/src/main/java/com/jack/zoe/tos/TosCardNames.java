package com.jack.zoe.tos;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jack.zoe.util.J;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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

        private void copyDatabase(Context context) {
            try {
                InputStream input = context.getAssets().open(NAME);
                OutputStream output = new FileOutputStream(context.getDatabasePath(NAME).getPath());

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
        }
    }

    private final List<String> cardNames = new ArrayList<String>();

    public TosCardNames(Context context) throws IOException {
        this.loadCardsFromTextFile(context);
        this.loadCardsFromDatabase(context);
    }

    public String findNameByMonsterId(int monsterId) {
        return cardNames.get(monsterId - 1);
    }

    private void loadCardsFromTextFile(Context context) throws IOException {
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

    private void loadCardsFromDatabase(Context context) {
        DbHelper helper = new DbHelper(context);
        helper.getCardMap();
    }
}
