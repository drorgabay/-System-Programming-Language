//
// Created by spl211 on 03/01/2021.
//

#ifndef BOOST_ECHO_CLIENT_SOCKETANSWER_H
#define BOOST_ECHO_CLIENT_SOCKETANSWER_H

#include "connectionHandler.h"

class SocketAnswer {
public:
    SocketAnswer(ConnectionHandler &);

    bool getAnswer(std::string &ans);

    short bytesToShort(char *);

private:
    ConnectionHandler &myConnectionHandler;
    std::string print;
    std::string optional;

};


#endif //BOOST_ECHO_CLIENT_SOCKETANSWER_H
