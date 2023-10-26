package raven.emoji;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import raven.emoji.data.EmojiData;
import raven.emoji.data.GroupData;

import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.util.stream.Collectors;

public class EmojiIcon {

    private static EmojiIcon instance;

    private Map<String, EmojiData> emojiMap;

    public static EmojiIcon getInstance() {
        if (instance == null) {
            instance = new EmojiIcon();
        }
        return instance;
    }

    public void installEmojiSvg() {
        if (emojiMap != null) {
            emojiMap.clear();
        }
        emojiMap = new HashMap<>();
        JSONTokener jsonTokener = new JSONTokener(getClass().getResourceAsStream("/raven/emoji/metadata.json"));
        JSONArray json = new JSONArray(jsonTokener);
        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            EmojiData data = new EmojiData(obj.getString("file"), obj.getString("emoji"), toArray(obj.getJSONArray("keywords")), obj.getString("unicode"), obj.getString("group"));
            emojiMap.put(obj.getString("emoji"), data);
        }
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
        if (emojiMap == null) {
            return null;
        }

        EmojiData data = emojiMap.get(key);
        if (data == null) {
            return null;
        }
        String svg = data.getGroup() + "/flat/" + data.getFile();
        if (svg == null) {
            return null;
        }
        return new FlatSVGIcon("raven/emoji/" + svg, scale);
    }

    public void installTextPane(JTextPane textPane) {
        StyledDocument styledDocument = new DefaultStyledDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offs, str, a);
                EmojiExtractor emojiExtractor = new EmojiExtractor();
                List<EmojiExtractor.EmojiInfo> list = emojiExtractor.extractEmojis(str);
                for (EmojiExtractor.EmojiInfo e : list) {
                    Icon icon = EmojiIcon.getInstance().getEmojiIcon(e.getEmoji());
                    if (icon != null) {
                        Style style = super.addStyle("emoji", null);
                        StyleConstants.setIcon(style, icon);
                        try {
                            super.remove(offs + e.getStartIndex(), e.getLength());
                            super.insertString(offs + e.getStartIndex(), e.getEmoji(), style);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
        textPane.setDocument(styledDocument);
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
        return emojiMap.values().stream()
                .filter(data -> group.equals(data.getGroup()))
                .collect(Collectors.toList());
    }

    public List<EmojiData> filterByKeywords(String keyword) {
        return emojiMap.values().stream()
                .filter(data -> containsKeyword(data, keyword))
                .collect(Collectors.toList());
    }

    private boolean containsKeyword(EmojiData data, String targetKeyword) {
        for (String keyword : data.getKeywords()) {
            if (targetKeyword.equals(keyword)) {
                return true;
            }
        }
        return false;
    }
}
