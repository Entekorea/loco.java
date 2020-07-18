package a.b.c.d;

import a.b.c.d.talk.PacketListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Invoker {

    private TalkClient client;
    private final BlockingQueue<String[]> newQueue = new LinkedBlockingQueue<String[]>(100);
    private final BlockingQueue<String[]> delQueue = new LinkedBlockingQueue<String[]>(100);

    public void login(String email, String password, String deviceUUID, String deviceName) throws Exception {
        client = new TalkClient(deviceName);
        client.listeners.put("NewMem", new PacketListener() {
            @Override
            public void onMessage(String[] str) {
                newQueue.offer(str);
            }
        });
        client.listeners.put("DelMem", new PacketListener() {
            @Override
            public void onMessage(String[] str) {
                delQueue.offer(str);
            }
        });
        client.login(email, password, deviceUUID);
    }

    public String[] onNewMem() throws Exception {
        return newQueue.take();
    }

    public String[] onDelMem() throws Exception {
        return delQueue.take();
    }

    public void stop() throws Exception {
        client.stop();
        newQueue.offer(null);
        delQueue.offer(null);
    }

}
