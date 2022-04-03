package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.IMP.MessageEncoderDecoderImp;
import bgu.spl.net.impl.BGRSServer.IMP.MessageProtocolImp;
import bgu.spl.net.srv.Server;

import static java.lang.Integer.parseInt;

public class TPCMain {

    public static void main(String[] args) {
        Server reactor = Server.threadPerClient(parseInt(args[0]), MessageProtocolImp::new, MessageEncoderDecoderImp::new);
        reactor.serve();

    }
}
