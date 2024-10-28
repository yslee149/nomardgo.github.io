package org.yslee.tree;

import java.util.*;

import java.util.*;

import java.util.*;

public class TreeConverter {

    static class Node {
        int id;
        int parentId;
        String name;
        int depth;

        public Node(int id, int parentId, String name, int depth) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.depth = depth;
        }

        @Override
        public String toString() {
            return "id=" + id + " , parentId=" + parentId + " , name=" + name;
        }
    }

    public static void main(String[] args) {
        List<Map<String, Object>> data = Arrays.asList(
                createData("a", 0),
                createData("b", 1),
                createData("b-1", 2),
                createData("b-2", 2),
                createData("b-3", 2),
                createData("c", 1),
                createData("c-1", 2),
                createData("c-2", 2),
                createData("c-3", 2),
                createData("c-3-1", 3),
                createData("c-3-2", 3),
                createData("c-3-2-1", 4),
                createData("d", 1),
                createData("d-1", 2),
                createData("d-2", 2),
                createData("e", 1),
                createData("e-1", 2),
                createData("e-1-1", 3)
        );

        List<Node> nodes = convertToTree(data);

        for (Node node : nodes) {
            System.out.println(node);
        }
    }

    private static Map<String, Object> createData(String name, int depth) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("depth", depth);
        return map;
    }

    public static List<Node> convertToTree(List<Map<String, Object>> data) {
        List<Node> nodeList = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        int idCounter = 1;

        for (Map<String, Object> item : data) {
            String name = (String) item.get("name");
            int depth = (int) item.get("depth");

            // 스택 조정
            while (stack.size() > depth) {
                stack.pop();
            }

            // 부모 ID 결정
            int parentId = stack.isEmpty() ? 0 : stack.peek().id;

            // 노드 생성
            Node node = new Node(idCounter++, parentId, name, depth);

            // 노드 추가 및 스택에 푸시
            nodeList.add(node);
            stack.push(node);
        }
        return nodeList;
    }
}
