//
// Created by spl211 on 03/01/2021.
//

#ifndef BOOST_ECHO_CLIENT_KEYBOARD_H
#define BOOST_ECHO_CLIENT_KEYBOARD_H

#include "connectionHandler.h"

using namespace std;

class Keyboard {
public:
    Keyboard(ConnectionHandler &connectionHandler);

    void run();

private:
    ConnectionHandler &myConnectionHandler;

    void shortToBytes(short, char *);

    void userPass(int opNum, char opcode[], std::vector<std::string> &userCommand);

    void course(int opNum, char opcode[], std::vector<std::string> &userCommand);
};


#endif //BOOST_ECHO_CLIENT_KEYBOARD_H
