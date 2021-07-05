package com.example.plugindemo;

/**
 * java方法
 * @author chenjianyu
 * @date 2021/7/5
 */
public class JavaMethod {

    public static float execute(){
        try {
            method1();
            method2();
            method3();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    private static void method1() throws InterruptedException {
        Thread.sleep(500);
    }

    private static void method2() throws InterruptedException {
        Thread.sleep(300);
    }

    private static void method3() throws InterruptedException {
        Thread.sleep(1000);
    }

}
