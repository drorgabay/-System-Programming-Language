package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.List;

public class MyCoursesMessage extends Message {

    public MyCoursesMessage(){
        super((short)11);
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
