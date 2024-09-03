package com.test.tree;

import java.util.HashMap;
import java.util.Map;

public class TreeDataGenerator {

    private Map<Integer, Integer[]> data;

    public TreeDataGenerator() {
        data = new HashMap<>();
        generateSampleData();
    }

    private void generateSampleData() {
        data.put(1, new Integer[]{2, 3});
        data.put(2, new Integer[]{});
        data.put(3, new Integer[]{4, 5, 6});
        data.put(4, new Integer[]{});
        data.put(5, new Integer[]{});
        data.put(6, new Integer[]{7, 8});
        data.put(7, new Integer[]{});
        data.put(8, new Integer[]{});
    }
    
    private void generateSampleData2() {
        int totalNodes = 100000;  // 총 1000개의 노드를 생성

        int nodeId = 1;  // 시작 노드 ID
        int childId = 2;  // 자식 노드의 ID 시작

        // 루트 노드 추가
        data.put(nodeId, new Integer[]{childId, childId + 1});
        nodeId++;
        childId += 2;

        while (childId <= totalNodes) {
            // 각 노드에 2개의 자식 노드를 추가
            if (childId + 1 <= totalNodes) {
                data.put(nodeId, new Integer[]{childId, childId + 1});
                childId += 2;
            } else {
                data.put(nodeId, new Integer[]{childId});
                childId++;
            }
            nodeId++;
        }

        // 마지막 노드들은 자식이 없음을 설정
        for (int i = nodeId; i <= totalNodes; i++) {
            data.put(i, new Integer[]{});
        }
    }


    public Map<Integer, Integer[]> getData() {
        return data;
    }
}
