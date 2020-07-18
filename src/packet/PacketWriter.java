package a.b.c.d.packet;

import a.b.c.d.packet.request.RequsetPacket;
import a.b.c.d.util.ByteUtil;

public class PacketWriter {

    private int id = 0;

    public int getNextId() {
        return ++id;
    }

    public byte[] toLocoPacket(int id, RequsetPacket packet) {
        byte[] bsonData = packet.toBosn();
        byte[] result = new byte[22 + bsonData.length];
        System.arraycopy(ByteUtil.intToByteArrayLE(id), 0, result, 0, 4);
        System.arraycopy(packet.getMethod().getBytes(), 0, result, 6, packet.getMethod().getBytes().length);
        System.arraycopy(ByteUtil.intToByteArrayLE(bsonData.length), 0, result, 18, 4);
        System.arraycopy(bsonData, 0, result, 22, bsonData.length);

        return result;
    }

}
