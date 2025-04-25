package org.openehr.utils.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.openehr.utils.operation.OperationOutcome;
import org.openehr.utils.operation.OperationOutcomeStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2017 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by cnanjo on 1/17/17.
 */

/**
 * File utility class
 */
public class FileAndDirUtils {

    /**
     * Method loads directories specified by argument. Returns list of operation outcomes consisting of
     * <ul>
     *     <li>The directory file</li>
     *     <li>An operation status flag indicating whether the loading of the directory was successful</li>
     * </ul>
     * @param directories
     * @return
     */
    public static List<OperationOutcome<File>> loadDirectories(List<String> directories) {
        List<OperationOutcome<File>> loadedDirectories = new ArrayList<>();
        directories.forEach( directoryPath -> {
            File directory = new File(directoryPath);
            if(directory.exists() && directory.isDirectory()) {
                loadedDirectories.add(new OperationOutcome<File>(directory));
            } else {
                loadedDirectories.add(new OperationOutcome<File>(null, OperationOutcomeStatus.FAILURE));
            }
        });
        return loadedDirectories;
    }

    /**
     * Method returns the list of files with the desired extension contained within the directories passed as arguments.
     *
     * @param directories The directories to search
     * @param fileFilter The filtering criterion
     * @param recursive Flag indicating whether to recurse down directories
     * @return
     */
    public static List<File> filterFilesFromDirectories(List<File> directories, IOFileFilter fileFilter, boolean recursive) {
        List<File> loadedFiles = new ArrayList<>();
        directories.forEach( dir -> {
            loadedFiles.addAll(FileUtils.listFiles(dir, fileFilter, null));
        });
        return loadedFiles;
    }

    public static List<File> loadFilesWithExtensionFromDirectoryPaths(List<File> directories, String extension, boolean recursive) {
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(extension);
            }

            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(extension);
            }
        };
        return filterFilesFromDirectories(directories, filter, recursive);
    }

    public static List<File> loadFilesWithExtensionFromDirectories(List<String> directoryPaths, String extension, boolean recursive) {
        List<File> directories = new ArrayList<>();
        List<OperationOutcome<File>> loadedDirectories = FileAndDirUtils.loadDirectories(directoryPaths);
        for(OperationOutcome<File> dir : loadedDirectories) {
            if(dir.getStatus() == OperationOutcomeStatus.SUCCESS) {
                directories.add(dir.getResult());
            }
        }
        return loadFilesWithExtensionFromDirectoryPaths(directories, extension, recursive);
    }

    public static void copyStreamToTargetFile(String sourceClassPath, String destinationFilePath) {
        InputStream is = FileAndDirUtils.class.getResourceAsStream(sourceClassPath);
        File destination = new File(destinationFilePath);
        try {
            FileUtils.copyInputStreamToFile(is, destination);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void copyFileToDirectory(String filePath, String dirPath) {
        File source = new File(filePath);
        File dest = new File(dirPath);
        try {
            FileUtils.copyFileToDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void copyFileToDirectory(File fileToCopy, String dirPath) {
        File dest = new File(dirPath);
        try {
            FileUtils.copyFileToDirectory(fileToCopy, dest);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void copyFileToDirectory(File fileToCopy, File destination) {
        try {
            FileUtils.copyFileToDirectory(fileToCopy, destination);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void copyDirectoryAndContent(String sourceDirPath, String destDirPath) {
        File source = new File(sourceDirPath);
        File dest = new File(destDirPath);
        try {
            FileUtils.copyDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = FileAndDirUtils.class.getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(resourcePath.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
