package com.example.plugindemo;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * @author chenjianyu
 * @date 2020/8/20
 */
public class ClassPrinter extends ClassVisitor implements Opcodes {

    public ClassPrinter() {
        super(ASM7);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + "{");
    }

    @Override
    public void visitSource(String source, String debug) {
        System.out.println(" source name = " + source);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        System.out.println(" outer class = " + name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println(" annotation = " + descriptor);
        return null;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.println(" inner class = " + name);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println(" field = "  + name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println(" method = " + name);
        return null;
    }

    @Override
    public void visitEnd() {
        System.out.println("}");
    }
}
