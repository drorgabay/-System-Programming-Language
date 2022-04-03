package bgu.spl.net.impl.BGRSServer.Messages;

import java.util.ArrayList;
import java.util.List;

public class LoginMessage extends Message {
    private String username;
    private String password;
    public LoginMessage(String user,String pass){
        super((short)3);
        username = user;
        password = pass;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public List<String> getData() {
        List<String> l = new ArrayList<>();
        l.add(username);
        l.add(password);
        return l;
    }

    @Override
    public byte[] selfEncode() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
