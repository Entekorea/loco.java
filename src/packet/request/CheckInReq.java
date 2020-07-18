package a.b.c.d.packet.request;

import a.b.c.d.BasicApi;
import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonObject;

public class CheckInReq extends RequsetPacket {

    Long userId;

    public CheckInReq(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getMethod() {
        return "CHECKIN";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("os", BasicApi.AGENT);
        jsonObject.addProperty("ntype", 0);
        jsonObject.addProperty("appVer", BasicApi.INTERNAL_APP_VERSION);
        jsonObject.addProperty("MCCMNC", "");
        jsonObject.addProperty("lang", BasicApi.LANGUAGE);
        jsonObject.addProperty("useSub", true);

        return BSONUtil.toBson(jsonObject);
    }

}
