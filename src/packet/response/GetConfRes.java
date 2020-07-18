package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class GetConfRes extends ResponsePacket {

    public String host = "";
    public int port = 0;

    @Override
    public String getMethod() {
        return "GETCONF";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonObject jsonObject = BSONUtil.toJsonObject(res);
        host = jsonObject.get("ticket").getAsJsonObject().get("lsl").getAsJsonArray().get(0).getAsString();
        port = jsonObject.get("wifi").getAsJsonObject().get("ports").getAsJsonArray().get(0).getAsInt();
    }
}
