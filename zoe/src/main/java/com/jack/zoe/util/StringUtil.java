package com.jack.zoe.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtil {

    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> inter = s.iterator();

        while (inter.hasNext()) {
            builder.append(inter.next());

            if (!inter.hasNext()) {
                break;
            }

            builder.append(delimiter);
        }

        return builder.toString();
    }
}
