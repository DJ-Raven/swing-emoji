package raven.emoji;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class EmojiExtractor {

    public List<EmojiInfo> extractEmojis(String input) {
        List<EmojiInfo> emojis = new ArrayList<>();

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);

        int index = 0;
        while (index < normalized.length()) {
            int codePoint = normalized.codePointAt(index);
            int codePointCount = Character.charCount(codePoint);
            String codePointStr = new String(Character.toChars(codePoint));

            if (isEmoji(codePointStr)) {
                EmojiInfo emoji = new EmojiInfo(codePointStr, index, codePointCount, codePoint);
                emojis.add(emoji);
            }
            index += codePointCount;
        }
        return emojis;
    }

    public boolean isEmoji(String str) {
        return str.codePoints().anyMatch(codePoint -> Character.getType(codePoint) == Character.OTHER_SYMBOL);
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