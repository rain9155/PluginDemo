package com.example.plugindemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        long startTime = System.currentTimeMillis();//start

        super.onCreate(savedInstanceState);

        long endTime = System.currentTimeMillis();//end
        long costTime = endTime - startTime;
        if(costTime > 100){
            StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[0];//获得当前方法的StackTraceElement
            Log.e("TimeCost", String.format(
                    "===> %s.%s(%s:%s)方法耗时 %d ms",
                    thisMethodStack.getClassName(), //类的全限定名称
                    thisMethodStack.getMethodName(),//方法名
                    thisMethodStack.getFileName(),  //类文件名称
                    thisMethodStack.getLineNumber(),//行号
                    costTime                        //方法耗时
                    )
            );
        }

//        try {
//            method1();
//            method2();
//            method3();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private static void method1() throws InterruptedException {
        Thread.sleep(500);
    }

    public void method2() throws InterruptedException {
        Thread.sleep(300);
    }

    void method3() throws InterruptedException {
        Thread.sleep(1000);
    }
}