package a.b.c.d.packet.request;

import a.b.c.d.packet.response.ResponsePacket;

import java.util.concurrent.CompletableFuture;

public abstract class RequsetPacket {

    CompletableFuture<ResponsePacket> future = new CompletableFuture<ResponsePacket>();

    public abstract String getMethod();
    public abstract byte[] toBosn();

    public ResponsePacket getResponse() {
        try {
            return future.get();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<ResponsePacket> getFuture() {
        return future;
    }

}
