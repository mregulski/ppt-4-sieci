#!/usr/bin/python3
import networkx as nx
import random
import matplotlib.pyplot as plot
# import json
# import pprint
import generate

verbosity = 0


def main(args):
    if args.file is not None:
        network, flows = generate.networkFromFile(args.file)
    else:
        print("No input file specified - using sample data.")
        network, flows = generate.sampleData(min_packet=1, max_packet=10)

    drawNetwork(network)
    if args.intervals is None:
        testFlows(network, flows)
    else:
        failures = 0
        for t in range(args.intervals):
            delay = testFlows(network, flows)
            if delay is None:
                print("\x1b[0;31mnetwork failure\x1b[0m")
                failures += 1
            elif delay > args.max_delay:
                print("\x1b[0;33mavg delay: {}\x1b[0m".format(delay))
                failures += 1
            else:
                print("\x1b[0;32mavg delay: {}\x1b[0m".format(delay))
        print("reliability: {:5.2f}".format((1-failures/args.intervals)*100))


def testFlows(network, flows):
    global verbosity
    if verbosity >= 1:
        print("Testing flows...")

    tmp = network.copy()
    possible, depleted, failed = applyFlows(tmp, flows, fail=True)
    tmp.add_edges_from(depleted)
    if verbosity >= 1:
        print("{}".format("\x1b[2;33mFlows applied\x1b[0m" if possible
                          else "\x1b[2;31mNetwork disconnected\x1b[0m" if not nx.is_connected(tmp)
                          else "\x1b[2;31mCannot apply specified flows\x1b[0m"))

    tmp.add_edges_from(failed)
    if verbosity >= 2:
        print("Network status:")
        print("{:^7s} {:^8s} {:^8s} {:>8s} {:^10s}"
              .format("edge", "capacity", "flow", "usage", "status"))
        for (x, y) in network.edges():
            print("({:2d},{:2d}) {:^8d} {:^8d} {:8.2f}% {}"
                  .format(x,
                          y,
                          tmp[x][y]['capacity'], tmp[x][y]['flow'],
                          (tmp[x][y]['flow']/tmp[x][y]['capacity'] * 100),
                          "[FAILED]" if (x, y, tmp[x][y]) in failed else
                          "[DEPLETED]" if tmp[x][y]['flow'] > tmp[x][y]['capacity'] else ""
                          )
                  )
    # print("Depleted connections: {}.".format(depleted if len(depleted) > 0 else "none"))
    if(possible):
        delay = averageDelay(tmp, flows, depleted)
        if verbosity >= 1:
            if verbosity >= 3:
                print("depleted: {}".format(depleted))
                print("removed: {}".format(failed))
            print()
        return delay
    network = tmp
    return None


def applyFlows(graph, flows, fail=False):
    """
    Check if the graph can realize specified flows.

    This function calculates the shortest path from each flow's source to its target
    and adds flow's payload to the actual flow of each channel on the path.
    If channel's capacity is depleted, it's removed from the network, and a new path
    is calculated from the beginning of depleted channel. E.g. if data is sent from
    1 to 7 by path [1,2,3,4,5,6,7], and it depletes channel (4,5), a new path is
    calculated from 4 to 7. Data is sent aggresively, so payload is added to (4,5)
    even though it can't go through it, so the channel is removed from the graph.

    fail=True adds reliability simulation. Before calculating flows, each channel
    is tested for failure and possibly removed.
    returns tuple:
        (canRealizeFlow, depletedEdges, failedEdges)
    """
    # graph = network.copy()
    depleted = []
    failed = []
    if fail:
        for (x, y) in graph.edges():
            r = random.random()
            if r > graph[x][y]['reliability']:
                failed.append((x, y, graph[x][y]))
        graph.remove_edges_from(failed)

    if not nx.is_connected(graph):
        return False, depleted, failed

    for flow in flows:
        # print(flow)
        path = nx.shortest_path(graph, flow['source'], flow['target'])
        payload = flow['amount']
        # print("payload: {}".format(payload))
        for i in range(len(path)-1):
            x = path[i]
            y = path[i+1]
            graph[x][y]['flow'] += payload
            if(graph[x][y]['capacity'] <= graph[x][y]['flow']):
                depleted.append((x, y, graph[x][y]))
                graph.remove_edge(x, y)
                # print("Edge capacity depleted: ({},{})".format(x, y))
                if(not nx.is_connected(graph)):
                    return False, depleted, failed

            # while(True):
            #     # print("i={}, path: {}".format(i, path))
            #     try:
            #         x = path[i]
            #         y = path[i+1]
            #     except IndexError:
            #         print("INDEX ERROR")
            #     print(path[i], path[i+1])
            #     # print("capacity: {}".format(graph[x][y]['capacity']))
            #     graph[x][y]['flow'] += payload
            #     if(graph[x][y]['capacity'] <= graph[x][y]['flow']):
            #         depleted.append((x, y, graph[x][y]))
            #         graph.remove_edge(x, y)
            #         # print("Edge capacity depleted: ({},{})".format(x, y))
            #         if(not nx.is_connected(graph)):
            #             return False, depleted, failed
            #         path = nx.shortest_path(graph, x, flow['target'])
            #     break
            # print("capacity: {}".format(graph[x][y]['capacity']))
        # print()
    return True, depleted, failed


def averageDelay(network, flows, depleted_channels):
    net = network.copy()
    net.remove_edges_from(depleted_channels)
    # net.remove_edges_from(depleted_channels)
    requestedFlows = flowTotal(flows)
    sum_e = 0
    for (x, y) in net.edges():
        channel = net[x][y]
        sum_e += channel['flow']/(channel['capacity'] - channel['flow'])
    return sum_e / requestedFlows


def flowTotal(flows):
    total = 0
    for flow in flows:
        total += flow['amount']
    return total


def drawNetwork(network):
    global verbosity
    pos = nx.spring_layout(network)
    nx.draw_networkx_nodes(network, pos, node_size=400, node_color='#5678dd')
    nx.draw_networkx_edges(network, pos, width=1.0)
    e_labels = {}
    for (x, y) in network.edges():
        e_labels[(x, y)] = network[x][y]['capacity']
    nx.draw_networkx_edge_labels(network, pos, edge_labels=e_labels, label_pos=0.6)
    nx.draw_networkx_labels(network, pos)
    plot.axis('off')
    if(verbosity >= 2):
        print("Drawing network graph '{0}' to {0}.png".format(network.graph['name']))
    plot.savefig(network.graph['name']+".png")
    plot.close()


if __name__ == "__main__":
    import argparse

    def probability(value):
        fval = float(value)
        if fval < 0 or fval > 1:
            raise argparse.ArgumentTypeError("{} is an invalid probability".format(value))
        return fval

    parser = argparse.ArgumentParser(description="Network flow and delay simulator")
    parser.add_argument("-f", "--file", type=str, help="JSON file with network definition")
    parser.add_argument("-i", "--intervals", type=int, help="number of tests to carry out")
    parser.add_argument("-t", "--max-delay", type=float, help="target delay for tests", required=True)
    parser.add_argument("-v", "--verbose", action='count')
    args = parser.parse_args()
    verbosity = args.verbose if args.verbose is not None else 0
    main(args)
