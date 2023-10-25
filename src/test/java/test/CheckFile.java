package test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CheckFile {

    public CheckFile() {
        JSONTokener jsonTokener = new JSONTokener(getClass().getResourceAsStream("/raven/emoji/metadata.json"));
        JSONArray json = new JSONArray(jsonTokener);
        JSONArray arr = new JSONArray();
        int a = 0;
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            if (obj.getString("emoji").length() <= 2) {
                a++;
                arr.put(obj);
                System.out.println(obj.getString("emoji"));
                obj.put("emoji", obj.getString("emoji").replace("️", ""));
            }
        }
       System.out.println(a);
    }

    public static void main(String[] args) {
        new CheckFile();
    }
}
