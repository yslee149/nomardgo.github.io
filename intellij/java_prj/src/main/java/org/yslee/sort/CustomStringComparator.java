package org.yslee.sort;

import java.util.Comparator;

public class CustomStringComparator implements Comparator<String> {

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
    public int compare(String s1, String s2) {
        String processedS1 = preprocess(s1);
        String processedS2 = preprocess(s2);
        return processedS1.compareTo(processedS2);
    }
}
