package a.b.c.d.packet.request;

import a.b.c.d.BasicApi;
import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class GetConfReq extends RequsetPacket {

    @Override
    public String getMethod() {
        return "GETCONF";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MCCMNC", "");
        jsonObject.addProperty("os", BasicApi.AGENT);
        jsonObject.addProperty("model", "");

        return BSONUtil.toBson(jsonObject);
    }

}
