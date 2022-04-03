package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.IMP.Database;
import bgu.spl.net.impl.BGRSServer.IMP.MessageEncoderDecoderImp;
import bgu.spl.net.impl.BGRSServer.IMP.MessageProtocolImp;
import bgu.spl.net.srv.Server;


import static java.lang.Integer.parseInt;

public class ReactorMain {
    public static void main(String[] args) {
        Server reactor= Server.reactor(parseInt(args[1]), parseInt(args[0]), MessageProtocolImp::new, MessageEncoderDecoderImp::new);
        reactor.serve();
    }
}
