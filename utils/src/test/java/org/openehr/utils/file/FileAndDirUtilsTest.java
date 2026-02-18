package org.openehr.utils.file;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Test;
import org.openehr.utils.operation.OperationOutcome;
import org.openehr.utils.operation.OperationOutcomeStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
@Deprecated
public class FileAndDirUtilsTest {
    @Test
    public void loadDirectories() throws Exception {
        List<String> directories = new ArrayList<String>();
        File file = new File(".");
        directories.add(file.getCanonicalPath());
        directories.add(file.getCanonicalPath());
        directories.add(file.getCanonicalPath() + File.separator + "somedirthatdoesnotexist");
        System.out.println(file.getCanonicalPath());
        List<OperationOutcome<File>> loadedDirectories = FileAndDirUtils.loadDirectories(directories);
        assertEquals(3, loadedDirectories.size());
        int successCount = 0;
        for(OperationOutcome<File> dir : loadedDirectories) {
            if(dir.getStatus() == OperationOutcomeStatus.SUCCESS) {
                successCount++;
                if(!dir.getResult().isDirectory()) {
                    fail();
                }
            }
        }
        assertEquals(2, successCount);
    }

    @Test
    public void filterFilesFromDirectoriesTest1() throws Exception {
        File containerDir = new File("src/test/resources/");
        List<String> directories = new ArrayList<>();
        directories.add(containerDir.getAbsolutePath());
        System.out.println(containerDir.getAbsolutePath());
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(".baz");
            }

            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".baz");
            }
        };
        List<OperationOutcome<File>> loadedDirectories = FileAndDirUtils.loadDirectories(directories);
        List<File> folders = new ArrayList<>();
        loadedDirectories.forEach( oo -> {
            folders.add(oo.getResult());
        });
        List<File> filteredFiles = FileAndDirUtils.filterFilesFromDirectories(folders, filter, false);
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void filterFilesFromDirectoriesTest2() throws Exception {
        File containerDir = new File("src/test/resources/");
        List<String> directories = new ArrayList<>();
        directories.add(containerDir.getAbsolutePath());
        System.out.println(containerDir.getAbsolutePath());
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(".foo");
            }

            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".baz");
            }
        };
        List<OperationOutcome<File>> loadedDirectories = FileAndDirUtils.loadDirectories(directories);
        List<File> folders = new ArrayList<>();
        loadedDirectories.forEach( oo -> {
            folders.add(oo.getResult());
        });
        List<File> filteredFiles = FileAndDirUtils.filterFilesFromDirectories(folders, filter, false);
        assertEquals(1, filteredFiles.size());
    }

    @Test
    public void loadFilesWithExtensionFromDirectoryPathsTest() throws Exception {
        File containerDir = new File("src/test/resources/");
        List<String> directories = new ArrayList<>();
        directories.add(containerDir.getAbsolutePath());
        List<OperationOutcome<File>> loadedDirectories = FileAndDirUtils.loadDirectories(directories);
        List<File> folders = new ArrayList<>();
        loadedDirectories.forEach( oo -> {
            folders.add(oo.getResult());
        });
        List<File> filteredFiles = FileAndDirUtils.loadFilesWithExtensionFromDirectoryPaths(folders, "baz", false);
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void loadFilesWithExtensionFromDirectories() throws Exception {
        File containerDir = new File("src/test/resources/");
        List<String> directories = new ArrayList<>();
        directories.add(containerDir.getAbsolutePath());
        List<File> filteredFiles = FileAndDirUtils.loadFilesWithExtensionFromDirectories(directories, "baz", false);
        assertEquals(2, filteredFiles.size());
    }
}