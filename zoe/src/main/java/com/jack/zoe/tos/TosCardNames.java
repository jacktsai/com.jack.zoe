package com.jack.zoe.tos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TosCardNames {
    private final List<String> cardNames = new ArrayList<String>();

    public TosCardNames(InputStream source) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(source);
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
