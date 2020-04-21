package com.github.liuzhengyang.hotreload.bytecode.util;

import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/4/21
 */
public class ClassByteCodeUtils {
    public static String getClassNameFromByteCode(byte[] bytes) {
        return null;
    }

    public static String getClassNameFromSourceCode(String sourceCode) {
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
        String className = compilationUnit.getTypes().get(0).getNameAsString();
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        boolean packagePresent = packageDeclaration.isPresent();
        if (packagePresent) {
            return packageDeclaration.get().getNameAsString() + "." + className;
        }
        return className;
    }
}
