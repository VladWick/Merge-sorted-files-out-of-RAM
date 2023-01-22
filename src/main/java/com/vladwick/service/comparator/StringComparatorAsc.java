package com.vladwick.service.comparator;

public class StringComparatorAsc implements MainComparator<String> {

    @Override
    public int compare(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
}
