package com.common;

public class NumberConverter {

    private static final String[] units = {"", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final String[] teens = {"", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
    private static final String[] tens = {"", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    public static String convertToWords(int number) {
        if (number == 0) {
            return "";
        }
        String txt = convertToWordsHelper(number).trim() + " only";
        return convertToTitleCase(txt);
    }

    private static String convertToWordsHelper(int number) {
        if (number < 10) {
            return units[number];
        } else if (number < 20) {
            return teens[number - 10];
        } else if (number < 100) {
            return tens[number / 10] + " " + convertToWordsHelper(number % 10);
        } else if (number < 1000) {
            return units[number / 100] + " hundred " + convertToWordsHelper(number % 100);
        } else if (number < 1000000) {
            return convertToWordsHelper(number / 1000) + " thousand " + convertToWordsHelper(number % 1000);
        } else {
            return convertToWordsHelper(number / 1000000) + " million " + convertToWordsHelper(number % 1000000);
        }
    }

    private static String convertToTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                titleCase.append(c);
                capitalizeNext = true;
            } else {
                titleCase.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            }
        }
        return titleCase.toString();
    }

}
