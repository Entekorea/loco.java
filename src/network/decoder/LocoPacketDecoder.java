package a.b.c.d.network.decoder;

import a.b.c.d.network.LocoPacketHeader;
import a.b.c.d.network.LocoSocket;
import a.b.c.d.util.ByteUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LocoPacketDecoder extends ChannelInboundHandlerAdapter {

    LocoSocket socket;

    public LocoPacketDecoder(LocoSocket socket) {
        this.socket = socket;
    }

    LocoPacketHeader currentHeader;
    byte[] currentPacket = new byte[0];

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        socket.isConnected = true;
        ctx.write(socket.cryptoManager.generateHandshake());
        ctx.flush();
        socket.context = ctx;
        socket.connectFuture.complete(true);
        System.out.println("Connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (socket.disconnect) {
            ctx.close();
        }
        currentPacket = ByteUtil.addBytes(currentPacket, (byte[]) msg);

        if(currentHeader == null && currentPacket.length > 22) {
            currentHeader = socket.packetReader.readHeader((byte[]) msg);
        }

        if (currentHeader != null) {
            int encryptedPacketSize = 22 + currentHeader.getLength();

            if (currentPacket.length == encryptedPacketSize) {
                LocoPacketHeader tHeader = currentHeader;
                byte[] tBody = currentPacket;
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            socket.onPacket(tHeader.getId(), socket.packetReader.readBody(tHeader, tBody));
                        } catch(NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                currentPacket = new byte[0];
                currentHeader = null;
            } else if (currentPacket.length > encryptedPacketSize) {

                byte[] encryptedBody = new byte[encryptedPacketSize];
                System.arraycopy(currentPacket, 0, encryptedBody, 0, encryptedPacketSize);

                byte[] nextBody = new byte[currentPacket.length - encryptedPacketSize];
                System.arraycopy(currentPacket, encryptedPacketSize, nextBody, 0, currentPacket.length - encryptedPacketSize);

                LocoPacketHeader tHeader = currentHeader;
                byte[] tBody = encryptedBody;
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            socket.onPacket(tHeader.getId(), socket.packetReader.readBody(tHeader, tBody));
                        } catch(NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                currentPacket = new byte[0];
                currentHeader = null;
                channelRead(ctx, nextBody);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
    }
}
