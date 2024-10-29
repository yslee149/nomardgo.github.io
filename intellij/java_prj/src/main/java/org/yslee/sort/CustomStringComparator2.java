//package org.yslee.sort;
//
//import java.util.Comparator;
//import java.util.function.Function;
//
//public class CustomStringComparator2<T> implements Comparator<T> {
//
//    private final Function<T, String> keyExtractor;
//
//    /**
//     * 생성자: 객체에서 비교할 문자열을 추출하는 함수 전달
//     * @param keyExtractor 객체에서 비교할 문자열을 추출하는 함수
//     */
//    public CustomStringComparator2(Function<T, String> keyExtractor) {
//        this.keyExtractor = keyExtractor;
//    }
//
//    /**
//     * 문자열을 전처리하여 대소문자 무시, 공백 및 특수문자 제거
//     * @param s 비교할 문자열
//     * @return 전처리된 문자열
//     */
//    private String preprocess(String s) {
//        if (s == null) {
//            return "";
//        }
//        // 대소문자 무시: 모두 소문자로 변환
//        String lowerCase = s.toLowerCase();
//        // 공백 및 특수문자 제거: 알파벳과 숫자만 남김
//        return lowerCase.replaceAll("[^a-z0-9]", "");
//    }
//
//    @Override
//    public int compare(T o1, T o2) {
//        String processedS1 = preprocess(keyExtractor.apply(o1));
//        String processedS2 = preprocess(keyExtractor.apply(o2));
//        return processedS1.compareTo(processedS2);
//    }
//}
//
//////////////
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class AdvancedNaturalSortExample {
//    public static void main(String[] args) {
//        List<String> list = new ArrayList<>();
//        list.add("aaa.0");
//        list.add("aaa.1");
//        list.add("aaa.2");
//        list.add("aaa.11");
//        list.add("bbb.2.1");
//        list.add("bbb.2.10");
//        list.add("bbb.2.2");
//
//        // 커스텀 Comparator 사용
//        Collections.sort(list, new AdvancedNaturalOrderComparator());
//
//        // 정렬 결과 출력
//        for (String str : list) {
//            System.out.println(str);
//        }
//    }
//}
//
//// 여러 숫자 부분을 처리하는 자연스러운 순서 Comparator
//class AdvancedNaturalOrderComparator implements Comparator<String> {
//    @Override
//    public int compare(String s1, String s2) {
//        String[] tokens1 = s1.split("\\.");
//        String[] tokens2 = s2.split("\\.");
//
//        int len = Math.min(tokens1.length, tokens2.length);
//        for (int i = 0; i < len; i++) {
//            String token1 = tokens1[i];
//            String token2 = tokens2[i];
//
//            boolean isNumeric1 = token1.matches("\\d+");
//            boolean isNumeric2 = token2.matches("\\d+");
//
//            if (isNumeric1 && isNumeric2) {
//                // 숫자 부분을 정수로 비교
//                int num1 = Integer.parseInt(token1);
//                int num2 = Integer.parseInt(token2);
//                if (num1 != num2) {
//                    return Integer.compare(num1, num2);
//                }
//            } else {
//                // 문자열 부분을 비교
//                int cmp = token1.compareTo(token2);
//                if (cmp != 0) {
//                    return cmp;
//                }
//            }
//        }
//
//        // 토큰 수에 따라 비교
//        return Integer.compare(tokens1.length, tokens2.length);
//    }
//}
//
//
////////////////
//
//
//import org.apache.commons.collections4.ComparatorUtils;
//import org.apache.commons.collections4.comparators.NaturalOrderComparator;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class ApacheNaturalSortItemExample {
//    public static void main(String[] args) {
//        List<Item> items = new ArrayList<>();
//        items.add(new Item("artifactDescriptors.0", 10));
//        items.add(new Item("artifactDescriptors.1", 15));
//        items.add(new Item("artifactDescriptors.10", 12));
//        items.add(new Item("artifactDescriptors.2", 5));
//        items.add(new Item("artifactDescriptors.3", 8));
//        items.add(new Item("abcd ", 20));
//
//        System.out.println("초기 리스트:");
//        printList(items);
//
//        // Apache Commons NaturalOrderComparator 사용
//        Comparator<String> naturalComparator = new NaturalOrderComparator();
//        Comparator<Item> itemComparator = Comparator.comparing(Item::getName, naturalComparator)
//                .thenComparingInt(Item::getValue);
//
//        // 순수한 알파벳 문자열을 먼저 오도록 조정
//        Comparator<Item> finalComparator = (item1, item2) -> {
//            boolean item1HasDigits = item1.getName().matches(".*\\d+.*");
//            boolean item2HasDigits = item2.getName().matches(".*\\d+.*");
//
//            if (!item1HasDigits && item2HasDigits) {
//                return -1; // item1이 먼저
//            }
//            if (item1HasDigits && !item2HasDigits) {
//                return 1; // item2가 먼저
//            }
//            // 둘 다 숫자를 포함하거나 둘 다 포함하지 않으면 자연스러운 순서로 비교
//            return naturalComparator.compare(item1.getName(), item2.getName());
//        };
//
//        Collections.sort(items, finalComparator);
//
//        System.out.println("\nApache Commons NaturalOrderComparator로 정렬된 리스트:");
//        printList(items);
//    }
//
//    // 리스트 출력 메소드
//    public static void printList(List<Item> list) {
//        list.forEach(System.out::println);
//    }
//
//    // Item 클래스 정의 (위에서 설명한 대로)
//    static class Item {
//        private String name;
//        private int value;
//
//        public Item(String name, int value) {
//            this.name = name;
//            this.value = value;
//        }
//
//        // Getter 메소드
//        public String getName() {
//            return name;
//        }
//
//        public int getValue() {
//            return value;
//        }
//
//        // Setter 메소드
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setValue(int value) {
//            this.value = value;
//        }
//
//        // toString 메소드 오버라이드
//        @Override
//        public String toString() {
//            return "Item{name='" + name + "', value=" + value + '}';
//        }
//
//        // equals 및 hashCode 메소드 오버라이드
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof Item)) return false;
//
//            Item item = (Item) o;
//
//            if (value != item.value) return false;
//            return name != null ? name.equals(item.name) : item.name == null;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = name != null ? name.hashCode() : 0;
//            result = 31 * result + value;
//            return result;
//        }
//    }
//}
//
//-------------------
//
//
//
//
//        import java.util.*;
//        import java.util.regex.*;
//
public class NaturalOrderComparator implements Comparator<String> {
    private static final Pattern PATTERN = Pattern.compile("(\\D+)|(\\d+)");

    @Override
    public int compare(String s1, String s2) {
        Matcher m1 = PATTERN.matcher(s1.toLowerCase());
        Matcher m2 = PATTERN.matcher(s2.toLowerCase());

        while (m1.find() && m2.find()) {
            String part1 = m1.group();
            String part2 = m2.group();

            int result;
            if (m1.group(2) != null && m2.group(2) != null) {
                // 두 부분 모두 숫자인 경우
                Integer num1 = Integer.parseInt(part1);
                Integer num2 = Integer.parseInt(part2);
                result = num1.compareTo(num2);
            } else {
                // 문자 부분인 경우
                result = part1.compareTo(part2);
            }

            if (result != 0) {
                return result;
            }
        }

        // 남은 부분이 있는 문자열 처리
        if (m1.find()) {
            return 1;
        } else if (m2.find()) {
            return -1;
        } else {
            return 0;
        }
    }
}
