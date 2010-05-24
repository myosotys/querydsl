package com.mysema.query.apt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.mysema.query.apt.hibernate.HibernateAnnotationProcessor;
import com.mysema.query.apt.jdo.JDOAnnotationProcessor;
import com.mysema.query.apt.jpa.JPAAnnotationProcessor;
import com.mysema.codegen.SimpleCompiler;

public class QuerydslAnnotationProcessorTest {
    
    private static final String packagePath = "src/test/java/com/mysema/query/domain/";

    private void process(Class<? extends AbstractProcessor> processorClass, List<String> classes) throws IOException{
        File out = new File("target/" + processorClass.getSimpleName());
        FileUtils.deleteDirectory(out);
        if (!out.mkdirs()){
            Assert.fail("Creation of " + out.getPath() + " failed");
        }
        
        JavaCompiler compiler = new SimpleCompiler();
        System.out.println(compiler.getClass().getName());
        List<String> options = new ArrayList<String>(classes.size() + 3);
        options.add("-s");
        options.add("target/" + processorClass.getSimpleName());
        options.add("-proc:only");
        options.add("-processor");
        options.add(processorClass.getName());        
        options.addAll(classes);        
        int compilationResult = compiler.run(null, null, null, options.toArray(new String[options.size()]));
        if(compilationResult == 0){
            System.out.println("Compilation is successful");
        }else{
            Assert.fail("Compilation Failed");
        }
    }
    
    @Test
    public void process() throws IOException{
        File file = new File(packagePath, "AbstractEntityTest.java");
        process(QuerydslAnnotationProcessor.class, Collections.singletonList(file.getPath()));
    }
    
    @Test
    public void processAll() throws IOException{
        // works only in Eclipse for the moment
        List<String> classes = new ArrayList<String>();
        for (File file : new File(packagePath).listFiles()){
            if (file.getName().endsWith(".java")){
                classes.add(file.getPath());
            }
        }
        
        // default processor
        process(QuerydslAnnotationProcessor.class, classes);
        
        // JPA
        process(JPAAnnotationProcessor.class, classes);
        
        // Hibernate
        process(HibernateAnnotationProcessor.class, classes);
        
        // JDO
        process(JDOAnnotationProcessor.class, classes);
    }
    
}
