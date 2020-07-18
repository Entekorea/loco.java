package a.b.c.d.packet.request;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class ChatInfoReq extends RequsetPacket {

    Long chatId;

    public ChatInfoReq(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String getMethod() {
        return "CHATINFO";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chatId", chatId);

        return BSONUtil.toBson(jsonObject);
    }
}
