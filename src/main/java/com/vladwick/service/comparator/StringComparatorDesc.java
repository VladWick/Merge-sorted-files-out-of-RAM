package com.vladwick.service.comparator;

public class StringComparatorDesc implements MainComparator<String> {

    @Override
    public int compare(String s1, String s2) {
        return s2.compareToIgnoreCase(s1);
    }
}
