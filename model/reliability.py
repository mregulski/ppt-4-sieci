#!/usr/bin/python3
import argparse                     # commandline parsing
import networkx as nx               # graph library
import matplotlib.pyplot as plot    # for drawing
import graphviz
import random                       # much random wow


def main(intervals, mode):

    # a) base network
    Sa = nx.MultiGraph([(x, x+1, {'reliability': 0.95}) for x in range(1, 20)],
                       name='network A')
    drawNetwork(Sa, Sa.edges())

    # b) one extra edge closing the circle
    Sb = nx.MultiGraph(Sa, name='network B')
    edges_b = [(1, 20, {'reliability': 0.95})]
    Sb.add_edges_from(edges_b)
    drawNetwork(Sb, Sb.edges())

    # c) two more edges, less reliable
    Sc = nx.MultiGraph(Sb, name='network C')
    edges_c = [(1, 10, {'reliability': 0.8}), (5, 15, {'reliability': 0.7})]
    Sc.add_edges_from(edges_c)

    drawNetwork(Sc, Sb.edges(), [(1, 10, {'reliability': 0.8})],
                [(5, 15, {'reliability': 0.7})])

    # d) 4 more random edges, even less reliable
    Sd = nx.MultiGraph(Sc, name='network D')
    rand_edges = [(random.randrange(1, 21), random.randrange(1, 21), {'reliability': 0.4})
                  for x in range(4)]
    Sd.add_edges_from(rand_edges)
    drawNetwork(Sd, Sb.edges(), [(1, 10, {'reliability': 0.8})],
                [(5, 15, {'reliability': 0.7})], rand_edges)
    test_count = intervals
    failure_counts = []
    simulateNetwork(Sa, test_count)
    simulateNetwork(Sb, test_count)
    simulateNetwork(Sc, test_count)
    simulateNetwork(Sd, test_count)


def simulateNetwork(network, timeRange):
    failures = 0
    removed = []
    for t in range(timeRange):
        # print("time: {}".format(t))
        # check each connection for failure
        for (x, y) in network.edges():
            for idx in network[x][y]:
                r = random.SystemRandom().random()
                # print(network[x][y]['reliability'])
                if(r > network[x][y][idx]['reliability']):
                    # print("[ {:<8d}]removing {} (reliability: {} < random: {})"
                        #   .format(t, (x, y), network[x][y]['reliability'], r,))
                    removed.append((x, y, idx,
                                   {'reliability': network[x][y][idx]['reliability']}
                                    ))
        # print("\tremoving: {}".format(removed))
        network.remove_edges_from(removed)
        # print("\tstate: {}".format(network.edges()))
        if(not nx.is_connected(network)):
            failures += 1
        # print("\tre-adding edges: {}".format(removed))
        network.add_edges_from(removed)
        # print("\tstate: {}".format(network.edges()))
        removed = []
    print(network.graph['name'])
    print("\tTests: {}.".format(timeRange))
    print("\tNetwork disconnected in {} cases.".format(failures))
    print("\tReliability: {:2.3f}%.".format((1 - failures / timeRange) * 100))


def drawNetwork(network, e1=[], e2=[], e3=[], e4=[], layout=nx.shell_layout):
    pos = layout(network)
    nx.draw_networkx_nodes(network, pos, node_size=400, node_color='#5678dd')
    nx.draw_networkx_edges(network, pos, edgelist=e1, width=1.5)
    nx.draw_networkx_edges(network, pos, edgelist=e2, width=1.0)
    nx.draw_networkx_edges(network, pos, edgelist=e3, width=0.75)
    nx.draw_networkx_edges(network, pos, edgelist=e4, width=0.5)
    nx.draw_networkx_labels(network, pos)
    plot.axis('off')
    plot.savefig(network.graph['name']+".png")
    plot.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="")
    parser.add_argument("-i", "--intervals",
                        help="number of intervals to test", type=int,
                        default=1000)
    args = parser.parse_args()
    main(args.intervals, 0)
