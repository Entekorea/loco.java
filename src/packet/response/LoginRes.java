package a.b.c.d.packet.response;

public class LoginRes extends ResponsePacket {

    @Override
    public String getMethod() {
        return "LOGINLIST";
    }

    @Override
    public void fromBson(byte[] res) {

    }

}
