/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件/目录扫描类
 *
 * @author zhuxiaoyan
 */
public class FileScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanner.class);

    private FileScanner() {

    }

    public static void main(String[] args) {
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add("xml");
        fileTypes.add("xls");
        List files = scan("config/ota", fileTypes,
                new FileVisitor() {
                    public void visit(File file) {
                        System.out.println(file.getAbsolutePath());
                    }
                }
        );
        int size = files.size();
        System.out.println(size);
    }

    /**
     * 扫描类路径下所有指定类型的文件
     *
     * @param classPath 类路径
     * @param fileTypes 文件类型(后缀名)集合
     * @param visitor   对扫描到的文件进行访问
     * @return 查找到的文件, 如果没找到可为null或空集合
     */
    public static List<File> scan(String classPath, List<String> fileTypes, FileVisitor visitor) {
        List<File> fileList = null;
        try {
            URL dirUrl = Thread.currentThread().getContextClassLoader().getResource(classPath);
            File file = new File(dirUrl.toURI());

            fileList = new ArrayList<File>();

            //convert types to pattern
            Pattern pattern = null;
            if (fileTypes != null && fileTypes.size() > 0) {
                pattern = createPattern(fileTypes);
            }

            findFiles(file, fileList, pattern, visitor);

        } catch (URISyntaxException use) {
            LOGGER.error("wrong classpath", use);
            return null;
        }
        return fileList;
    }

    /**
     * 查找目录下所有指定类型的文件,含子目录
     * @param classPath
     * @param fileTypes
     * @return
     */
    public static List<File> scan(String classPath, List<String> fileTypes) {
        return scan(classPath, fileTypes, null);
    }

    /**
     * 查找目录下所有文件,含子目录
     * @param classPath
     * @return
     */
    public static List<File> scan(String classPath) {
        return scan(classPath, null, null);
    }

    private static void findFiles(File file, List<File> fileList, Pattern pattern, FileVisitor visitor) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                findFiles(subFile, fileList, pattern, visitor);
            }
        } else {
            if (pattern == null || checkFileSuffix(file, pattern)) {
                if (visitor != null) {
                    visitor.visit(file);
                }
                fileList.add(file);
            }
        }
    }

    private static Pattern createPattern(List<String> fileTypes) {
        StringBuilder sb = new StringBuilder("(");
        for (String type : fileTypes) {
            sb.append(".*\\.").append(type.toLowerCase()).append("|");
        }
        sb.deleteCharAt(sb.length() - 1).append(")$");
        return Pattern.compile(sb.toString());
    }

    private static boolean checkFileSuffix(File file, Pattern pattern) {
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }

    public static interface FileVisitor {
        void visit(File file);
    }
}