package com.gnimty.communityapiserver.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantKoreaTimeUtil {

    public static Instant getNow() {
        long epochMilli = Instant.now().plus(9, ChronoUnit.HOURS).toEpochMilli();

        Instant instant = Instant.ofEpochMilli(epochMilli);
        System.out.println("instant = " + instant);
        return instant;
    }

}
