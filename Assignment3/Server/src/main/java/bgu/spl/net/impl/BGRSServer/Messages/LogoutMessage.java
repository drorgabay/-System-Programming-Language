package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.List;

public class LogoutMessage extends Message {
    public LogoutMessage(){
        super((short)4);
    }

    @Override
    public List<String> getData() {
        return null;
    }

    @Override
    public byte[] selfEncode() {
        return new byte[0];
    }
}
