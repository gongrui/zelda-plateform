package com.blue.zelda.code.starter;

public class CheckCodeUtil {
    private static final int[] WEIGHT = {2, 3, 5, 7, 1, 2, 3, 5, 7, 1, 2, 3, 5, 7};

    public static int calcCheck(String numBody) {
        int sum = 0;
        char[] arr = numBody.toCharArray();
        for (int i = 0; i < arr.length && i < WEIGHT.length; i++) {
            sum += (arr[i] - '0') * WEIGHT[i];
        }
        return sum % 9;
    }
}
