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
