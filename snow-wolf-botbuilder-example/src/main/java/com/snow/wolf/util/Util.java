package com.snow.wolf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static List<String> getTransIdFromText(String text) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(text);
        while (m.find()) {
            if (m.group().startsWith("1") && m.group().length() > 8 && m.group().length() < 15)
                list.add(m.group());
        }
        return list;
    }
}
