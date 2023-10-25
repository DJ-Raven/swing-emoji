package raven.emoji.data;

public class EmojiData {

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public EmojiData(String file, String emoji, String[] keywords, String unicode, String group) {
        this.file = file;
        this.emoji = emoji;
        this.keywords = keywords;
        this.unicode = unicode;
        this.group = group;
    }

    private String file;
    private String emoji;
    private String keywords[];
    private String unicode;
    private String group;
}
