package a.b.c.d.packet.handler;

import a.b.c.d.packet.response.ResponsePacket;

public interface PacketHandler {

    void onPacket(ResponsePacket res);

}
