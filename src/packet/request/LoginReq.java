package a.b.c.d.packet.request;

import a.b.c.d.BasicApi;
import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LoginReq extends RequsetPacket {

    String deviceUUID;
    String token;

    public LoginReq(String deviceUUID, String token) {
        this.deviceUUID = deviceUUID;
        this.token = token;
    }

    @Override
    public String getMethod() {
        return "LOGINLIST";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("appVer", BasicApi.INTERNAL_APP_VERSION);
        jsonObject.addProperty("prtVer", "1");
        jsonObject.addProperty("os", BasicApi.AGENT);
        jsonObject.addProperty("lang", BasicApi.LANGUAGE);
        jsonObject.addProperty("duuid", deviceUUID);
        jsonObject.addProperty("oauthToken", token);
        jsonObject.addProperty("dtype", 1);
        jsonObject.addProperty("ntype", 0);
        jsonObject.addProperty("MCCMNC", "");
        jsonObject.addProperty("revision", 0);
        jsonObject.add("chatIds", new JsonArray());
        jsonObject.add("maxIds", new JsonArray());
        jsonObject.addProperty("lastTokenId", 0);
        jsonObject.addProperty("lbk", 0);
        jsonObject.addProperty("bg", false);

        return BSONUtil.toBson(jsonObject);
    }

}
