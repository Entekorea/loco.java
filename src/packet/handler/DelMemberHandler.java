package a.b.c.d.packet.handler;

import a.b.c.d.TalkClient;
import a.b.c.d.packet.request.ChatInfoReq;
import a.b.c.d.packet.request.ChatOnRoomReq;
import a.b.c.d.packet.request.InfoLinkReq;
import a.b.c.d.packet.response.*;
import a.b.c.d.talk.room.ChatRoom;

public class DelMemberHandler implements PacketHandler {

    TalkClient client;

    public DelMemberHandler(TalkClient client) {
        this.client = client;
    }

    @Override
    public void onPacket(ResponsePacket res) {
        DelMemberRes cRes = (DelMemberRes) res;
        if (!client.rooms.containsKey(cRes.chatId)) {
            ChatRoom room = new ChatRoom();
            client.rooms.put(cRes.chatId, room);
            room.fromChatInfo((ChatInfoRes) client.sendPacket(new ChatInfoReq(cRes.chatId)).getResponse());
            room.fromChatOnRoom((ChatOnRoomRes) client.sendPacket(new ChatOnRoomReq(cRes.chatId)).getResponse());
            if (room.getType().equals("OM") || room.getType().equals("OD")) {
                room.fromLinkInfo((InfoLinkRes) client.sendPacket(new InfoLinkReq(room.getLi())).getResponse());
            }
        } else {
            client.rooms.get(cRes.chatId).fromChatOnRoom((ChatOnRoomRes) client.sendPacket(new ChatOnRoomReq(cRes.chatId)).getResponse());
        }

        if (client.rooms.get(cRes.chatId).getType().equals("OM") || client.rooms.get(cRes.chatId).getType().equals("OD")) {
            String[] par = new String[4];
            par[0] = client.rooms.get(cRes.chatId).getName();
            par[1] = cRes.name;
            par[2] = client.rooms.get(cRes.chatId).getLink();
            par[3] = (client.rooms.get(cRes.chatId).getMemberCount() + 1) + "";
            client.listeners.get("DelMem").onMessage(par);
        }
    }
}
