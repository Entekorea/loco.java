package a.b.c.d.crypto;

public class EncryptedHeader {

    private int len;
    private byte[] iv;

    public EncryptedHeader(int len, byte[] iv) {
        this.len = len;
        this.iv = iv;
    }

    public int getLength() {
        return len;
    }

    public byte[] getIV() {
        return iv;
    }

}
