package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NewMemberRes extends ResponsePacket {

    public String name = "";
    public long chatId = 0;

    @Override
    public String getMethod() {
        return "NEWMEM";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = BSONUtil.toJsonObject(res).get("chatLog").getAsJsonObject();
        chatId = jsonObject.get("chatId").getAsLong();
        name = jsonParser.parse(jsonObject.get("message").getAsString()).getAsJsonObject().get("members").getAsJsonArray().get(0).getAsJsonObject().get("nickName").getAsString();
    }
}
