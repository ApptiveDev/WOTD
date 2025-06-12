package com.example.apptive_3team.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class NumberUtils {

    /**
     * double 값들의 평균을 구하는 메서드.
     *
     * @param values
     */
    public double average(double... values) {
        return Arrays.stream(values).average().orElse(0.0);
    }

    /**
     * double형 변수의 소수점 셋째자리에서 반올림하는 메서드.
     *
     * @param value
     */
    public double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
