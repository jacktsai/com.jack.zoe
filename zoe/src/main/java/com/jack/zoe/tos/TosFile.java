package com.jack.zoe.tos;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class TosFile {
    private static final String TAG = TosFile.class.getSimpleName();
    private static final File SourceFile = new File("/data/data/com.madhead.tos.zh/shared_prefs/com.madhead.tos.zh.xml");
    private static final int SIGNATURE_LENGTH = 32;
    private static long previousModified = 0;

    private static TosCardNames cardNames;

    public static boolean isChanged() {
        if (SourceFile.exists()) {
            long lastModified = SourceFile.lastModified();
            if (previousModified != lastModified) {
                previousModified = lastModified;
                return true;
            }
        }

        return false;
    }

    public static TosFile snapshot(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(String.format("chmod 777 %s\n", SourceFile));
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();

            checkTosCardNames(context);
            SharedPreferences preferences = createSharedPreferences(SourceFile, Context.MODE_PRIVATE);
            return new TosFile(preferences);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
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

    private static SharedPreferences createSharedPreferences(File file, int mode) {
        try {
            Class<?> implClass = Class.forName("android.app.SharedPreferencesImpl");
            Constructor<?> constructor = implClass.getDeclaredConstructor(File.class, int.class);
            return  (SharedPreferences)constructor.newInstance(file, mode);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    private final SharedPreferences preferences;

    protected TosFile(SharedPreferences preferences) {
        this.preferences = preferences;

    }

    public int GAME_LOCAL_USER() {
        return this.preferences.getInt("GAME_LOCAL_USER", -1);
    }

    public String GAME_UNIQUE_KEY() {
        return this.preferences.getString("GAME_UNIQUE_KEY", null);
    }

    public List<Integer> USER_COLLECTED_MONSTER_IDS() {
        String idArrayString = this.preferences.getString("MH_CACHE_RUNTIME_USER_COLLECTED_MONSTER_IDS", null)
            .substring(SIGNATURE_LENGTH);

        List<Integer> result = new ArrayList<Integer>();
        for (String idString : idArrayString.split(",")) {
            int id = Integer.parseInt(idString);
            result.add(id);
        }

        return result;
    }

    public User USER() {
        String source = this.preferences.getString("GAME_USER_JSON", null);
        return new User(source);
    }

    public Iterable<Alarm> ALARM_SETTING() {
        String jsonString = this.preferences.getString("SP_ALARM_SETTING", null);

        if (jsonString != null) {
            jsonString = jsonString
                    .replace("\"{", "{")
                    .replace("}\"", "}")
                    .replace("\\\"", "\"");

            try {
                JSONArray alarmArray = new JSONArray(jsonString);
                List<Alarm> result = new ArrayList<Alarm>();
                for (int i = 0; i < alarmArray.length(); i++) {
                    JSONObject alarm = (JSONObject) alarmArray.get(i);
                    result.add(new Alarm(alarm));
                }

                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public String CURRENT_FLOOR() {
        return this.preferences.getString("MH_CACHE_RUNTIME_DATA_CURRENT_FLOOR", null);
    }

    public Iterable<FloorWave> CURRENT_FLOOR_WAVES() {
        String jsonString = this.preferences.getString("MH_CACHE_RUNTIME_DATA_CURRENT_FLOOR_WAVES", null)
            .substring(SIGNATURE_LENGTH);

        try {

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

    public class User {
        public String uid;
        public String uniqueKey;
        public int level;
        public int diamond;
        public int coin;
        public String session;

        public User(String source) {

            for (String pair : source.split(";")) {
                String[] blocks = pair.split("=");
                String key = blocks[0];
                String value = blocks[1];

                if (key.equals("uid")) {
                    uid = value;
                } else if (key.equals("uniqueKey")) {
                    uniqueKey = value;
                } else if (key.equals("level")) {
                    level = Integer.parseInt(value);
                } else if (key.equals("diamond")) {
                    diamond = Integer.parseInt(value);
                } else if (key.equals("coin")) {
                    coin = Integer.parseInt(value);
                } else if (key.equals("session")) {
                    session = value;
                }
            }
        }
    }

    public class Alarm {
        private final JSONObject json;

        Alarm(JSONObject json) {
            this.json = json;
        }

        public int J_NOTIFICATION_ID() throws JSONException {
            return json.getInt("J_NOTIFICATION_ID");
        }

        public Time J_C_TIMESTAMP() throws JSONException {
            Time time = new Time();
            time.set(json.getLong("J_C_TIMESTAMP"));

            return time;
        }

        public String J_MESSAGE() throws JSONException {
            return json.getString("J_MESSAGE");
        }
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
