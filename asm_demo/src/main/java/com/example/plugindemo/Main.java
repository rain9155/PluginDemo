package com.example.plugindemo;

import com.android.build.api.transform.Transform;
import com.example.plugindemo.bean.OuterClass;
import com.example.plugindemo.classvisitor.PrintClassVisitor;
import com.example.plugindemo.classvisitor.RemoveAnnotationClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;


public class Main {

    public static void main(String[] args){
        printClass();
        createClass();
        transformClass();
    }

    private static void printClass(){
        try {
            PrintClassVisitor printClassVisitor = new PrintClassVisitor();
            ClassReader classReader = new ClassReader(OuterClass.class.getName());
            classReader.accept(printClassVisitor, 0);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void createClass(){
        ClassWriter classWriter = new ClassWriter(0);

        classWriter.visit(V1_7, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "com/example/plugindemo/bean/Person", null, "java/lang/Object", null);
        classWriter.visitSource("Person.java", null);

        FieldVisitor fileVisitor = classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "NAME", "Ljava/lang/String;", null, "rain9155");
        fileVisitor.visitEnd();


        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getAge", "()I", null, null);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
    }

    private static void transformClass(){
        try{
            ClassReader classReader = new ClassReader(OuterClass.class.getName());
            ClassWriter classWriter = new ClassWriter(0);
            RemoveAnnotationClassVisitor removeAnnotationClassVisitor = new RemoveAnnotationClassVisitor(classWriter);
            classReader.accept(removeAnnotationClassVisitor, 0);
            byte[] bytes = classWriter.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}