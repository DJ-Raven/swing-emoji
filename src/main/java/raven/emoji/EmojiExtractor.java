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
        return str.codePoints().anyMatch(codePoint -> {
            int type = Character.getType(codePoint);
            return type == Character.OTHER_SYMBOL ||
                    type == Character.OTHER_PUNCTUATION ||
                    type == Character.MATH_SYMBOL ||
                    (codePoint >= 0x1F600 && codePoint <= 0x1F64F) || // Common Emoticons
                    (codePoint >= 0x1F300 && codePoint <= 0x1F5FF) || // Miscellaneous Symbols and Pictographs
                    (codePoint >= 0x1F680 && codePoint <= 0x1F6FF) || // Transport and Map Symbols
                    (codePoint >= 0x1F700 && codePoint <= 0x1F77F) || // Alchemical Symbols
                    (codePoint >= 0x1F780 && codePoint <= 0x1F7FF) || // Geometric Shapes Extended
                    (codePoint >= 0x1F800 && codePoint <= 0x1F8FF) || // Supplemental Arrows-C
                    (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) || // Supplemental Symbols and Pictographs
                    (codePoint >= 0x1FA00 && codePoint <= 0x1FA6F) || // Chess Symbols
                    (codePoint >= 0x1FA70 && codePoint <= 0x1FAFF) || // Symbols and Pictographs Extended-A
                    (codePoint >= 0x1F004 && codePoint <= 0x1F0CF) || // Miscellaneous Symbols and Arrows
                    (codePoint >= 0x1F200 && codePoint <= 0x1F251) || // Enclosed Ideographic Supplement
                    isExcludedCharacter(type);
        });
    }

    private boolean isExcludedCharacter(int codePoint) {
        // Define the code points of characters you want to exclude
        return false;
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