package com.oaei2017.others;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;

public class FileUtil {

    /**
     * A recursive method for collecting a list of all files with a given file
     * extension. Will scan all sub-folders recursively.
     *
     * @param startFolder - where to start from
     * @param collectedFilesList - collected list of <File>
     * @param fileExtFilter
     * @param recurseFolders
     * @throws java.io.IOException
     */

    public static void collectFilesList(String startFolder, List<File> collectedFilesList, String fileExtFilter, boolean recurseFolders) throws IOException {
        File file = new File(startFolder);
        File[] filesList = file.listFiles();

        for (File f : filesList) {
            if (f.isDirectory() && recurseFolders) {
                collectFilesList(f.getAbsolutePath(), collectedFilesList, fileExtFilter, recurseFolders);
            } else //no filter
                if (fileExtFilter.isEmpty() || fileExtFilter.equals("*")) {
                    collectedFilesList.add(f);
                } else if (fileExtFilter.equalsIgnoreCase(getFileExtension(f))) {
                    collectedFilesList.add(f);
                }
        }
    }

    private static String getFileExtension(File f) {
        String fileName = f.getName();
        String fileExtension = fileName;

        int lastPos = fileName.lastIndexOf('.');

        if (lastPos > 0 && lastPos < (fileName.length() - 1)) {
            fileExtension = fileName.substring(lastPos + 1).toLowerCase();
        }

        return fileExtension;
    }

    public static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

}