package com.example.plugindemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        long startTime = System.currentTimeMillis();
//        long endTime = System.currentTimeMillis();
//        long costTime = endTime - startTime;
//        if(costTime > 100){
//            StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[0];//获得当前方法的StackTraceElement
//            Log.e("TimeCost", String.format(
//                    "===> %s.%s(%s:%s)方法耗时 %d ms",
//                            thisMethodStack.getClassName(), //类的全限定名称
//                            thisMethodStack.getMethodName(),//方法名
//                            thisMethodStack.getFileName(),  //类文件名称
//                            thisMethodStack.getLineNumber(),//行号
//                            costTime                        //方法耗时
//                    )
//            );
//        }
        method1();
        method2();
        method3();
    }

    private void method1(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void method2(){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void method3(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}