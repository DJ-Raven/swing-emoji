package raven.emoji;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import javax.swing.text.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiIcon {

    private static EmojiIcon instance;
    private Map<String, String> emojiMap;

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
            String svg = obj.getString("group") + "/flat/" + obj.getString("file");
            emojiMap.put(obj.getString("emoji"), svg);
        }
    }

    public Icon getEmojiIcon(String key) {
        if (emojiMap == null) {
            return null;
        }
        String svg = emojiMap.get(key);
        if (svg == null) {
            return null;
        }
        return new FlatSVGIcon("raven/emoji/" + svg, 0.5f);
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
}
