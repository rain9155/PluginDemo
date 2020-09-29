package com.example.plugindemo.classvisitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 去除类的注解
 * @author chenjianyu
 * @date 2020/8/23
 */
public class RemoveAnnotationClassVisitor extends ClassVisitor implements Opcodes {

    public RemoveAnnotationClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return null;
    }


}
