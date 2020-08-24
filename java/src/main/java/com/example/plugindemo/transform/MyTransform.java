package com.example.plugindemo.transform;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;

import java.io.IOException;
import java.util.Set;

/**
 * @author chenjianyu
 * @date 2020/8/24
 */
public class MyTransform extends Transform {

    @Override
    public String getName() {
        //Transform的名称
        return null;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        //输入类型
        return null;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        //输入的作用域
        return null;
    }

    @Override
    public boolean isIncremental() {
        //是否开启增量编译
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation){
        //在这里处理class文件
    }
}
