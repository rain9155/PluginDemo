package com.example.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.example.asm.TimeCostClassVisitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * Transform的模版代码
 * @author chenjianyu
 * @date 2020/7/16
 */
public class TimeCostTransform extends Transform {

    private static final String TAG = TimeCostTransform.class.getSimpleName();//类名

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        System.out.println("transform(), ---------------------------start------------------------------");

        //通过TransformInvocation的getInputs方法获取所有输入，是一个集合，TransformInput代表一个输入
        Collection<TransformInput> transformInputs = transformInvocation.getInputs();
        //通过TransformInvocation的getOutputProvider方法获取输出的提供者，通过TransformOutputProvider可以创建Transform的输出
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        //通过TransformInvocation的isIncremental方法判断本次Transform任务是否是增量，如果Transform的isIncremental方法返回false，TransformInvocation的isIncremental方法永远返回false
        boolean isIncremental = transformInvocation.isIncremental();

        System.out.println("transform(), isIncremental = " + isIncremental);

        //如果不是增量，就删除之前所有产生的输出，重头来过
        if(!isIncremental){
            outputProvider.deleteAll();
        }

        //遍历所有的输入，每一个输入里面包含jar和directory两种输入类型的文件集合
        for(TransformInput transformInput : transformInputs){
            //获取这个输入中的所有jar文件输入，JarInput代表一个jar文件输入
            Collection<JarInput> jarInputs = transformInput.getJarInputs();
            //遍历所有的jar文件输入
            for(JarInput jarInput : jarInputs){
                //判断本次Transform任务是否增量
                if(isIncremental){
                    //增量处理Jar文件
                    handleJarIncremental(jarInput, outputProvider);
                }else {
                    //非增量处理Jar文件
                    handleJar(jarInput, outputProvider);
                }
            }

            //获取这个输入中的所有directory文件输入，DirectoryInput代表一个directory文件输入
            Collection<DirectoryInput> directoryInputs = transformInput.getDirectoryInputs();
            //遍历所有的directory文件输入
            for(DirectoryInput directoryInput : directoryInputs){
                //判断本次Transform任务是否增量
                if(isIncremental){
                    //增量处理目录文件
                    handleDirectoryIncremental(directoryInput, outputProvider);
                }else {
                    //非增量处理目录文件
                    handleDirectory(directoryInput, outputProvider);
                }
            }
        }

        System.out.println("transform(), ---------------------------end------------------------------");
    }

    /**
     * 处理jar文件中的class文件，产生新的输出
     * @throws IOException
     */
    private void handleJar(JarInput jarInput, TransformOutputProvider outputProvider) throws IOException {
        //获取输入的jar文件
        File srcJar = jarInput.getFile();
        //使用TransformOutputProvider的getContentLocation方法根据输入构造输出位置
        File destJar = outputProvider.getContentLocation(
                jarInput.getName(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR
        );
        //遍历srcJar的所有内容, 在遍历的过程中把srcJar中的内容一条一条地复制到destJar
        //如果发现这个内容条目是class文件，就把它通过asm修改后再复制到destJar中
        foreachJarWithTransform(srcJar, destJar);
    }

    /**
     * (srcJar -> destJar）:
     * 遍历srcJar的所有内容，把srcJar中的内容条目一条一条地复制到destJar中,
     * 如果发现这个内容条目是class文件，就把它通过asm修改后再复制到destJar中
     * @throws IOException
     */
    private void foreachJarWithTransform(File srcJar, File destJar) throws IOException {
        try(
                JarFile srcJarFile = new JarFile(srcJar);
                JarOutputStream destJarFileOs = new JarOutputStream(new FileOutputStream(destJar))
        ){
            Enumeration<JarEntry> enumeration = srcJarFile.entries();
            //遍历srcJar中的每一条条目
            while (enumeration.hasMoreElements()){
                JarEntry entry = enumeration.nextElement();
                try(
                    //获取每一条条目的输入流
                    InputStream entryIs = srcJarFile.getInputStream(entry)
                ){
                    destJarFileOs.putNextEntry(new JarEntry(entry.getName()));
                    if(entry.getName().endsWith(".class")){//如果是class文件
                        //通过asm修改源class文件
                        ClassReader classReader = new ClassReader(entryIs);
                        ClassWriter classWriter = new ClassWriter(0);
                        TimeCostClassVisitor timeCostClassVisitor = new TimeCostClassVisitor(classWriter);
                        classReader.accept(timeCostClassVisitor, ClassReader.EXPAND_FRAMES);
                        //然后把修改后的class文件复制到destJar中
                        destJarFileOs.write(classWriter.toByteArray());
                    }else {//如果不是class文件
                        //原封不动地复制到destJar中
                        destJarFileOs.write(IOUtils.toByteArray(entryIs));
                    }
                    destJarFileOs.closeEntry();
                }
            }
        }
    }

    /**
     * 处理directory目录中的class文件，产生新的输出
     * @throws IOException
     */
    private void handleDirectory(DirectoryInput directoryInput, TransformOutputProvider outputProvider) throws IOException {
        //获取输入目录代表的File实例
        File srcDirectory = directoryInput.getFile();
        //根据输入构造输出的位置
        File destDirectory = outputProvider.getContentLocation(
                directoryInput.getName(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY
        );
        //递归地遍历srcDirectory的所有文件, 在遍历的过程中把srcDirectory中的文件逐个地复制到destDirectory，
        //如果发现这个文件是class文件，就把它通过asm修改后再复制到destDirectory中
        foreachDirectoryRecurseWithTransform(srcDirectory, destDirectory);
    }

    /**
     * (srcDirectory -> destDirectory):
     * 递归地遍历srcDirectory的所有文件, 在遍历的过程中把srcDirectory中的文件逐个地复制到
     * destDirectory，如果发现这个文件是class文件，就把它通过asm修改后再复制到destDirectory中
     * @throws IOException
     */
    private void foreachDirectoryRecurseWithTransform(File srcDirectory, File destDirectory) throws IOException {
        if(!srcDirectory.isDirectory()){
            return;
        }
        File[] files = srcDirectory.listFiles();
        for(File srcFile : files){
            if(srcFile.isFile()){
                File destFile = getDestFile(srcFile, srcDirectory, destDirectory);
                //把srcFile文件复制到destFile中，如果srcFile是class文件，则把它经过asm修改后再复制到destFile中
                transformSingleFile(srcFile, destFile);
            }else {
                //继续递归
                foreachDirectoryRecurseWithTransform(srcFile, destDirectory);
            }
        }
    }

    /**
     * 构造srcFile在destDirectory中对应的destFile
     * @throws IOException
     */
    private File getDestFile(File srcFile, File srcDirectory, File destDirectory) throws IOException {
        String srcDirPath = srcDirectory.getAbsolutePath();
        String destDirPath = destDirectory.getAbsolutePath();
        //找到源输入文件对应的输出文件位置
        String destFilePath = srcFile.getAbsolutePath().replace(srcDirPath, destDirPath);
        //构造源输入文件对应的输出文件
        File destFile = new File(destFilePath);
        FileUtils.touch(destFile);
        return destFile;
    }

    /**
     * (srcFile -> destFile)
     * 把srcFile文件复制到destFile中，如果srcFile是class文件，则把它经过asm修改后再复制到destFile中
     * @throws IOException
     */
    private void transformSingleFile(File srcFile, File destFile) throws IOException {
            try(
                InputStream srcFileIs = new FileInputStream(srcFile);
                OutputStream destFileOs = new FileOutputStream(destFile)
            ){
                if(srcFile.getName().endsWith(".class")){
                    ClassReader classReader = new ClassReader(srcFileIs);
                    ClassWriter classWriter = new ClassWriter(0);
                    TimeCostClassVisitor timeCostClassVisitor = new TimeCostClassVisitor(classWriter);
                    classReader.accept(timeCostClassVisitor, ClassReader.EXPAND_FRAMES);
                    destFileOs.write(classWriter.toByteArray());
                }else {
                    destFileOs.write(IOUtils.toByteArray(srcFileIs));
                }
            }
    }

    /**
     * 增量处理jar文件中的class文件，可能产生新的输出
     * @throws IOException
     */
    private void handleJarIncremental(JarInput jarInput, TransformOutputProvider outputProvider) throws IOException {
        //获取输入文件的状态
        Status status = jarInput.getStatus();
        //根据文件的Status做出不同的操作
        switch (status){
            case ADDED:
            case CHANGED:
                handleJar(jarInput, outputProvider);
                break;
            case REMOVED:
                //删除所有输出
                outputProvider.deleteAll();
                break;
            case NOTCHANGED:
                //do nothing
                break;
            default:
        }
    }

    /**
     * 增量处理directory目录中的class文件，可能产生新的输出
     * @throws IOException
     */
    private void handleDirectoryIncremental(DirectoryInput directoryInput, TransformOutputProvider outputProvider) throws IOException {
        //通过DirectoryInput的getChangedFiles方法获取改变过的文件集合，每一个文件对应一个Status
        Map<File, Status> changedFileMap = directoryInput.getChangedFiles();
        //遍历所有改变过的文件
        for (Map.Entry<File, Status> entry : changedFileMap.entrySet()) {
            File file = entry.getKey();
            Status status = entry.getValue();
            File destDirectory = outputProvider.getContentLocation(
                    directoryInput.getName(),
                    directoryInput.getContentTypes(),
                    directoryInput.getScopes(),
                    Format.DIRECTORY
            );
            //根据文件的Status做出不同的操作
            switch (status) {
                case ADDED:
                case CHANGED:
                    transformSingleFile(
                            file,
                            getDestFile(file, directoryInput.getFile(), destDirectory)
                    );
                    break;
                case REMOVED:
                    FileUtils.forceDelete(getDestFile(file, directoryInput.getFile(), destDirectory));
                    break;
                case NOTCHANGED:
                    //do nothing
                    break;
                default:
            }
        }
    }
}
