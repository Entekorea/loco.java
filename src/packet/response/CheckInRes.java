package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class CheckInRes extends ResponsePacket {

    public String host = "";
    public int port = 0;

    @Override
    public String getMethod() {
        return "CHECKIN";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonObject jsonObject = BSONUtil.toJsonObject(res);
        host = jsonObject.get("host").getAsString();
        port = jsonObject.get("port").getAsInt();
    }
}
