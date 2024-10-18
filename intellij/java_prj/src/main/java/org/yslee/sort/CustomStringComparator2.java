package org.yslee.sort;

import java.util.Comparator;
import java.util.function.Function;

public class CustomStringComparator2<T> implements Comparator<T> {

    private final Function<T, String> keyExtractor;

    /**
     * 생성자: 객체에서 비교할 문자열을 추출하는 함수 전달
     * @param keyExtractor 객체에서 비교할 문자열을 추출하는 함수
     */
    public CustomStringComparator2(Function<T, String> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    /**
     * 문자열을 전처리하여 대소문자 무시, 공백 및 특수문자 제거
     * @param s 비교할 문자열
     * @return 전처리된 문자열
     */
    private String preprocess(String s) {
        if (s == null) {
            return "";
        }
        // 대소문자 무시: 모두 소문자로 변환
        String lowerCase = s.toLowerCase();
        // 공백 및 특수문자 제거: 알파벳과 숫자만 남김
        return lowerCase.replaceAll("[^a-z0-9]", "");
    }

    @Override
    public int compare(T o1, T o2) {
        String processedS1 = preprocess(keyExtractor.apply(o1));
        String processedS2 = preprocess(keyExtractor.apply(o2));
        return processedS1.compareTo(processedS2);
    }
}

////////////

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdvancedNaturalSortExample {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("aaa.0");
        list.add("aaa.1");
        list.add("aaa.2");
        list.add("aaa.11");
        list.add("bbb.2.1");
        list.add("bbb.2.10");
        list.add("bbb.2.2");

        // 커스텀 Comparator 사용
        Collections.sort(list, new AdvancedNaturalOrderComparator());

        // 정렬 결과 출력
        for (String str : list) {
            System.out.println(str);
        }
    }
}

// 여러 숫자 부분을 처리하는 자연스러운 순서 Comparator
class AdvancedNaturalOrderComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        String[] tokens1 = s1.split("\\.");
        String[] tokens2 = s2.split("\\.");

        int len = Math.min(tokens1.length, tokens2.length);
        for (int i = 0; i < len; i++) {
            String token1 = tokens1[i];
            String token2 = tokens2[i];

            boolean isNumeric1 = token1.matches("\\d+");
            boolean isNumeric2 = token2.matches("\\d+");

            if (isNumeric1 && isNumeric2) {
                // 숫자 부분을 정수로 비교
                int num1 = Integer.parseInt(token1);
                int num2 = Integer.parseInt(token2);
                if (num1 != num2) {
                    return Integer.compare(num1, num2);
                }
            } else {
                // 문자열 부분을 비교
                int cmp = token1.compareTo(token2);
                if (cmp != 0) {
                    return cmp;
                }
            }
        }

        // 토큰 수에 따라 비교
        return Integer.compare(tokens1.length, tokens2.length);
    }
}
