package org.kik.bot.core.model;

import net.lynx.client.objects.Node;
import org.apache.log4j.Logger;

import java.util.Optional;

public class PersonChat extends Chat {
    private String username;
    private String displayName;

    public PersonChat(Node item) {
        super(item);
        Optional.ofNullable(item.getFirstChildByName("username"))
                .ifPresent(node -> username = node.getText());
        Optional.ofNullable(item.getFirstChildByName("display-name"))
                .ifPresent(node -> displayName = node.getText());
    }

    public PersonChat(String jid) {
        super(jid);
        username = "[unknown]";
        displayName = "[jid: " + jid + "]";

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDefaultDisplayName() {
        return getDisplayName();
    }

    @Override
    public String toString() {
        return "ChatPerson{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
