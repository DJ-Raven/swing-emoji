package raven.emoji;

import java.util.ArrayList;
import java.util.List;

public class EmojiExtractor {

    /**
     * Find all supported emoji in the input. The index and length are in chars of the input string,
     * a trailing variation selector (U+FE0F) is counted as part of the emoji
     */
    public List<EmojiInfo> extractEmojis(String input) {
        List<EmojiInfo> emojis = new ArrayList<>();
        int index = 0;
        while (index < input.length()) {
            int codePoint = input.codePointAt(index);
            int length = Character.charCount(codePoint);
            String codePointStr = new String(Character.toChars(codePoint));
            if (isEmoji(codePointStr)) {
                if (index + length < input.length() && input.charAt(index + length) == '\uFE0F') {
                    length++;
                }
                emojis.add(new EmojiInfo(codePointStr, index, length, codePoint));
            }
            index += length;
        }
        return emojis;
    }

    public boolean isEmoji(String str) {
        return EmojiIcon.getInstance().isEmoji(str);
    }

    public class EmojiInfo {

        public String getEmoji() {
            return emoji;
        }

        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getUnicodeCodePoints() {
            return unicodeCodePoints;
        }

        public void setUnicodeCodePoints(int unicodeCodePoints) {
            this.unicodeCodePoints = unicodeCodePoints;
        }

        public EmojiInfo(String emoji, int startIndex, int length, int unicodeCodePoints) {
            this.emoji = emoji;
            this.startIndex = startIndex;
            this.length = length;
            this.unicodeCodePoints = unicodeCodePoints;
        }

        private String emoji;
        private int startIndex;
        private int length;
        private int unicodeCodePoints;
    }
}