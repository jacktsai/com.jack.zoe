package com.jack.zoe;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jack.zoe.tos.TosFile;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TosLootsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tos_loots);

        try {
            List<Map<String, String>> loots = this.getLoots();

            ListAdapter adapter = new SimpleAdapter(
                    this,
                    loots,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "desc"},
                    new int[] {android.R.id.text1, android.R.id.text2});

            this.setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, String>> getLoots() throws JSONException {
        List<Map<String, String>> loots = new ArrayList<Map<String, String>>();
        TosFile tosFile = TosFile.snapshot(this);

        String floor = tosFile.CURRENT_FLOOR();
        if (floor != null && floor.length() > 0) {
            Iterable<TosFile.FloorWave> floorWaves = tosFile.CURRENT_FLOOR_WAVES();
            List<Integer> collectedIds = tosFile.USER_COLLECTED_MONSTER_IDS();
            int waveIndex = 1;
            for (TosFile.FloorWave wave : floorWaves) {
                for (TosFile.FloorEnemy enemy : wave.enemies()) {
                    TosFile.LootItem lootItem = enemy.lootItem();
                    if (lootItem != null) {
                        String title = "", desc = "";
                        String lootType = lootItem.type();
                        if (lootType.equals("money") || lootType.equals("monster")) {
                            if (lootType.equals("money")) {
                                title =String.format("金幣 %d", lootItem.amount());
                            } else if (lootType.equals("monster")) {
                                TosFile.Card card = lootItem.card();
                                title = String.format("WAVE#%d %s", waveIndex, card.name());
                                if (collectedIds != null) {
                                    if (!collectedIds.contains(card.id())) {
                                        title = title + "*NEW*";
                                    }
                                }

                                desc = String.format("[%s]%d星%s", card.number(), card.rarity(), card.race());
                            }
                        }

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("title", title);
                        data.put("desc", desc);
                        loots.add(data);
                    }
                }

                waveIndex++;
            }
        }

        return loots;
    }
}
