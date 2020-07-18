package a.b.c.d.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.BSON;
import org.bson.json.Converter;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;

public class BSONUtil {

    private static DBDecoder decoder = DefaultDBDecoder.FACTORY.create();
    private static Gson gson = new Gson();
    private static JsonParser jsonParser = new JsonParser();

    public static byte[] toBson(JsonObject jsonObject) {
        return BSON.encode((DBObject) JSON.parse(gson.toJson(jsonObject)));
    }

    public static JsonObject toJsonObject(byte[] data) {
        JsonWriterSettings.Builder settings = JsonWriterSettings.builder();
        settings.int64Converter(new Converter() {
            @Override
            public void convert(Object value, StrictJsonWriter writer) {
                writer.writeNumber(value.toString());
            }
        });
        return jsonParser.parse(((BasicDBObject) decoder.decode(data, (DBCollection) null)).toJson(settings.build())).getAsJsonObject();
    }

}
