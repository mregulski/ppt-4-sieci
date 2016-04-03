#!/usr/bin/python3
import networkx as nx
import random
import matplotlib.pyplot as plot
import json
import pprint


def networkFromFile(file, reliability_min=0, reliability_base=1, reliability_max=1,
                    flow_min=0, flow_base=15, flow_max=30,
                    capacity_min=100, capacity_base=400, capacity_max=1000):
    edges, flows = [], []
    maxNode = 0
    with open(file) as data_file:
        data = json.load(data_file)
    for edge in data['network']:
        start = edge['start']
        maxNode = start if start > maxNode else maxNode
        end = edge['end']
        maxNode = end if end > maxNode else maxNode

        if edge['capacity'] == "":
            capacity = capacity_base
        elif edge['capacity'] == "r":
            capacity = random.randrange(capacity_min, capacity_max+1)
        else:
            capacity = edge['capacity']

        if edge['reliability'] == "":
            reliability = reliability_base
        elif edge['reliability'] == "r":
            reliability = random.uniform(reliability_min, reliability_max)
        else:
            reliability = edge['reliability']

        edges.append((start, end,
                     {'capacity': capacity, 'reliability': reliability, 'flow': 0}))

    if len(data['flows']) == 0:
        print("No flows found - generating random data.")
        flows = randomFlows(flow_min, flow_max, maxNode)
    else:
        for x in range(len(data['flows'])):
            for y in range(len(data['flows'][x])):
                if data['flows'][x][y] == "r":
                    amount = random.randrange(flow_min, flow_max+1)
                else:
                    amount = data['flows'][x][y]
                flows.append({'source': x+1, 'target': y+1, 'amount': amount})
        # for flow in data['flows']:
        #     source = flow['source']
        #     target = flow['target']
        #     if flow['amount'] == "":
        #         amount = flow_base
        #     elif flow['amount'] == "r":
        #         amount = random.randrange(flow_min, flow_max+1)
        #     else:
        #         amount = flow['amount']
        #
        #     flows.append({'source': source, 'target': target, 'amount': amount})

    # pprint.pprint(edges)
    # pprint.pprint(flows)
    network = nx.Graph()
    network.graph['name'] = data['name']
    network.add_edges_from(edges)
    return network, flows


def randomFlows(min_packet, max_packet, nodes):
    flows = []
    for src in range(1, nodes+1):
        for tgt in range(1, nodes+1):
            if(tgt != src):
                flows.append({'source': src,
                              'target': tgt,
                              'amount': random.randrange(min_packet, max_packet+1)})
    return flows


def sampleData(p=0.8, min_packet=15, max_packet=30):
    if min_packet > max_packet:
        min_packet, max_packet = max_packet, min_packet
    network = nx.Graph(name="sampleNetwork1")
    # capacity: maximum flow, measured in average packet sizes
    # flow: amount of data sent through channel
    # reliability: probability of channel not breaking
    edges = [(1, 2,  {'capacity': 400, 'reliability': p, 'flow': 0}),
             (1, 5,  {'capacity': 400, 'reliability': p, 'flow': 0}),
             (2, 3,  {'capacity': 400, 'reliability': p, 'flow': 0}),
             (2, 5,  {'capacity': 900, 'reliability': p, 'flow': 0}),
             (2, 6,  {'capacity': 300, 'reliability': p, 'flow': 0}),
             (3, 4,  {'capacity': 600, 'reliability': p, 'flow': 0}),
             (3, 6,  {'capacity': 300, 'reliability': p, 'flow': 0}),
             (3, 7,  {'capacity': 700, 'reliability': p, 'flow': 0}),
             (4, 7,  {'capacity': 200, 'reliability': p, 'flow': 0}),
             (5, 6,  {'capacity': 100, 'reliability': p, 'flow': 0}),
             (5, 8,  {'capacity': 200, 'reliability': p, 'flow': 0}),
             (5, 9,  {'capacity': 400, 'reliability': p, 'flow': 0}),
             (6, 8,  {'capacity': 600, 'reliability': p, 'flow': 0}),
             (6, 9,  {'capacity': 400, 'reliability': p, 'flow': 0}),
             (6, 10, {'capacity': 300, 'reliability': p, 'flow': 0}),
             (7, 9,  {'capacity': 300, 'reliability': p, 'flow': 0}),
             (7, 10, {'capacity': 100, 'reliability': p, 'flow': 0}),
             (8, 9,  {'capacity': 900, 'reliability': p, 'flow': 0}),
             (9, 10, {'capacity': 300, 'reliability': p, 'flow': 0})]    # 19 edges

    network.add_edges_from(edges)
    flows = []
    for src in range(1, 11):
        for tgt in range(1, 11):
            if(tgt != src):
                flows.append({'source': src,
                              'target': tgt,
                              'amount': random.randrange(min_packet, max_packet+1)})
    return network, flows
