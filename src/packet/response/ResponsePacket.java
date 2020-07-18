package a.b.c.d.packet.response;

import a.b.c.d.network.LocoPacketHeader;

import java.util.HashMap;
import java.util.Map;

public abstract class ResponsePacket {

    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public abstract String getMethod();
    public abstract void fromBson(byte[] res);

}
