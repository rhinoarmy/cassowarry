package com.github.rhinoarmy.cassowarry.ids;

import java.math.BigInteger;
import java.util.function.*;

import static java.math.BigInteger.*;

public class IDFactory {

    public static final int DEFAULT_ENCODING_RADIX = 16;

    private static final UnaryOperator<BigInteger> DEFAULT_COPRIME_FACTORY = (final BigInteger max1) -> max1.pow(2).divide(TWO).sqrt();
    private static final Function<String, BigInteger> DEFAULT_ID_DECODER = (String id) -> new BigInteger(id, DEFAULT_ENCODING_RADIX);
    private static final Function<BigInteger, String> DEFAULT_ID_ENCODER = (BigInteger id) -> id.toString(DEFAULT_ENCODING_RADIX).toUpperCase();
    private final BigInteger coPrime;
    private final BigInteger max;
    private final BigInteger modInverse;
    private final Function<BigInteger, String> encoder;
    private final Function<String, BigInteger> decoder;

    public IDFactory(BigInteger max) {
        this(max, null, null, null);
    }

    public IDFactory(
            BigInteger max,
            UnaryOperator<BigInteger> initialCoprimeFactory,
            Function<BigInteger, String> encoder,
            Function<String, BigInteger> decoder
    ) {
        if (initialCoprimeFactory == null) {
            initialCoprimeFactory = DEFAULT_COPRIME_FACTORY;
        }
        if ((encoder == null && decoder != null)
                || (decoder == null && encoder != null)) {
            throw new IllegalArgumentException("Encoder and decoder must be supplied if one is supplied");
        }
        if (encoder == null && decoder == null) {
            encoder = DEFAULT_ID_ENCODER;
            decoder = DEFAULT_ID_DECODER;
        }
        this.max = max;
        this.encoder = encoder;
        this.decoder = decoder;
        BigInteger guess = initialCoprimeFactory.apply(max);
        while (!guess.gcd(max).equals(BigInteger.ONE)) {
            guess = guess.add(ONE);
            if (guess.equals(max)) {
                guess = TWO;
            }
        }
        this.coPrime = guess;
        this.modInverse = this.coPrime.modInverse(max);
    }

    public UnaryOperator<BigInteger> getIdetifierFactory() {
        return (BigInteger index) -> {
            if (index.compareTo(max) >= 0) {
                throw new IllegalArgumentException("Index out of range");
            }
            return index.add(ONE).multiply(coPrime).mod(max);
        };
    }

    public UnaryOperator<BigInteger> getIndexFactory() {
        return (BigInteger id) -> {
            if (id.compareTo(max) >= 0) {
                throw new IllegalArgumentException("Invalid ID");
            }
            return id.multiply(modInverse).subtract(ONE).mod(max);
        };
    }

    public Function<BigInteger, String> getIdentifierStringFactory() {
        return (BigInteger index) -> encoder.apply(getIdetifierFactory().apply(index));
    }

    public Function<String, BigInteger> getIndexStringFactory() {
        return (String identifier) -> getIndexFactory().apply(decoder.apply(identifier));
    }
}
