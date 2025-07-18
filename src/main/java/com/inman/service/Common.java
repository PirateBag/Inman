package com.inman.service;

public class Common {
    public static String SPACES = "    ".repeat(11 );
    public static String spacesForLevel( long level ) {
        assert level >= 0;
        assert level <= 10;
        return level == 0 ? "" : Common.SPACES.substring(1, (int) (level*4));
    }
}
