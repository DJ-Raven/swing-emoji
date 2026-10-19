package raven.emoji;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import raven.emoji.data.EmojiData;
import raven.emoji.data.GroupData;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class EmojiIcon {

    private static EmojiIcon instance;

    private Map<String, EmojiData> emojiMap;

    public static synchronized EmojiIcon getInstance() {
        if (instance == null) {
            instance = new EmojiIcon();
        }
        return instance;
    }

    public void installEmojiSvg() {
        Map<String, EmojiData> emojiMap = new LinkedHashMap<>();
        JSONTokener jsonTokener = new JSONTokener(getClass().getResourceAsStream("/raven/emoji/metadata.json"));
        JSONArray json = new JSONArray(jsonTokener);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            EmojiData data = new EmojiData(obj.getString("file"), obj.getString("emoji"), toArray(obj.getJSONArray("keywords")), obj.getString("unicode"), obj.getString("group"));
            emojiMap.put(obj.getString("emoji"), data);
        }
        this.emojiMap = emojiMap;
    }

    private Map<String, EmojiData> getEmojiMap() {
        if (emojiMap == null) {
            installEmojiSvg();
        }
        return emojiMap;
    }

    public EmojiData getEmojiData(String emoji) {
        return getEmojiMap().get(emoji);
    }

    public boolean isEmoji(String emoji) {
        return getEmojiMap().containsKey(emoji);
    }

    private String[] toArray(JSONArray array) {
        String arr[] = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.getString(i);
        }
        return arr;
    }

    public Icon getEmojiIcon(String key) {
        return getEmojiIcon(key, 0.5f);
    }

    public Icon getEmojiIcon(String key, float scale) {
        EmojiData data = getEmojiMap().get(key);
        if (data == null) {
            return null;
        }
        return new FlatSVGIcon("raven/emoji/" + data.getGroup() + "/flat/" + data.getFile(), scale);
    }

    /**
     * Install an {@link EmojiDocument} to the text pane, the current text is kept.
     * Call this after {@link JTextPane#setEditorKit}, because setting the editor kit replaces the document.
     */
    public void installTextPane(JTextPane textPane) {
        String text = textPane.getText();
        textPane.setDocument(new EmojiDocument());
        if (text != null && !text.isEmpty()) {
            textPane.setText(text);
        }
    }

    public List<GroupData> getGroups() {
        List<GroupData> list = new ArrayList<>();
        list.add(new GroupData("Smileys & Emotion", "\uD83D\uDE0A"));
        list.add(new GroupData("Activities", "\uD83C\uDFC6"));
        list.add(new GroupData("Animals & Nature", "\uD83D\uDC39"));
        list.add(new GroupData("Flags", "\uD83D\uDEA9"));
        list.add(new GroupData("Food & Drink", "\uD83C\uDF54"));
        list.add(new GroupData("Objects", "\uD83C\uDFF9"));
        list.add(new GroupData("People & Body", "\uD83E\uDDD2"));
        list.add(new GroupData("Symbols", "\uD83D\uDCB2"));
        list.add(new GroupData("Travel & Places", "\uD83D\uDE90"));
        return list;
    }

    public List<EmojiData> filterByGroup(String group) {
        return getEmojiMap().values().stream()
                .filter(data -> group.equals(data.getGroup()))
                .sorted(Comparator.comparing(EmojiData::getUnicode).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Search emoji by keyword, case-insensitive and partial match
     */
    public List<EmojiData> filterByKeywords(String keyword) {
        String search = keyword.trim().toLowerCase(Locale.ROOT);
        return getEmojiMap().values().stream()
                .filter(data -> containsKeyword(data, search))
                .sorted(Comparator.comparing(EmojiData::getUnicode).reversed())
                .collect(Collectors.toList());
    }

    private boolean containsKeyword(EmojiData data, String targetKeyword) {
        for (String keyword : data.getKeywords()) {
            if (keyword.toLowerCase(Locale.ROOT).contains(targetKeyword)) {
                return true;
            }
        }
        return false;
    }
}
