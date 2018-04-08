package info.androidhive.materialdesign.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by HP ENVY on 5/25/2017.
 */

public class MyFunctions {
    public static JSONArray getJSONArray(JSONObject jsonArray, String index){
        try {
            return jsonArray.getJSONArray(index);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String index){
        try {
            return jsonObject.getJSONObject(index);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static String getJSONString(JSONObject jsonObject, String index){
        try {
            return jsonObject.getString(index);
        } catch (JSONException e) {
            return "";
        }
    }

    public static int getJSONInt(JSONObject jsonObject, String index){
        try {
            return jsonObject.getInt(index);
        } catch (JSONException e) {
            return 0;
        }
    }




}
