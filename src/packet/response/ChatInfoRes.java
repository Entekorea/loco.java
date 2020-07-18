package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ChatInfoRes extends ResponsePacket {

    public String roomType = "";
    public String displayMembers = "";
    public long li = 0;

    @Override
    public String getMethod() {
        return "CHATINFO";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonObject jsonObject = BSONUtil.toJsonObject(res).get("chatInfo").getAsJsonObject();
        roomType = jsonObject.get("type").getAsString();
        JsonArray dMem = jsonObject.get("displayMembers").getAsJsonArray();
        for (int i = 0; i < dMem.size(); i++) {
            displayMembers += "," + dMem.get(i).getAsJsonObject().get("nickName").getAsString();
        }
        displayMembers = displayMembers.replaceFirst(",", "");
        if (roomType.equals("OM") || roomType.equals("OD")) {
            li = jsonObject.get("li").getAsLong();
        }
    }
}
