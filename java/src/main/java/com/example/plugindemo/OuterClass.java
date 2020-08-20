package com.example.plugindemo;

/**
 * @author chenjianyu
 * @date 2020/8/20
 */
@Deprecated
public class OuterClass{

    private int mData = 1;

    public OuterClass(int data){
        this.mData = data;
    }

    public int getData(){
        return mData;
    }

    class InnerClass{ }
}
