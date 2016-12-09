package ist.csf.snifftorrent.classes;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class ServerProperties implements Serializable {
    public long timeWindow;
    public int numberOfPackets;

    public ServerProperties(long timeWindowInMinutes, int numberOfPackets) {
        this.timeWindow = TimeUnit.MINUTES.toMillis(timeWindowInMinutes);
        this.numberOfPackets = numberOfPackets;
    }
}
