package org.openehr.bmm.core;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstraction of a BMM model component that contains packages and classes.
 *
 */
public abstract class BmmPackageContainer extends BmmModelElement implements IBmmPackageContainer, Serializable {

    public static final String PACKAGE_PATH_DELIMITER = ".";

//    private String name;//TODO Potentially remove. See if this is used in the code.
    /**
     * Child packages; keys all in upper case for guaranteed matching.
     */
    private Map<String, BmmPackage> packages;

    /**
     * No-op constructor
     */
    public BmmPackageContainer() {
        super();
        packages = new LinkedHashMap<String, BmmPackage>();
    }

    /**
     * Adds a package to this BMM Package Container. Package must have a name.
     *
     * @param bmmPackage
     */
    public void addPackage(BmmPackage bmmPackage) {
        packages.put(bmmPackage.getName(), bmmPackage);
    }

    /**
     * Returns package with name argument or null if no package matches the query.
     *
     * @param packageName
     * @return
     */
    public BmmPackage getPackage(String packageName) {
        return packages.get(packageName);
    }

    /**
     * Returns list of packages contained in this model as a map.
     *
     * @return
     */
    public Map<String, BmmPackage> getPackages() {
        return this.packages;
    }

    /**
     * Package at the path `a_path'.
     * @param packagePath
     * @return
     */
    public BmmPackage packageAtPath(String packagePath) {
        String[] packagePathParsed = packagePath.split("\\" + PACKAGE_PATH_DELIMITER);
        Map<String, BmmPackage> packages = this.packages;
        BmmPackage bmmPackage = null;
        for(String packageName:packagePathParsed) {
            bmmPackage = packages.get(packageName);
            if(bmmPackage == null) {
                return null;
            }
            packages = bmmPackage.getPackages();
        }
        return bmmPackage;
    }

    /**
     * Recursively execute `action', which is a procedure taking a BMM_PACKAGE argument, on all members of packages.
     */
    public void doRecursivePackages(Consumer<BmmPackage> action) {
        packages.forEach((packageName,bmmPackage) -> {
            action.accept(bmmPackage);
            bmmPackage.doRecursivePackages(action);
        });
    }

    /**
     * True if there is a package at the path `a_path'; paths are delimited with Package_name_delimiter.
     *
     * @param packagePathString
     * @return
     */
    public boolean hasPackagePath(String packagePathString) {
       return packageAtPath(packagePathString) != null;
    }

}
