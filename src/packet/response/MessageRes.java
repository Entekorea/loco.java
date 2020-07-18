package a.b.c.d.packet.response;

import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class MessageRes extends ResponsePacket {

    public long chatId = -1;
    public String sender = "";
    public int mType = 0;
    public long senderId = 0;
    public String message = "";
    public String attachment = "{}";
    public long msgId = 0;

    @Override
    public String getMethod() {
        return "MSG";
    }

    @Override
    public void fromBson(byte[] res) {
        JsonObject jsonObject = BSONUtil.toJsonObject(res);
        chatId = jsonObject.get("chatId").getAsLong();
        sender = jsonObject.get("authorNickname").getAsString();
        JsonObject log = jsonObject.get("chatLog").getAsJsonObject();
        mType = log.get("type").getAsInt();
        senderId = log.get("authorId").getAsLong();
        message = log.get("message").getAsString();
        msgId = log.get("msgId").getAsLong();
        try {
            attachment = log.get("attachment").getAsString();
        } catch(Exception e) {
        }
    }
}
