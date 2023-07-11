package com.user.common;

import java.util.Comparator;

public class CodeComparator implements Comparator<String> {

    @Override
    public int compare(String code1, String code2) {
        return code1.compareTo(code2);
    }
}
