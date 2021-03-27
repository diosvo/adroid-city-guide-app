package com.vtmn.storage;

import java.io.File;
import java.util.ArrayList;

public class Constant {
    public static String[] videoExtensions = {
            ".mp4", ".ts", ".mkv", ".mov", ".3gp",
            ".mv2", ".m4v", "webm", ".mpeg1", ".mpeg2", ".mts",
            ".ogm", ".bup", ".dv", ".fiv", ".mlv", ".m2ts", ".mpeg4",
            ".vlc", ".3g2", ".avi", ".mpeg", ".mpg", ".wmv", ".asf"
    };

    //    All loaded files will be here
    public static  ArrayList<File> allMediaList = new ArrayList<>();
}
