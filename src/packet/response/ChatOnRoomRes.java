package a.b.c.d.packet.response;

import a.b.c.d.talk.user.Member;
import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ChatOnRoomRes extends ResponsePacket {

    public HashMap<Long, Member> members = new HashMap<Long, Member>();

    @Override
    public String getMethod() {
        return "CHATONROOM";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonArray jsonArray = BSONUtil.toJsonObject(res).get("m").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject cI = jsonArray.get(i).getAsJsonObject();
            try {
                members.put(cI.get("userId").getAsLong(), new Member(cI.get("userId").getAsLong(), cI.get("nickName").getAsString(), cI.get("profileImageUrl").getAsString(), cI.get("fullProfileImageUrl").getAsString(), cI.get("originalProfileImageUrl").getAsString()));
            } catch(NullPointerException e) {
                members.put(cI.get("userId").getAsLong(), new Member(cI.get("userId").getAsLong(), cI.get("nickName").getAsString(), cI.get("pi").getAsString(), cI.get("fpi").getAsString(), cI.get("opi").getAsString()));
            }
        }
    }
}
