package a.b.c.d.network;

public class LocoPacketHeader {

    private int id;
    private String method;
    private int len;

    public LocoPacketHeader(int id, String method, int len) {
        this.id = id;
        this.method = method;
        this.len = len;
    }

    public int getId() {
        return id;
    }

    public String getMethod(){
        return method;
    }

    public int getLength() {
        return len;
    }

}
