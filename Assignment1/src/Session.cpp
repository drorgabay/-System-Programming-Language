//
// Created by spl211 on 03/11/2020.
//
#include <iostream>
#include <fstream>
#include "../include/Session.h"
#include "../include/Agents.h"
#include "../include/json.hpp"

using json = nlohmann::json;
using namespace std;


Session::Session(const std::string &path) : currCycle(0), agents({}), g({}), treeType() {
    ifstream i(path);
    json j;
    i >> j;
    //--------agents----------
    int size = j["agents"].size();
    for (int k = 0; k < size; ++k) {
        std::string type = j["agents"][k][0].get<string>();
        if (type == "V") {
            int value = j["agents"][k][1].get<int>();
            agents.push_back(new Virus(value));
        } else
            agents.push_back(new ContactTracer());
    }
    //---------tree type--------
    std::string tree = j["tree"].get<string>();
    if (tree == "M")
        treeType = MaxRank;
    else if (tree == "R")
        treeType = Root;
    else if (tree == "C")
        treeType = Cycle;
    //----------Graph------------------
    g = Graph(j["graph"]); //change in dror
    //---------------------------------------------------
}

//Destructor
Session::~Session() {
    clearSession();
}

//Copy Constructor
Session::Session(const Session &other) :currCycle(other.currCycle),agents({}), g(other.g), treeType(other.treeType){
    for (auto agent : other.agents) {
        agents.push_back(agent->clone());
    }
}

//Move Constructor
Session::Session(Session &&other) : currCycle(other.currCycle),agents(std::move(other.agents)), g(std::move(other.g)), treeType(other.treeType){}

//Copy Assignment
Session &Session::operator=(const Session &other) {
    if (&other != this) {
        currCycle = other.currCycle;
        treeType = other.treeType;
        g = other.g;
        clearSession();
        agents.clear();
        for (auto agent : other.agents) {
            agents.push_back(agent->clone());
        }
    }
    return *this;
}


//Move Assignment
Session &Session::operator=(Session &&other) {
    if (&other != this) {
        currCycle = other.currCycle;
        treeType = other.treeType;
        g = other.g;
        clearSession();
        agents.clear();
        agents = std::move(other.agents);
    }
    return *this;
}
//-----------------------------------------------------------------

void Session::setGraph(const Graph &graph) {
    g = graph;
}

void Session::simulate() { //ADD TO DROR
    for (auto & agent : agents) {
        if(agent->getValue() !=-1){
            g.addToCarry(agent->getValue());
        }
    }
    while (!g.isLegal() || currCycle == 0) {
        int size = agents.size();
        for (int i = 0; i < size; ++i) {
            agents[i]->act(*this);
        }
        currCycle++;
    }
    makeOutputJson();
}

void Session::addAgent(const Agent &agent) { //NEW
    bool contains = false;
    for (auto & i : agents) {
        if (i->getValue() == agent.getValue())
            contains = true;
    }
    if (!contains) {
        Agent *copy = agent.clone();
        agents.push_back(copy);
    }
}

const int &Session::getCurrCycle() const {
    return currCycle;
}

Graph &Session::getGraph() {
    return g;
}

int Session::dequeueInfected() {
    return g.popInfected();
}

void Session::enqueueInfected(int nodeIndex) {
    g.infectNode(nodeIndex);
}

TreeType Session::getTreeType() const {
    return treeType;
}



void Session::makeOutputJson() {
    ofstream out("output.json");
    json j;
    int size = g.getEdges().size();
    for (int i = 0; i < size; ++i) {
        j["graph"][i] = g.getEdges()[i];
    }
    j["infected"] = g.getOutput();
    out << j;
}

void Session::clearSession() {
    for (auto &agent : agents) {
        delete agent;
    }
}

