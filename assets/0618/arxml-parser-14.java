private static void printTableData(List<ArxmlParsedData> batch) {
    Map<Integer, List<ArxmlParsedData>> treeMap = new HashMap<>();
    for (ArxmlParsedData data : batch) {
        treeMap.computeIfAbsent(data.getParentId(), k -> new ArrayList<>()).add(data);
    }

    printTree(0, 0, treeMap);
}

private static void printTree(int parentId, int indent, Map<Integer, List<ArxmlParsedData>> treeMap) {
    if (!treeMap.containsKey(parentId)) return;

    for (ArxmlParsedData data : treeMap.get(parentId)) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.printf("%-5d %-30s %-50s %-15s %-20s%n", data.getId(), data.getName(), data.getXmlPath(), data.getKey(), data.getValue());
        printTree(data.getId(), indent + 1, treeMap);
    }
}
