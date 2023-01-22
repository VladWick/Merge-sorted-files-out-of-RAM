package com.vladwick.service.comparator;

import java.util.Comparator;

public class IntegerComparatorDesc implements MainComparator<Long> {

    @Override
    public int compare(Long o1, Long o2) {
        return o2.compareTo(o1);
    }

}
