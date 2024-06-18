package com.github.rhinoarmy.cassowarry.ids;

import java.math.BigInteger;
import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IDFactoryTest {

    @Test
    public void regularNumbers() {
        System.out.println("regularNumbers");
        final int max = 100;
        final IDFactory f = new IDFactory(BigInteger.valueOf(max));
        final List<BigInteger> all = new ArrayList<>();
        for (int n = 0; n < max; n++) {
            BigInteger result = f.getIdetifierFactory().apply(BigInteger.valueOf(n));
            if (all.contains(result)) {
                fail(result.toString());
            }
            all.add(result);
            BigInteger inverse = f.getIndexFactory().apply(result);
            System.out.println(n + " -> " + result + " -> " + inverse);
        }
        assertEquals(max, all.size(), "Some items are missing");
    }

    @Test
    public void verifyStrings() {
        System.out.println("verifyStrings");
        IDFactory sut = new IDFactory(BigInteger.valueOf(Integer.MAX_VALUE));
        for (int n = 0; n < 10; n++) {
            String identifier = sut.getIdentifierStringFactory().apply(BigInteger.valueOf(n));
            BigInteger index = sut.getIndexStringFactory().apply(identifier);
            String end = sut.getIdentifierStringFactory().apply(index);
            System.out.println(n + " -> " + identifier + " -> " + index + " -> " + end);
            assertEquals(identifier, end, "Identifier not consistant");
            assertEquals(index, BigInteger.valueOf(n), "Index not consistant");
        }
    }
}
