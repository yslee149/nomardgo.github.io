public static Map<Integer, ArrayList<Integer>> removeDuplicates(Map<Integer, ArrayList<Integer>> map) {
    // 새로운 Map을 생성하여 중복 제거된 결과를 저장
    Map<Integer, ArrayList<Integer>> resultMap = new HashMap<>();
    
    // Map의 각 키에 대해 중복을 제거
    for (Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
        // ArrayList를 LinkedHashSet으로 변환하여 중복 제거 및 순서 유지
        Set<Integer> set = new LinkedHashSet<>(entry.getValue());
        
        // 중복이 제거된 Set을 다시 ArrayList로 변환하여 새로운 Map에 저장
        resultMap.put(entry.getKey(), new ArrayList<>(set));
    }
    
    // 중복이 제거된 새로운 Map 반환
    return resultMap;
}