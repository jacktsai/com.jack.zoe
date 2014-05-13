package com.jack.zoe.tos;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

import com.jack.zoe.util.J;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.json.*;

public class TosFile {
    private static final String TAG = TosFile.class.getSimpleName();
    private static final File TosFile = new File("/data/data/com.madhead.tos.zh/shared_prefs/com.madhead.tos.zh.xml");
    private static final int SignatureLength = 32;
    private static long previousModified = 0;

    private static TosCardNames cardNames;

    public static boolean isChanged() {
        long lastModified = TosFile.lastModified();

        if (previousModified != lastModified) {
            J.d2(TAG, "TosFile '%s' changed", TosFile.getPath());
            previousModified = lastModified;
            return true;
        }

        return false;
    }

    public static TosFile snapshot(Context context) {
        File cacheDir = context.getCacheDir();
        File cacheFile = new File(cacheDir, "com.madhead.tos.zh.xml");

        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(String.format("cp %s %s\n", TosFile.getPath(), cacheFile.getPath()));
            outputStream.writeBytes(String.format("chmod 777 %s\n", cacheFile.getPath()));
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();

            if (process.exitValue() != 255) {
                checkTosCardNames(context);
                return new TosFile(cacheFile);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    private static void checkTosCardNames(Context context) {
        if (cardNames == null) {
            try {
                cardNames = new TosCardNames(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final File cacheFile;

    protected TosFile(File cacheFile) {
        this.cacheFile = cacheFile;
    }

    public int GAME_LOCAL_USER() {
        try {
            return this.getInt("GAME_LOCAL_USER");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return -1;
    }

    public String GAME_UNIQUE_KEY() {
        try {
            return this.getString("GAME_UNIQUE_KEY");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    public List<Integer> USER_COLLECTED_MONSTER_IDS() {
        try {
            String idArrayString = this.getSignaturedString("MH_CACHE_RUNTIME_USER_COLLECTED_MONSTER_IDS");
            if (idArrayString != null) {
                List<Integer> result = new ArrayList<Integer>();
                for (String idString : idArrayString.split(",")) {
                    int id = Integer.parseInt(idString);
                    result.add(id);
                }

                return result;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    public Iterable<FloorWave> CURRENT_FLOOR_WAVES() {
        try {
            String jsonString = this.getSignaturedString("MH_CACHE_RUNTIME_DATA_CURRENT_FLOOR_WAVES");
            if (jsonString == null) {
                return null;
            }

            JSONArray waveArray = new JSONArray(jsonString);
            List<FloorWave> result = new ArrayList<FloorWave>();
            for (int i = 0; i < waveArray.length(); i++) {
                JSONObject wave = (JSONObject) waveArray.get(i);
                result.add(new FloorWave(wave));
            }

            return result;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    private String getString(String name) throws XmlPullParserException, IOException {
        XmlPullParserFactory parserFactory;
        XmlPullParser pullParser;
        parserFactory = XmlPullParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        pullParser = parserFactory.newPullParser();

        FileReader fileReader = new FileReader(this.cacheFile.getPath());
        pullParser.setInput(fileReader);

        int eventType = pullParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (pullParser.getName().equals("string")) {
                    if (pullParser.getAttributeValue(0).equals(name)) {
                        eventType = pullParser.next();
                        if (eventType == XmlPullParser.TEXT) {
                            return pullParser.getText();
                        }
                    }
                }
            }
            eventType = pullParser.next();
        }

        return null;
    }

    private String getSignaturedString(String name) throws XmlPullParserException, IOException {
        String rawString = this.getString(name);
        if (rawString == null || rawString.length() <= SignatureLength) {
            return null;
        }

        return rawString.substring(SignatureLength);
    }

    private int getInt(String name) throws XmlPullParserException, IOException {
        XmlPullParserFactory parserFactory;
        XmlPullParser pullParser;
        parserFactory = XmlPullParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        pullParser = parserFactory.newPullParser();

        FileReader fileReader = new FileReader(this.cacheFile.getPath());
        pullParser.setInput(fileReader);

        int eventType = pullParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                if (pullParser.getName().equals("int")) {
                    if (pullParser.getAttributeValue(0).equals(name)) {
                        return Integer.parseInt(pullParser.getAttributeValue(1));
                    }
                }
            }
            eventType = pullParser.next();
        }

        return -1;
    }

    public class FloorWave {
        private final JSONObject json;

        FloorWave(JSONObject json) {
            this.json = json;
        }

        public Iterable<FloorEnemy> enemies() throws JSONException {
            if (json.isNull("enemies")) {
                return null;
            }

            JSONArray enemyArray = json.getJSONArray("enemies");
            List<FloorEnemy> result = new ArrayList<FloorEnemy>();
            for (int i = 0; i < enemyArray.length(); i++) {
                JSONObject enemy = (JSONObject)enemyArray.get(i);
                result.add(new FloorEnemy(enemy));
            }

            return result;
        }
    }

    public class FloorEnemy {
        private final JSONObject json;

        FloorEnemy(JSONObject json) {
            this.json = json;
        }

        public LootItem lootItem() throws JSONException {
            if (json.isNull("lootItem")) {
                return null;
            }

            return new LootItem(json.getJSONObject("lootItem"));
        }
    }

    public class LootItem {
        private final JSONObject json;

        LootItem(JSONObject json) {
            this.json = json;
        }

        public String type() throws JSONException {
            return json.getString("type");
        }

        public int amount() throws JSONException {
            return json.getInt("amount");
        }

        public Card card() throws JSONException {
            return new Card(json.getJSONObject("card"));
        }
    }

    public class Card {
        private final JSONObject json;

        Card(JSONObject json) {
            this.json = json;
        }

        public int monsterId() throws JSONException {
            return json.getInt("monsterId");
        }

        public String monsterName() throws JSONException {
            return cardNames.findNameByMonsterId(this.monsterId());
        }
    }
 }
