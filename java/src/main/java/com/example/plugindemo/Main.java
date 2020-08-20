package com.example.plugindemo;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.FileVisitor;

import static org.objectweb.asm.Opcodes.*;


public class Main {

    public static void main(String[] args){
        printClass();
       createClass();
    }

    private static void printClass(){
        try {
            ClassPrinter classPrinter = new ClassPrinter();
            ClassReader classReader = new ClassReader(OuterClass.class.getName());
            classReader.accept(classPrinter, 0);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void createClass(){
        ClassWriter classWriter = new ClassWriter(0);

        classWriter.visit(V1_7, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "com/example/plugindemo/Person", null, "java/lang/Object", null);
        classWriter.visitSource("Person.java", null);

        FieldVisitor fileVisitor = classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "NAME", "Ljava/lang/String;", null, "rain9155");
        fileVisitor.visitEnd();


        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getAge", "()I", null, null);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
    }
}