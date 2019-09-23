package org.kik.bot.core.behaviour;

import org.kik.bot.core.model.GroupChatMessage;
import org.kik.bot.core.model.PersonalChatMessage;

public interface BotBehaviour {
    void onPersonalChatMessage(PersonalChatMessage personalChatMessage);

    void onGroupChatMessage(GroupChatMessage groupChatMessage);
}
