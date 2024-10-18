package org.yslee.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparatorExample {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("Hello2 World!");
        strings.add("hello1-world");
        strings.add("1HELLO WORLD");
        strings.add("Hello_World");
        strings.add("Hi there");
        strings.add("hi-there!");

        // CustomStringComparator 인스턴스 생성
        CustomStringComparator customComparator = new CustomStringComparator();

        // GenericComparator 인스턴스 생성
        GenericComparator<String> genericComparator = new GenericComparator<>(customComparator);

        // 정렬 수행
        Collections.sort(strings, genericComparator);

        // 정렬 결과 출력
        for (String str : strings) {
            System.out.println(str);
        }
    }
}
