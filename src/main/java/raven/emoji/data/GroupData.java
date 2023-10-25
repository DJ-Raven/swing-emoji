package raven.emoji.data;

public class GroupData {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GroupData(String name, String key) {
        this.name = name;
        this.key = key;
    }

    private String name;
    private String key;
}
