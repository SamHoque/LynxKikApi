package org.kik.bot.core.constants;

/**
 * Constants for interacting with kik server
 * Contains expected tag/node name and attribute names from xml response
 *
 * Mainly constants for handling details of Group memberships and Chat interactions
 */
public class IqType {
    public static final String IQ = "iq"; // main tag/node of roster/group chat member request/response

    public static final String QUERY = "query"; // tag/node name that contains xmlns

    public static final String XMLNS = "xmlns"; // attribute name that determines the type of request/response for all chat interactions or details of a group chat member
    public static final String XMLNS_ROSTER_REQUEST = "jabber:iq:roster"; // xmlns value for all chat interaction request/response
    public static final String XMLNS_MEMBER_DETAIL_REQUEST = "kik:iq:friend:batch"; // xmlns value for a group member request/response
}
