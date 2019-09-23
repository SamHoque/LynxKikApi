package org.kik.bot.core;

import net.lynx.client.objects.KikUUIDGen;
import org.apache.log4j.Logger;

import java.io.IOException;

import static org.kik.bot.core.constants.IqType.XMLNS_MEMBER_DETAIL_REQUEST;
import static org.kik.bot.core.constants.IqType.XMLNS_ROSTER_REQUEST;

public class KikClient extends net.lynx.client.KikClient {
    private static final Logger LOGGER = Logger.getLogger(KikClient.class);

    public KikClient() throws IOException {
        super();
    }

    public void sendMemberDetailRequest(String memberJid) {
        try {
            String memberDetailsRequest = String.format(
                    "<iq type=\"get\" id=\"%s\"><query p=\"8\" xmlns=\"%s\"><item jid=\"%s\" /></query></iq>",
                    KikUUIDGen.getKikUUID(), XMLNS_MEMBER_DETAIL_REQUEST, memberJid
            );
            write_to_kik_server(memberDetailsRequest);
        } catch (IOException e) {
            LOGGER.error("[System] Error requesting membership details for member jid: " + memberJid, e);
        }
    }

    public void sendFullChatRosterRequest() {
        try {
            String fullChatRosterRequest = String.format(
                    "<iq type=\"get\" id=\"%s\"><query p=\"8\" xmlns=\"%s\" /></iq>",
                    KikUUIDGen.getKikUUID(), XMLNS_ROSTER_REQUEST
            );
            write_to_kik_server(fullChatRosterRequest);
        } catch (IOException e) {
            LOGGER.error("[System] Error full Chat Roster.", e);
        }
    }
}