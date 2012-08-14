package be.guntherdw.bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class Player {

    private List<Group> groups;
    private String username;
    private String nick;
    private String prefix = null;
    private String suffix = null;
    private boolean parsed = false;

    public Player(String username) {
        this.username = username;
    }

    public void addGroup(Group group) {
        if (this.groups == null)
            this.groups = new ArrayList<Group>();

        if (!this.groups.contains(group))
            this.groups.add(group);
    }

    public boolean hasGroup() {
        return this.groups != null && this.groups.size() > 0;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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

    public boolean hasPrefix() {
        return this.prefix != null;
    }

    public boolean hasSuffix() {
        return this.suffix != null;
    }

    public boolean hasNick() {
        return nick != null;
    }

    public void setSuffix(String suffix) {
        if (suffix.trim().equals("")) this.suffix = null;
        else this.suffix = suffix;
    }

    public String prettify() {
        StringBuilder s = new StringBuilder();
        if (hasPrefix())
            s.append(getPrefix());
        if (hasNick())
            s.append(getNick());
        else
            s.append(getUsername());
        if (hasSuffix())
            s.append(getSuffix());

        return s.toString();
    }

    public boolean isParsed() {
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    @Override
    public String toString() {
        return "Player{" +
            "username='" + username + '\'' +
            ", nick='" + nick + '\'' +
            ", prefix='" + prefix + '\'' +
            ", suffix='" + suffix + '\'' +
            ", groups='" + groups + '\'' +
            '}';
    }
}
