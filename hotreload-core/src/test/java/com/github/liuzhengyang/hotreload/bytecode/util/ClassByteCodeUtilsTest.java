package com.github.liuzhengyang.hotreload.bytecode.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/4/21
 */
public class ClassByteCodeUtilsTest {

    @Test
    public void testGetClassNameFromSourceCode() {
        String fileContent = "package com.github.liuzhengyang.hotreload.bytecode.util;\n" +
                "\n" +
                "import java.util.Optional;\n" +
                "\n" +
                "import com.github.javaparser.StaticJavaParser;\n" +
                "import com.github.javaparser.ast.CompilationUnit;\n" +
                "import com.github.javaparser.ast.PackageDeclaration;\n" +
                "\n" +
                "/**\n" +
                " * @author liuzhengyang\n" +
                " * Make something people want.\n" +
                " * 2020/4/21\n" +
                " */\n" +
                "public class ClassByteCodeUtils {\n" +
                "    public static String getClassNameFromByteCode(byte[] bytes) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    public static String getClassNameFromSourceCode(String sourceCode) {\n" +
                "        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);\n" +
                "        String className = compilationUnit.getTypes().get(0).getNameAsString();\n" +
                "        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();\n" +
                "        boolean packagePresent = packageDeclaration.isPresent();\n" +
                "        if (packagePresent) {\n" +
                "            return packageDeclaration.get().getNameAsString() + \".\" + className;\n" +
                "        }\n" +
                "        return className;\n" +
                "    }\n" +
                "}\n";
        System.out.println(ClassByteCodeUtils.getClassNameFromSourceCode(fileContent));
    }

}
