#ifndef PROJECT1_AGENTS_H
#define PROJECT1_AGENTS_H
#include <vector>
#include "Session.h"

class Agent{
public:
    Agent();
    virtual ~Agent();
    virtual void act(Session& session)=0;
    virtual Agent* clone() const=0;
    const int getValue() const;

protected:
    int value;
};

class ContactTracer: public Agent{
public:
    ContactTracer();
    virtual void act(Session& session);
    Agent* clone() const;
};


class Virus: public Agent{
public:
    Virus(int nodeInd);
    virtual void act(Session& session);
    Agent* clone() const;
private:
    const int nodeInd;
};

#endif //PROJECT1_AGENTS_H
