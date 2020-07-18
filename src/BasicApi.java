package a.b.c.d;

import a.b.c.d.network.LocoPacketHeader;
import a.b.c.d.network.LocoSocket;
import a.b.c.d.packet.PacketReader;
import a.b.c.d.packet.PacketWriter;
import a.b.c.d.packet.request.CheckInReq;
import a.b.c.d.packet.request.GetConfReq;
import a.b.c.d.packet.response.CheckInRes;
import a.b.c.d.packet.response.GetConfRes;
import a.b.c.d.util.ByteUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class BasicApi {

    public final static String REQUEST_URL = "https://ac-sb-talk.kakao.com/win32/account/request_passcode.json";
    public final static String REGISTER_URL = "https://ac-sb-talk.kakao.com/win32/account/register_device.json";
    public final static String LOGIN_URL = "https://ac-sb-talk.kakao.com/win32/account/login.json";
    public final static String SETTING_URL = "https://sb-talk.kakao.com/win32/account/more_settings.json?since=0&lang=ko";

    public final static String AGENT = "win32";
    public final static String VERSION = "3.1.1";
    public final static String INTERNAL_APP_VERSION = VERSION + ".2441";
    public final static String OS_VERSION = "10.0";
    public final static String LANGUAGE = "ko";
    public final static String AUTH_USER_AGENT = String.format("KT/%s Wd/%s %s", VERSION, OS_VERSION, LANGUAGE);
    public final static String AUTH_HEADER_AGENT = String.format("%s/%s/%s", AGENT, VERSION, LANGUAGE);

    public final static String BOOKING_HOST = "booking-loco.kakao.com";
    public final static int BOOKING_PORT = 443;

    public static boolean registerDevice(String passcode, String email, String password, String deviceUUID, String deviceName) {
        try {
            JsonParser jsonParser = new JsonParser();

            String text = Jsoup.connect(REGISTER_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("A", AUTH_HEADER_AGENT)
                    .header("X-VC", calcXvc(email, deviceUUID))
                    .header("User-Agent", AUTH_USER_AGENT)
                    .header("Accept", "*/*")
                    .header("Accept-Language", LANGUAGE)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .data("email", email)
                    .data("password", password)
                    .data("device_name", deviceName)
                    .data("device_uuid", deviceUUID)
                    .data("os_version", OS_VERSION)
                    .data("permanent", "true")
                    .data("once", "false")
                    .data("passcode", passcode)
                    .post().text();

            JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
            int status = jsonObject.get("status").getAsInt();

            if (status == 0) {
                System.out.println("Register successful");
                return true;
            } else {
                System.out.println("Register failed. Status code : " + status);
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void requestPasscode(String email, String password, String deviceUUID, String deviceName) throws Exception {
        JsonParser jsonParser = new JsonParser();

        String text = Jsoup.connect(REQUEST_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("A", AUTH_HEADER_AGENT)
                .header("X-VC", calcXvc(email, deviceUUID))
                .header("User-Agent", AUTH_USER_AGENT)
                .header("Accept", "*/*")
                .header("Accept-Language", LANGUAGE)
                .ignoreContentType(true)
                .timeout(5000)
                .data("email", email)
                .data("password", password)
                .data("device_name", deviceName)
                .data("device_uuid", deviceUUID)
                .data("os_version", OS_VERSION)
                .data("permanent", "true")
                .data("once", "false")
                .post().text();

        JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
        int status = jsonObject.get("status").getAsInt();

        if (status == 0) {
            System.out.println("Passcode was sent");
        } else {
            System.out.println("Request failed. Status code : " + status);
            throw new Exception("[" + email + "] " + status);
        }
    }

    public static String requestLogin(String email, String password, String deviceUUID, String deviceName) {
        try {
            return Jsoup.connect(LOGIN_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("A", AUTH_HEADER_AGENT)
                    .header("X-VC", calcXvc(email, deviceUUID))
                    .header("User-Agent", AUTH_USER_AGENT)
                    .header("Accept", "*")
                    .header("Accept-Language", LANGUAGE)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .data("email", email)
                    .data("password", password)
                    .data("device_name", deviceName)
                    .data("device_uuid", deviceUUID)
                    .data("os_version", OS_VERSION)
                    .data("permanent", "true")
                    .post().text();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String requestAccountSettings(String accessToken, String deviceUUID) {
        try {
            return Jsoup.connect(SETTING_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", String.format("%s-%s", accessToken, deviceUUID))
                    .header("A", AUTH_HEADER_AGENT)
                    .header("User-Agent", AUTH_USER_AGENT)
                    .header("Accept", "*")
                    .header("Accept-Language", LANGUAGE)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .get().text();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GetConfRes getBookingData() {
        try {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(BOOKING_HOST, BOOKING_PORT);
            socket.startHandshake();

            OutputStream writer = socket.getOutputStream();
            InputStream reader = socket.getInputStream();

            PacketWriter packetWriter = new PacketWriter();
            PacketReader packetReader = new PacketReader();

            byte[] packet = packetWriter.toLocoPacket(packetWriter.getNextId(),new GetConfReq());

            writer.write(packet);
            writer.flush();

            byte[] res = new byte[2048];
            reader.read(res);
            LocoPacketHeader header = packetReader.readHeader(res);
            byte[] body = new byte[header.getLength() + 22];
            System.arraycopy(res, 0, body, 0, header.getLength() + 22);

            socket.close();

            return (GetConfRes) packetReader.readBody(header, body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CheckInRes getCheckinData(String host, int port, long userId) {
        LocoSocket socket = new LocoSocket(null, host, port);
        socket.connect();

        CheckInRes res = (CheckInRes) socket.sendPacket(new CheckInReq(userId)).getResponse();
        socket.stop();

        return res;
    }

    public static String calcXvc(String email, String deviceUUID) {
        try {
            String res = String.format("HEATH|%s|DEMIAN|%s|%s", AUTH_USER_AGENT, email, deviceUUID);

            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(res.getBytes());

            return ByteUtil.byteArrayToHexString(digest.digest()).substring(0, 16).toLowerCase();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
