package a.b.c.d.packet;

import a.b.c.d.network.LocoPacketHeader;
import a.b.c.d.packet.response.*;
import a.b.c.d.util.ByteUtil;

import java.util.HashMap;
import java.util.Map;

public class PacketReader {

    private Map<String, Class<? extends ResponsePacket>> packets = new HashMap<String, Class<? extends ResponsePacket>>();

    public PacketReader() {
        packets.put("GETCONF", GetConfRes.class);
        packets.put("CHECKIN", CheckInRes.class);
        packets.put("LOGINLIST", LoginRes.class);
        packets.put("MSG", MessageRes.class);
        packets.put("CHATINFO", ChatInfoRes.class);
        packets.put("CHATONROOM", ChatOnRoomRes.class);
        packets.put("INFOLINK", InfoLinkRes.class);
        packets.put("NEWMEM", NewMemberRes.class);
        packets.put("DELMEM", DelMemberRes.class);
    }

    public LocoPacketHeader readHeader(byte[] data) {
        byte[] idBytes = new byte[4];
        System.arraycopy(data, 0, idBytes, 0, 4);
        int id = ByteUtil.byteArrayToIntLE(idBytes);

        byte[] methodBytes = new byte[11];
        System.arraycopy(data, 6, methodBytes, 0, 11);
        String method = new String(methodBytes).replaceAll("\0", "");

        byte[] lenBytes = new byte[4];
        System.arraycopy(data, 18, lenBytes, 0, 4);
        int len = ByteUtil.byteArrayToIntLE(lenBytes);

        return new LocoPacketHeader(id, method, len);
    }

    public ResponsePacket readBody(LocoPacketHeader header, byte[] body) {
        try {
            if (packets.containsKey(header.getMethod())) {
                byte[] rBody = new byte[body.length - 22];
                System.arraycopy(body, 22, rBody, 0, body.length - 22);

                ResponsePacket packet = packets.get(header.getMethod()).newInstance();
                packet.fromBson(rBody);

                return packet;
            } else {
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
