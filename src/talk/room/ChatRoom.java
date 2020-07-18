package a.b.c.d.talk.room;

import a.b.c.d.packet.response.ChatInfoRes;
import a.b.c.d.packet.response.ChatOnRoomRes;
import a.b.c.d.packet.response.InfoLinkRes;
import a.b.c.d.talk.user.Member;

import java.util.Map;

public class ChatRoom {

    private String name = "";
    private long id = 0;
    private String roomType = "";

    public String getLink() {
        return link;
    }

    private String link = "";

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return roomType;
    }

    public int getMemberCount() {
        return memCount;
    }

    public Map<Long, Member> getMembers() {
        return members;
    }

    public long getLi() {
        return li;
    }

    private int memCount = 0;
    private Map<Long, Member> members = null;
    private long li = 0;

    public void fromChatInfo(ChatInfoRes res) {
        this.roomType = res.roomType;
        if (!res.roomType.equals("OM") && !res.roomType.equals("OD")) {
            this.name = res.displayMembers;
        } else {
            li = res.li;
        }
    }

    public void fromChatOnRoom(ChatOnRoomRes res) {
        this.members = res.members;
        this.memCount = res.members.size();
    }

    public void fromLinkInfo(InfoLinkRes res) {
        this.name = res.name;
        this.link = res.link;
    }

}
