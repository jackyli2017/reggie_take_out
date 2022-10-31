package com.jacky.test;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test1() {
        String filename = "qweasd.jpg";
        String suffix = filename.substring(filename.lastIndexOf("."));
        System.out.println(suffix);
    }
}
