package com.test.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeDataGenerator {

	private Map<Integer, ArrayList<Integer>> data;

	public TreeDataGenerator() {
		data = new HashMap<>();
		generateSampleData();
	}

	private void generateSampleData2() {
		data.put(1, new ArrayList<>(List.of(2, 3)));
		data.put(2, new ArrayList<>());
		data.put(3, new ArrayList<>(List.of(4, 5, 6)));
		data.put(4, new ArrayList<>());
		data.put(5, new ArrayList<>());
		data.put(6, new ArrayList<>(List.of(7, 8)));
		data.put(7, new ArrayList<>());
		data.put(8, new ArrayList<>());
	}

	private void generateSampleData() {
		int totalNodes = 100000; // 총 1000개의 노드를 생성

		int nodeId = 1; // 시작 노드 ID
		int childId = 2; // 자식 노드의 ID 시작

		// 루트 노드 추가
		ArrayList<Integer> rootChildren = new ArrayList<>();
		rootChildren.add(childId);
		rootChildren.add(childId + 1);
		data.put(nodeId, rootChildren);
		nodeId++;
		childId += 2;

		while (childId <= totalNodes) {
			// 각 노드에 2개의 자식 노드를 추가
			ArrayList<Integer> children = new ArrayList<>();
			if (childId + 1 <= totalNodes) {
				children.add(childId);
				children.add(childId + 1);
				childId += 2;
			} else {
				children.add(childId);
				childId++;
			}
			data.put(nodeId, children);
			nodeId++;
		}

		// 마지막 노드들은 자식이 없음을 설정
		for (int i = nodeId; i <= totalNodes; i++) {
			data.put(i, new ArrayList<>());
		}
	}

	public Map<Integer, ArrayList<Integer>> getData() {
		return data;
	}

	public static void main(String[] args) {
		TreeDataGenerator generator = new TreeDataGenerator();
		Map<Integer, ArrayList<Integer>> data = generator.getData();

		// 생성된 데이터 확인 (첫 20개 노드만 출력)
		for (int i = 1; i <= 20; i++) {
			System.out.println("Node " + i + ": " + data.get(i));
		}
	}
}
