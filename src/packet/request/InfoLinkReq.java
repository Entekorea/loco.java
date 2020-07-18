package a.b.c.d.packet.request;

import a.b.c.d.BasicApi;
import a.b.c.d.util.BSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class InfoLinkReq extends RequsetPacket {

    Long li;

    public InfoLinkReq(Long li) {
        this.li = li;
    }

    @Override
    public String getMethod() {
        return "INFOLINK";
    }

    @Override
    public byte[] toBosn() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(li);
        jsonObject.add("lis", jsonArray);

        return BSONUtil.toBson(jsonObject);
    }

}
