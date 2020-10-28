package com.rodolfo.ulcer.segmentation.utils;

import java.io.File;

import com.rodolfo.ulcer.segmentation.enums.MethodEnum;

public class FileUtil {

    private StringBuilder path = new StringBuilder();

    public FileUtil setFileName(String fileName) {

        this.path.append("/").append(fileName);

        return this;
    }

    public FileUtil setMethodNamePath(MethodEnum method) {

        this.path.append("/").append(method.name().toLowerCase());

        return this;
    }

    public FileUtil setDirPath(String dirPath) {

        this.path.append(dirPath);

        return this;
    }

    public File toFile() {

        return new File(this.path.toString());
    }
}
