package a.b.c.d.packet.request;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class ChatOnRoomReq extends RequsetPacket {

    Long chatId;

    public ChatOnRoomReq(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String getMethod() {
        return "CHATONROOM";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chatId", chatId);
        jsonObject.addProperty("token", 0);

        return BSONUtil.toBson(jsonObject);
    }

}
