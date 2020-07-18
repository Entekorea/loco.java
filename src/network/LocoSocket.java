package a.b.c.d.network;

import a.b.c.d.TalkClient;
import a.b.c.d.crypto.CryptoManager;
import a.b.c.d.network.decoder.EncryptedPacketDecoder;
import a.b.c.d.network.decoder.LocoPacketDecoder;
import a.b.c.d.packet.PacketReader;
import a.b.c.d.packet.PacketWriter;
import a.b.c.d.packet.request.RequsetPacket;
import a.b.c.d.packet.response.ResponsePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LocoSocket {

    private TalkClient client;
    private String host;
    private int port;

    public LocoSocket(TalkClient client, String host, int port) {
        this.client = client;
        this.host = host;
        this.port = port;
    }

    public boolean isConnected = false;
    public boolean disconnect = false;

    Thread thread;

    public ChannelHandlerContext context;

    public CryptoManager cryptoManager = new CryptoManager();
    public PacketWriter packetWriter = new PacketWriter();
    public PacketReader packetReader = new PacketReader();

    Map<Integer, CompletableFuture<ResponsePacket>> reqMap = new HashMap<Integer, CompletableFuture<ResponsePacket>>();

    public CompletableFuture<Boolean> connectFuture;

    NioEventLoopGroup group;

    EncryptedPacketDecoder encryptedPacketDecoder = new EncryptedPacketDecoder(this);
    LocoPacketDecoder locoPacketDecoder = new LocoPacketDecoder(this);

    public void connect() {
        connectFuture = new CompletableFuture<Boolean>();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                group = new NioEventLoopGroup(1);
                try{
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast(new ByteArrayEncoder());
                                    pipeline.addLast(new ByteArrayDecoder());
                                    pipeline.addLast(encryptedPacketDecoder);
                                    pipeline.addLast(locoPacketDecoder);
                                }
                            });

                    ChannelFuture future = b.connect(host, port).sync();
                    future.channel().closeFuture().sync();
                } catch(Exception e) {
                    isConnected = false;
                    e.printStackTrace();
                } finally{
                    group.shutdownGracefully();
                }
            }
        });
        thread.start();
    }

    public void onPacket(int id, ResponsePacket res) {
        if (res == null) return;
        if(reqMap.containsKey(id)) {
            reqMap.get(id).complete(res);
        } else {
            client.onPacket(res);
        }
    }

    public RequsetPacket sendPacket(RequsetPacket req) {
        try {
            if (!isConnected && connectFuture == null) throw new Error("No connection to the server");
            if (!isConnected) {
                connectFuture.get();
            }
            int id = packetWriter.getNextId();
            reqMap.put(id, req.getFuture());

            context.write(cryptoManager.encryptedAES(packetWriter.toLocoPacket(id, req)));
            context.flush();

            return req;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stop() {
        try {
            if (!isConnected && connectFuture == null) throw new Error("No connection to the server");
            if (!isConnected) {
                connectFuture.get();
            }
            context.close();
            disconnect = true;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
