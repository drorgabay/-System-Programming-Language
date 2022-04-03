//
// Created by spl211 on 03/01/2021.
//

#include "../include/Keyboard.h"
#include <boost/algorithm/string.hpp>
#include "boost/lexical_cast.hpp"


using namespace std;

Keyboard::Keyboard(ConnectionHandler &connectionHandler) : myConnectionHandler(connectionHandler) {}

void Keyboard::run() {
    while (1) {
        //read from screen
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize); //blocking
        std::string line(buf);

        vector<string> userCommand;
        boost::split(userCommand, line, boost::is_any_of(" "));
        char opcode[2];
        if (userCommand[0] == "ADMINREG") {
            userPass(1, opcode, userCommand);
        }
        if (userCommand[0] == "STUDENTREG") {
            userPass(2, opcode, userCommand);
        }
        if (userCommand[0] == "LOGIN") {
            userPass(3, opcode, userCommand);
        }
        if (userCommand[0] == "LOGOUT") {
            shortToBytes(4, opcode);
            myConnectionHandler.sendBytes(opcode, 2);
            if (myConnectionHandler.isLogedIn()) //if the user is logged in then set the terminate sign to 1
                myConnectionHandler.setTerminate(1);
            while (myConnectionHandler.getTerminate() == 1) {} //busy wait until a ACK 4 message returns from server
            if (myConnectionHandler.getTerminate() ==-1) //if is an ACK 4 message then it will stop, else its Error 4 message and will continue;
                break;
        }
        if (userCommand[0] == "COURSEREG") {
            course(5, opcode, userCommand);
        }
        if (userCommand[0] == "KDAMCHECK") {
            course(6, opcode, userCommand);
        }
        if (userCommand[0] == "COURSESTAT") {
            course(7, opcode, userCommand);
        }
        if (userCommand[0] == "STUDENTSTAT") {
            shortToBytes(8, opcode);
            myConnectionHandler.sendBytes(opcode, 2);
            myConnectionHandler.sendLine(userCommand[1]);
        }
        if (userCommand[0] == "ISREGISTERED") {
            course(9, opcode, userCommand);
        }
        if (userCommand[0] == "UNREGISTER") {
            course(10, opcode, userCommand);
        }
        if (userCommand[0] == "MYCOURSES") {
            shortToBytes(11, opcode);
            myConnectionHandler.sendBytes(opcode, 2);
        }
    }

}

void Keyboard::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}


void Keyboard::userPass(int opNum, char *opcode, vector<std::string> &userCommand) {
    shortToBytes(opNum, opcode);
    myConnectionHandler.sendBytes(opcode, 2);
    myConnectionHandler.sendLine(userCommand[1]);
    myConnectionHandler.sendLine(userCommand[2]);
}

void Keyboard::course(int opNum, char *opcode, vector<std::string> &userCommand) {
    shortToBytes(opNum, opcode);
    myConnectionHandler.sendBytes(opcode, 2);
    char courseNUmber[2];
    auto myShort = boost::lexical_cast<short>(userCommand[1]);
    shortToBytes(myShort, courseNUmber);
    myConnectionHandler.sendBytes(courseNUmber, 2);
}


