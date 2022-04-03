//
// Created by spl211 on 03/11/2020.
//
#include "../include/Agents.h"
#include "../include/Tree.h"

using namespace std;

//------------Agent---------------
Agent::Agent():value(0) {}

const int Agent::getValue() const {
    return value;
}

Agent::~Agent() {}

//----------ContactTracer-----------
ContactTracer::ContactTracer() : Agent() { value = -1;} //NEW

void ContactTracer::act(Session &session) {
    int infectedNode(session.dequeueInfected());
    if (infectedNode != -1) {
        Tree *bfs = Tree::createTree(session, infectedNode);
        if (!session.getGraph().neighborsOf(infectedNode).empty()) {
            bfs->BFS(session);
            int index(bfs->traceTree());
            session.getGraph().eraseEdges(index);
        }
        delete bfs;
    }
}

Agent *ContactTracer::clone() const {
    return new ContactTracer(*this);
}


//------------Virus------------
Virus::Virus(int nodeInd) : Agent(), nodeInd(nodeInd) { value =nodeInd;}

void Virus::act(Session &session) {
    if (!(session.getGraph().isInfected(nodeInd))) {
        if (!session.getGraph().isCarry(nodeInd)) {
            session.getGraph().addToCarry(nodeInd);
        }
        session.enqueueInfected(nodeInd);
    }
    std::vector<int> neighborsOf = session.getGraph().neighborsOf(nodeInd);
    for (int i : neighborsOf) {
        if (!(session.getGraph().isCarry(i))) {
            session.getGraph().addToCarry(i);
            session.addAgent(Virus(i));
            break;
        }
    }
}

Agent *Virus::clone() const { return new Virus(*this); }


