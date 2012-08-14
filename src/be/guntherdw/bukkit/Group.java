package be.guntherdw.bukkit;

/**
 * @author GuntherDW
 */
public class Group {
    private String name;
    private String prefix = null;
    private String suffix = null;
    private Group inheritance = null;
    private boolean defaultGroup = false;


    public Group(String name) {
        this.name = name;
    }

    public Group(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix.trim().equals("")) this.prefix = null;
        else this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public boolean hasPrefix() {
        return this.prefix != null;
    }

    public boolean hasSuffix() {
        return this.suffix != null;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public void setSuffix(String suffix) {
        if (suffix.trim().equals("")) this.suffix = null;
        else this.suffix = suffix;
    }

    public void setInheritance(Group g) {
        this.inheritance = g;
    }

    @Override
    public String toString() {
        return "Group{name="+name+", prefix="+prefix+", suffix="+suffix+", inheritance="+inheritance+"}";
    }
}
