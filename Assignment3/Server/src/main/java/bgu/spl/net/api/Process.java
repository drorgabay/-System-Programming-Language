package bgu.spl.net.api;


import bgu.spl.net.impl.BGRSServer.Messages.Message;

public interface Process {
    Message act(Message m);
}
