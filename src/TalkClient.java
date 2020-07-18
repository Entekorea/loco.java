package a.b.c.d;

import a.b.c.d.network.LocoSocket;
import a.b.c.d.packet.handler.DelMemberHandler;
import a.b.c.d.packet.handler.MessageHandler;
import a.b.c.d.packet.handler.NewMemberHandler;
import a.b.c.d.packet.handler.PacketHandler;
import a.b.c.d.packet.request.LoginReq;
import a.b.c.d.packet.request.RequsetPacket;
import a.b.c.d.packet.response.CheckInRes;
import a.b.c.d.packet.response.GetConfRes;
import a.b.c.d.packet.response.LoginRes;
import a.b.c.d.packet.response.ResponsePacket;
import a.b.c.d.talk.PacketListener;
import a.b.c.d.talk.room.ChatRoom;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TalkClient {

    private String name;

    Map<String, PacketHandler> handlers = new HashMap<String, PacketHandler>();

    public TalkClient(String name) {
        this.name = name;
        handlers.put("MSG", new MessageHandler(this));
        handlers.put("NEWMEM", new NewMemberHandler(this));
        handlers.put("DELMEM", new DelMemberHandler(this));
    }

    LocoSocket socket;
    boolean login;

    CompletableFuture<ResponsePacket> loginFuture;

    public Map<String, PacketListener> listeners = new HashMap<String, PacketListener>();
    public Map<Long, ChatRoom> rooms = new HashMap<Long, ChatRoom>();

    public void login(String email, String password, String deviceUUID) throws Exception {
        if (login) throw new Exception("Already login");
        login = true;
        JsonParser jsonParser = new JsonParser();

        JsonObject loginData = jsonParser.parse(BasicApi.requestLogin(email, password, deviceUUID, this.name)).getAsJsonObject();

        int status = loginData.get("status").getAsInt();
        if (status != 0) {
            throw new Exception("Login failed with status code : " + status);
        }

        GetConfRes booking = BasicApi.getBookingData();
        CheckInRes checkin = BasicApi.getCheckinData(booking.host, booking.port, 0);

        socket = new LocoSocket(this, checkin.host, checkin.port);
        socket.connect();

        loginFuture = socket.sendPacket(new LoginReq(deviceUUID, loginData.get("access_token").getAsString())).getFuture();

        System.out.println("Login succeed");
    }

    public void onPacket(ResponsePacket res) {
        if (!handlers.containsKey(res.getMethod())) {
            throw new Error("Unknown PacketType : " + res.getMethod());
        }
        handlers.get(res.getMethod()).onPacket(res);
    }

    public RequsetPacket sendPacket(RequsetPacket req) {
        return socket.sendPacket(req);
    }

    public void stop() throws Exception {
        if(!login) throw new Exception("Not logged in");
        loginFuture.cancel(true);
        while(socket==null);
        socket.stop();
    }

}
