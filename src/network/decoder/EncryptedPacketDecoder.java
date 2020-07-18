package a.b.c.d.network.decoder;

import a.b.c.d.crypto.EncryptedHeader;
import a.b.c.d.network.LocoSocket;
import a.b.c.d.util.ByteUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class EncryptedPacketDecoder extends MessageToMessageDecoder<byte[]> {

    LocoSocket socket;

    public EncryptedPacketDecoder(LocoSocket socket) {
        this.socket = socket;
    }

    EncryptedHeader currentHeader = null;
    byte[] currentPacket = new byte[0];

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, byte[] i, List<Object> list) throws Exception {
        currentPacket = ByteUtil.addBytes(currentPacket, i);

        if (currentHeader == null && currentPacket.length > 4) {
            byte[] lenBytes = new byte[4];
            System.arraycopy(currentPacket, 0, lenBytes, 0, 4);
            int len = ByteUtil.byteArrayToIntLE(lenBytes);
            currentHeader = new EncryptedHeader(len, null);
        }

        if (currentHeader != null) {
            int encryptedPacketSize = 4 + currentHeader.getLength();

            if (currentPacket.length == encryptedPacketSize) {
                list.add(socket.cryptoManager.decryptAES(currentPacket));

                currentPacket = new byte[0];
                currentHeader = null;
            } else if (currentPacket.length >= encryptedPacketSize) {

                byte[] encryptedBody = new byte[encryptedPacketSize];
                System.arraycopy(currentPacket, 0, encryptedBody, 0, encryptedPacketSize);

                byte[] nextBody = new byte[currentPacket.length - encryptedPacketSize];
                System.arraycopy(currentPacket, encryptedPacketSize, nextBody, 0, currentPacket.length - encryptedPacketSize);

                currentPacket = new byte[0];
                currentHeader = null;

                list.add(socket.cryptoManager.decryptAES(encryptedBody));
                decode(channelHandlerContext, nextBody, list);
            }
        }
    }
}