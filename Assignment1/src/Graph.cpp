//
// Created by spl211 on 03/11/2020.
//

#include "../include/Graph.h"

using namespace std;

Graph::Graph(std::vector<std::vector<int>> matrix) : edges(std::move(matrix)), infected({}), output({}), carry({}) {}

bool Graph::isInfected(int nodeInd) {
    for (int i : output) {
        if (i == nodeInd)
            return true;
    }
    return false;
}

void Graph::infectNode(int nodeInd) {
    if (!isInfected(nodeInd)) {
        infected.push_back(nodeInd);
        output.push_back(nodeInd);
    }
}

const std::vector<std::vector<int>> &Graph::getEdges() const {
    return edges;
}

void Graph::eraseEdges(int &nodeInd) {
    int size = edges.size();
    for (int i = 0; i < size; ++i) {
        if (edges[i][nodeInd] == 1) {
            edges[i][nodeInd] = 0;
            edges[nodeInd][i] = 0;
        }
    }
}

int Graph::popInfected() {
    if (!infected.empty()) {
        int node = infected[0];
        infected.erase(infected.begin());
        return node;
    }
    return -1;
}

std::vector<int> Graph::neighborsOf(int nodeIndex) const {
    std::vector<int> neighborsOf;
    int size = edges.size();
    for (int i = 0; i < size; ++i) {
        if (edges[i][nodeIndex] == 1)
            neighborsOf.push_back(i);
    }
    return neighborsOf;
}

bool Graph::isLegal() {
    int size = edges.size();
    for (int i = 0; i < size; ++i) {
        if (isInTheOutput(i)) {
            std::vector<int> neighbors = neighborsOf(i); //changed to save less memory
            for (int neighbor : neighbors) { //change in dror
                if (!isInTheOutput(neighbor))
                    return false;
            }
        } else if (!isCarry(i)) {
            std::vector<int> neighbors = neighborsOf(i);
            for (int neighbor : neighbors) {
                if (isCarry(neighbor) || isInTheOutput(i))
                    return false;
            }
        }
    }
    return true;
}
bool Graph::isInTheOutput(int nodeIndex) const {
    for (int i : output) {
        if (i == nodeIndex)
            return true;
    }
    return false;
}

std::vector<int> &Graph::getOutput() {
    return output;
}

bool Graph::isCarry(int nodeIndex) {
    for (int i : carry) {
        if (i == nodeIndex)
            return true;
    }
    return false;
}

void Graph::addToCarry(int nodeIndex) {
    carry.push_back(nodeIndex);
}

