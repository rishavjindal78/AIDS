package org.shunya.serverwatcher.utils;

import java.util.function.Predicate;

public class Atleast implements Predicate<Integer> {
    final int count;

    public Atleast(int count) {
        this.count = count;
    }

    @Override
    public boolean test(Integer integer) {
        return integer >= count;
    }
}
