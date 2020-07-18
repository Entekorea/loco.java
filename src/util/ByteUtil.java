package a.b.c.d.util;

public class ByteUtil {

    public static byte[] intToByteArrayLE(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value);
        byteArray[1] = (byte)(value >> 8);
        byteArray[2] = (byte)(value >> 16);
        byteArray[3] = (byte)(value >> 24);

        return byteArray;
    }

    public static int byteArrayToIntLE(byte[] bytes) {
        return ((bytes[0] & 0xFF)) |
                ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[3] & 0xFF) << 24);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }


    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02X", b&0xff));
        }

        return sb.toString();
    }

    public static byte[] addBytes(byte[]... bytes) {
        int len = 0;
        for (byte[] i: bytes) len += i.length;

        byte[] r = new byte[len];
        int c = 0;
        for (byte[] i: bytes) {
            System.arraycopy(i, 0, r, c, i.length);
            c += i.length;
        }

        return r;
    }

}

