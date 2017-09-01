package com.getbooks.android.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by marinaracu on 01.09.17.
 */

public class AppUtil {

    public static Map<String, String> stringToJsonMap(String string) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            JSONObject jObject = jsonArray.getJSONObject(0);
            Iterator<?> keys = jObject.keys();

            keys.hasNext();
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
