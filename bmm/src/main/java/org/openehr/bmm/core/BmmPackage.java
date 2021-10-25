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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstraction of a package as a tree structure whose nodes can contain other packages and classes.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmPackage extends BmmPackageContainer implements Serializable {

    /**
     * Name of this package. This name may be qualified if it is a top-level package.
     *
     */
    private String name;

    /**
     * Classes listed as being in this package.
     */
    private Map<String, BmmClass> classes;

    /**
     * Path to root package.
     */
    private String canonicalPath = "";

    /**
     * The parent package that contains this package or null if package it a top-level package.
     */
    private BmmPackage parent;

    /**
     * No-arg constructor
     */
    public BmmPackage() {
        super();
        classes = new LinkedHashMap<String, BmmClass>();
    }

    /**
     * Constructor initializing the package name
     *
     * @param name
     */
    public BmmPackage(String name) {
        this();
        this.name = name;
    }

    /**
     * Returns the name of this package. This name may be qualified if it is a top-level package.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this package. This name may be qualified if it is a top-level package.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns classes listed as being in this package as a shallow clone.
     *
     * @return
     */
    public List<BmmClass> getClasses() {
        return new ArrayList<>(classes.values());
    }

    /**
     * Sets classes listed as being in this package.
     *
     * @param classes
     */
    public void setClasses(Map<String, BmmClass> classes) {
        this.classes = classes;
    }

    /**
     * Adds class to package.
     *
     * @param className
     * @param bmmClass
     */
    public void addClass(String className, BmmClass bmmClass) {
        this.classes.put(className, bmmClass);
    }

    /**
     * Adds class to package.
     *
     * @param bmmClass
     */
    public void addClass(BmmClass bmmClass) {
        this.classes.put(bmmClass.getName(), bmmClass);
    }

    /**
     * Obtain the set of top-level classes in this package, either from this package itself or by recursing into the
     * structure until classes are obtained from child packages. Recurse into each child only far enough to find the
     * first level of classes.
     *
     * @return
     */
    public List<BmmClass> getRootClasses() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns full path of this package back to root package.
     *
     * @return
     */
    public String getPath() {
        return this.canonicalPath;
    }

    /**
     * Sets path from root of model
     *
     * @param path
     */
    public void setPath(String path) {
        this.canonicalPath = path;
    }

    public void appendToPath(String pathComponent) {
        if(this.canonicalPath == null || this.canonicalPath.isEmpty()) {
            this.canonicalPath = pathComponent;
        } else {
            this.canonicalPath = this.canonicalPath + PACKAGE_PATH_DELIMITER + pathComponent;
        }
    }

    public boolean hasPackagePath(List<String> packagePathComponents, int currentIndex, int sizeRemaining) {
        boolean matches = false;
        if(sizeRemaining > 1) {
            if (packagePathComponents.get(currentIndex).equalsIgnoreCase(name)) {
                for(BmmPackage bmmPackage : getPackages().values()) {
                    matches = bmmPackage.hasPackagePath(packagePathComponents, currentIndex + 1, sizeRemaining - 1);
                    if(matches) {
                        break;
                    }
                }
            }
        } else if(sizeRemaining == 1) {
            if(packagePathComponents.get(currentIndex).equalsIgnoreCase(name)) {
                matches = true;
            }
        } else {
            throw new RuntimeException("Invalid size. Recursion exceeded bounds");
        }
        return matches;
    }

    /**
     * Returns the parent package or null if this package is a top-level package in the model.
     *
     * @return
     */
    public BmmPackage getParent() {
        return parent;
    }

    /**
     * Sets the parent package that contains this package.
     *
     * @param parent
     */
    public void setParent(BmmPackage parent) {
        this.parent = parent;
    }

    public String toString() {
        return name;
    }

    public String getFullPath() {
        if(parent != null) {
            return getParent().getFullPath() + "." + getName();
        }
        return getName();
    }
}
