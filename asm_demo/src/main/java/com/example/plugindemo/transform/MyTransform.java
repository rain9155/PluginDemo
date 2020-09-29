package com.example.plugindemo.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * @author chenjianyu
 * @date 2020/8/24
 */
public class MyTransform extends Transform {

    @Override
    public String getName() {
        //Transform的名称
        return "";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        //输入类型
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        //输入的作用域
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        //是否开启增量编译
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        //通过TransformInvocation的getInputs方法获取所有输入，是一个集合，TransformInput代表一个输入
        Collection<TransformInput> transformInputs = transformInvocation.getInputs();

        //通过TransformInvocation的getOutputProvider方法获取输出的提供者，通过TransformOutputProvider可以创建Transform的输出
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        //遍历所有的输入，每一个输入里面包含jar和directory两种输入类型的文件集合
        for(TransformInput transformInput : transformInputs){
            Collection<JarInput> jarInputs = transformInput.getJarInputs();
            //遍历，处理jar文件
            for(JarInput jarInput : jarInputs){
                File dest = outputProvider.getContentLocation(
                        jarInput.getName(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR
                );
                //这里只是简单的把jar文件复制到输出位置
                FileUtils.copyFile(jarInput.getFile(), dest);
            }

            Collection<DirectoryInput> directoryInputs = transformInput.getDirectoryInputs();
            //遍历，处理文件夹中的文件
            for(DirectoryInput directoryInput : directoryInputs){
                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY
                );
                //这里只是简单的把文件夹中的所有文件递归地复制到输出位置
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }
}
