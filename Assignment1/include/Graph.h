#ifndef PROJECT1_GRAPH_H
#define PROJECT1_GRAPH_H
#include <vector>

class Graph{
public:
    Graph(std::vector<std::vector<int>> matrix);


    const std::vector<std::vector<int>>& getEdges() const;
    void eraseEdges(int& nodeInd);
    void infectNode(int nodeInd);
    bool isInfected(int nodeInd);
    int popInfected();
    std::vector<int> neighborsOf(int) const;
    bool isLegal() ;
    bool isInTheOutput(int nodeIndex) const;
    std::vector<int>& getOutput();
    bool isCarry(int nodeIndex);
    void addToCarry(int nodeIndex);

private:
    std::vector<std::vector<int>> edges;
    std::vector<int> infected;
    std::vector<int> output;
    std::vector<int> carry;
};
#endif //PROJECT1_GRAPH_H
