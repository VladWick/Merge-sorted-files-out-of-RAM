package com.vladwick.service.comparator;

public class IntegerComparatorAsc implements MainComparator<Long> {

    @Override
    public int compare(Long o1, Long o2) {
        return o1.compareTo(o2);
    }
}
