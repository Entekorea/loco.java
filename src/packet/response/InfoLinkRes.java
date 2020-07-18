package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class InfoLinkRes extends ResponsePacket {

    public String name = "";
    public String link = "";

    @Override
    public String getMethod() {
        return "INFOLINK";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonObject jsonObject = BSONUtil.toJsonObject(res).get("ols").getAsJsonArray().get(0).getAsJsonObject();
        name = jsonObject.get("ln").getAsString();
        link = jsonObject.get("lu").getAsString();
    }

}
