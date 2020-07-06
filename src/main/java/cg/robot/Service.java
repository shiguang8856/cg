package cg.robot;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cg.complexobj.CodeGenContext;
import cg.complexobj.PojoSetterGenerator;
import cg.unittest.GenTestParam;
import cg.unittest.MockClzFactoryCreateResult;
import cg.unittest.MockClzFactoryGenerator;
import cg.unittest.OneTestClassCreateResult;
import cg.unittest.UnitTestCodeGeneratorDependOnClass;
import cg.util.Common;

public class Service {
    PojoSetterGenerator objSetCodeGenAfterCreateMethod;
    UnitTestCodeGeneratorDependOnClass u;
    MockClzFactoryGenerator i;

    public String genMvnInstallForJar(File jarFile, String groupId, String artifactId, String version, String pack) {
        String cmd = String.format(
                "mvn install:install-file -Dfile=%s -DgroupId=%s -DartifactId=%s -Dversion=%s -Dpackaging=%s >deploy.log",
                jarFile.getAbsolutePath(), groupId, artifactId, version, pack);
        return cmd;
    }

    public void genAllTestClssesByPackage(Class oneOfClass, File targetDirectory) throws Exception {
        CodeGenContext context = CodeGenContext.getInstance();
        u = new UnitTestCodeGeneratorDependOnClass();
        GenTestParam gtp = new GenTestParam();
        gtp.setTargetDir(targetDirectory.getAbsolutePath());
        u.genAllTestUnderPack(oneOfClass, gtp, context);
        u = null;
    }

    public void genOneTestClass(Class clzToTest, File targetDirectory) throws Exception {
        CodeGenContext context = CodeGenContext.getInstance();

        u = new UnitTestCodeGeneratorDependOnClass();
        GenTestParam gtp = new GenTestParam();
        gtp.setTargetDir(targetDirectory.getAbsolutePath());
        u.genAllTestCasesAndSave(clzToTest, gtp, context);
        u = null;
    }

    public String genTestClassSource(Class clzToTest) throws Exception {
        CodeGenContext context = CodeGenContext.getInstance();

        u = new UnitTestCodeGeneratorDependOnClass();
        GenTestParam gtp = new GenTestParam();
        OneTestClassCreateResult otccr = u.genAllTest(clzToTest, gtp, context);
        u = null;
        return otccr.getCode();
    }

    public String genCodeSetter(Class c) {
        CodeGenContext context = CodeGenContext.getInstance();
        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        List<String> concernedPacks = Common.getDefaultConcernedPacks(c);
        String str = PojoSetterGenerator.generateCodeold(c, (Class[]) null, concernedPacks, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCode(Class c) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCode(c, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCode(Class c, Class genericTypes[]) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCode(c, genericTypes, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCode(Class c, List<String> concernedPacks) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCode(c, concernedPacks, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCode(Class c, Class genericTypes[], List<String> concernedPacks) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCode(c, genericTypes, concernedPacks, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCodeWithoutRecursive(Class c) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCodeWithoutRecursive(c, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genCodeWithoutRecursive(Class c, List<String> concernedPacks) {
        CodeGenContext context = CodeGenContext.getInstance();

        objSetCodeGenAfterCreateMethod = new PojoSetterGenerator();
        String str = PojoSetterGenerator.generateCodeWithoutRecursive(c, concernedPacks, context);
        objSetCodeGenAfterCreateMethod = null;
        return str;
    }

    public String genMockClassSource(Class c) throws Exception {
        CodeGenContext context = CodeGenContext.getInstance();

        i = new MockClzFactoryGenerator();
        MockClzFactoryCreateResult mcr = i.genMockClassSource(c, context);
        i = null;
        return mcr.getCode();
    }

    public void genMock(Class clzToTest, String targetDirectory) throws Exception {
        CodeGenContext context = CodeGenContext.getInstance();

        i = new MockClzFactoryGenerator();
        i.genMock(clzToTest, targetDirectory, context);
        i = null;
    }

    public void genAllInterfaceMockUnderPack(Class oneOfClass, String targetDirectory) throws Exception {
        i = new MockClzFactoryGenerator();
        i.genAllInterfaceMockUnderPack(oneOfClass, targetDirectory);
        i = null;
    }

    static class Helper {
        private int data = 5;

        public void bump(int inc) {
            inc++;
            data = data + inc;

        }
    }

    // System.out.print
    public static void main(String[] args) {
        Double d = 1.0;
        if (d instanceof Number)
            d = d++;
        double e1 = 1.0;
        if ((Double) e1 instanceof Double | d++ == e1++)
            d += d;

        Stream.of("little", "red", "riding", "hood").parallel().map(s -> {
            System.out.println("map:" + s + " " + Thread.currentThread().getName());
            return s + "_";
        }).filter(s -> {
            System.out.println("filter: " + s + " " + Thread.currentThread().getName());
            return s.length() > 3;
        }).reduce((s1, s2) -> {
            System.out.println("reducer: " + s1 + " " + Thread.currentThread().getName());
            return s1.length() > s2.length() ? s1 : s2;
        });

        Path inputFile = Paths.get("");
        Path outputFile = Paths.get("");
        try {
            BufferedReader reader = Files.newBufferedReader(inputFile, Charset.defaultCharset());
            BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.defaultCharset());
            String line = "";
            while ((line = reader.readLine()) != null)
                writer.append(line);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Float f = new Float(23.33);
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (sb.indexOf(arg) < 1) {
                sb.append(arg + " ");
            }
        }
        List<String> list11 = new ArrayList<>();
        list11.add("Jupiter");
        list11.add("Neptune");
        list11.add("Mars");
        list11.add("Barth");
        Map<Integer, List<String>> len = list11.stream().collect(Collectors.groupingBy(p -> p.length()));

        len.forEach((l, s) -> System.out.print(l + "=" + s + " "));

    }
}
