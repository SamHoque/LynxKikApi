package org.kik.bot.core.model;

import net.lynx.client.objects.Node;

import java.util.Optional;

public abstract class Chat {
    private String jid;

    public Chat(Node node) {
        Optional.ofNullable(node.getAttribute("jid"))
                .ifPresent(value -> jid = value);
    }

    public Chat(String jid) {
        this.jid = jid;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public abstract String getDefaultDisplayName();
}
