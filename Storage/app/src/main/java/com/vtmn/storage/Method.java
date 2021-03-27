package com.vtmn.storage;

import java.io.File;

public class Method {
    // Load files from all storage areas
    public static void load_Directory_files(File directory) {
        File[] fileList = directory.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    load_Directory_files(fileList[i]);
                } else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension : Constant.videoExtensions) {
                        //  Check type of file
                        if (name.endsWith(extension)) {
                            Constant.allMediaList.add(fileList[i]);
                            break;
                        }
                    }
                }
            }
        }
    }
}
