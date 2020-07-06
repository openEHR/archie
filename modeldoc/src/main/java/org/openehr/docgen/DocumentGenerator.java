package org.openehr.docgen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmPackage;
import org.openehr.bmm.core.BmmPackageContainer;
import org.openehr.docgen.model.ClassDetails;
import org.openehr.docgen.model.ClassListItem;
import org.openehr.docgen.model.PackageListItem;
import org.openehr.docgen.model.PackageTreeNode;
import org.openehr.docgen.model.PropertyDetails;
import org.openehr.utils.file.FileAndDirUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
 * Created by cnanjo on 3/7/17.
 */

public class DocumentGenerator {

    public static final String ALL_CLASSES_FRAME_TEMPLATE = "allclasses-frame.ftlh";
    public static final String ALL_PACKAGES_FRAME_TEMPLATE = "overview-frame.ftlh";
    public static final String OVERVIEW_SUMMARY_TEMPLATE = "overview-summary.ftlh";
    public static final String OVERVIEW_TEMPLATE = "overview.ftlh";
    public static final String PACKAGE_DETAILS_TEMPLATE = "package_details.ftlh";
    public static final String CLASS_DETAILS_TEMPLATE = "class.ftlh";
    protected static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    private Configuration cfg;
    private String outputDirectory;
    private String supplementalFilesDirectory;

    public DocumentGenerator() {
    }
    public void generateDocument(BmmModel schema) {
        try {
            prepareDirectory();
            populateOverviewPage(schema);
            populateAllClassList(schema);
            populateAllPackagesList(schema);
            populateOverviewSummaryList(schema);
        } catch(Exception e) {
            throw new RuntimeException("Error binding root to template", e);
        }
    }

    private void prepareDirectory() {
        try {
            FileUtils.deleteDirectory(new File(outputDirectory));
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        String currentBaseDir = DocumentGenerator.class.getResource("/templates").getFile();
        File dir = new File(currentBaseDir);
        if(dir.exists()) {
            System.out.println(dir + " exists");
        }

        FileAndDirUtils.copyStreamToTargetFile("/templates/index.html", outputDirectory + "index.html");
        FileAndDirUtils.copyStreamToTargetFile("/templates/stylesheet.css", outputDirectory + "stylesheet.css");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/background.gif", outputDirectory + "resources/background.gif");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/lastnode.png", outputDirectory + "resources/lastnode.png");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/node.png", outputDirectory + "resources/node.png");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/overview.jpg", outputDirectory + "resources/overview.jpg");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/tab.gif", outputDirectory + "resources/tab.gif");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/titlebar.gif", outputDirectory + "resources/titlebar.gif");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/titlebar_end.gif", outputDirectory + "resources/titlebar_end.gif");
        FileAndDirUtils.copyStreamToTargetFile("/templates/resources/vline.png", outputDirectory + "resources/vline.png");

        FileAndDirUtils.copyDirectoryAndContent(currentBaseDir + File.separator + "supplementalFiles", outputDirectory);
    }

    public void populateOverviewPage(BmmModel schema) throws Exception {
        Template overviewTemplate = retrieveTemplate(OVERVIEW_TEMPLATE);
        Map<String, Object> root = new HashMap<>();
        root.put("stylesheet", getStylesheetPath(0));
        root.put("overview", getOverviewPath(0));
        populateTemplate(overviewTemplate, root, "overview.html");
    }

    public void populateAllClassList(BmmModel schema) throws Exception {
        Template allclasses = retrieveAllClassesTemplate();
        Map<String, Object> root = populateRootMapWithClassDefinitions(schema);
        populateTemplate(allclasses, root, "allclasses-frame.html");
    }

    public void populateOverviewSummaryList(BmmModel schema) throws Exception {
        Template allclasses = retrieveOverviewSummaryTemplate();
        Map<String, Object> root = populateRootMapWithClassDefinitions(schema);
        root.put("currentDate", getMMDDYYYYhmsFormattedDate());
        populateTemplate(allclasses, root, "overview-summary.html");
    }

    public Map<String, Object> populateRootMapWithClassDefinitions(BmmModel schema) {
        Map<String, Object> root = new HashMap<>();
        root.put("stylesheet", getStylesheetPath(0));
        List<ClassDetails> classes = new ArrayList<>();
        schema.getClassDefinitions().forEach( (bmmClassName, bmmClass) -> {
            String classDetailUri = bmmClass.getPackage().getPath().replaceAll("\\.", "/") + "/" + bmmClass.getName() + ".html";
            ClassDetails details = new ClassDetails(classDetailUri, bmmClass.getPackagePath(), bmmClass.getName());
            details.setDocumentation(revertSafeString(((BmmClass) bmmClass).getDocumentation()));
            details.setFlagClass(false);
            bmmClass.getProperties().forEach((propName, property) ->{
                if(property.getDocumentation() == null)
                    details.setFlagClass(true);
            });
            if(bmmClass.getDocumentation() == null)
                details.setFlagClass(true);
            classes.add(details);
        });
        root.put("classes", classes);
        return root;
    }

    public void populatePackageClassList(BmmModel schema, BmmPackage bmmPackage, PackageListItem packageListItem, String filePath) throws Exception {
        Template packageDetailsTemplate = retrievePackageDetailsTemplate();
        Map<String, Object> root = new HashMap<>();
        List<ClassListItem> classes = new ArrayList<>();
        Collection bmmClasses = bmmPackage.getClasses();
        bmmClasses.forEach( bmmClass -> {
            ClassListItem item = new ClassListItem(((BmmClass)bmmClass).getName() + ".html", packageListItem.getPath(), ((BmmClass)bmmClass).getName());
            classes.add(item);
            String classDetailFilePath = filePath.substring(0, filePath.lastIndexOf("/") + 1) + ((BmmClass)bmmClass).getName() + ".html";
            populateClassDetails(schema, (BmmClass)bmmClass, item, classDetailFilePath);
        });
        root.put("classes", classes);
        root.put("package", packageListItem);
        root.put("stylesheet", getStylesheetPath(packageListItem.getPath().split("\\.").length));
        populateTemplate(packageDetailsTemplate, root, filePath);
    }

    public void populateAllPackagesList(BmmModel schema) throws Exception {
        Template allPackages = retrieveAllPackagesTemplate();
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("stylesheet", getStylesheetPath(0));
        Map<String, BmmPackage> packages = collectPackages(schema);
        List<PackageListItem> packageList = new ArrayList<>();
        packages.forEach( (K, V) -> {
            PackageListItem item = buildPackageListItemFromPackage(K, V);
            packageList.add(item);
            generateFolderStructureFromPath(schema, K, V, item);
        });
        PackageTreeNode node = buildPackageTree(schema);
        root.put("packages", packageList);
        root.put("packagetree", node);
        populateTemplate(allPackages, root, "overview-frame.html");
    }

    private PackageListItem buildPackageListItemFromPackage(String path, BmmPackage bmmPackage) {
        PackageListItem item = new PackageListItem(bmmPackage.getName(), buildPackageContentPagePath(path, bmmPackage), path);
        item.setPackageInfoPath(buildPackageInfoPagePath(path,bmmPackage));
        return item;
    }

    public void populateClassDetails(BmmModel schema, BmmClass bmmClass, ClassListItem item, String classPath) {
        try {
            Template classDetails = retrieveClassDetailsTemplate();
            Map<String, Object> root = new HashMap<>();
            ClassDetails details = new ClassDetails(item);
            details.setDocumentation(revertSafeString(bmmClass.getDocumentation()));
            List<ClassDetails> ancestors = new ArrayList<>();
            Map<String, BmmClass> ancestorMap = schema.getAllAncestorClassObjects(bmmClass);
            ancestors.add(details);
            ancestorMap.forEach((K,V) -> {
                String classDetailUri = V.getPackage().getPath().replaceAll("\\.", "/") + "/" + V.getName() + ".html";
                ClassDetails ancestor = new ClassDetails(getRelativePath(classPath.split("/").length -1, classDetailUri), V.getPackagePath(), V.getName());
                V.getProperties().forEach((name, value) ->{
                    PropertyDetails propDetails = new PropertyDetails(value.getName());
                    ancestor.addProperty(propDetails);
                });
                ancestors.add(ancestor);
            });
            List<PropertyDetails> properties = new ArrayList<>();
            bmmClass.getProperties().forEach((K,V) -> {
                PropertyDetails property = new PropertyDetails(V.getName());
                property.setType(V.getType().toDisplayString());
                property.setDocumentation(revertSafeString(V.getDocumentation()));
                property.setExistence(V.getExistence().toString());
                if(V instanceof BmmContainerProperty) {
                    if(((BmmContainerProperty)V).getCardinality().getLower() != null)
                        property.setCardinality(((BmmContainerProperty) V).getCardinality().toString());
                    else {
                        System.out.println("######## INVESTIGATE " + bmmClass.getName() + "." + property.getName());
                        property.setCardinality("0..*");
                    }
                }
                else
                    property.setCardinality("N/A");

                properties.add(property);
            });
            List<ClassDetails> descendants = new ArrayList<>();
            Map<String, BmmClass> descendantMap = schema.getAllDescendantClassObjects(bmmClass);
            descendantMap.forEach((K,V) -> {
                String classDetailUri = V.getPackage().getPath().replaceAll("\\.", "/") + "/" + V.getName() + ".html";
                ClassDetails descendant = new ClassDetails(getRelativePath(classPath.split("/").length -1, classDetailUri), V.getPackagePath(), V.getName());
                descendants.add(descendant);
            });
            root.put("stylesheet", getStylesheetPath(classPath.split("/").length -1));
            root.put("overview", getOverviewPath(classPath.split("/").length -1));
            root.put("class", details);
            root.put("ancestors", ancestors);
            root.put("properties", properties);
            root.put("descendants", descendants);
            populateTemplate(classDetails, root, classPath);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating class detail page from template for class " + bmmClass.getName());
        }
    }

    public void populateTemplate(Template template, Map<String, Object> root, String outputFilePath) throws Exception {
        Writer out = new FileWriter(outputDirectory + File.separator + outputFilePath);
        template.process(root, out);
        out.close();
    }

    public Map<String, BmmPackage> collectPackages(BmmModel model) {
        Map<String, BmmPackage> packages = new LinkedHashMap<>();
            collectPackages(model, packages, "", 0);
        return packages;
    }

    protected void collectPackages(BmmPackageContainer packageContainer, Map<String, BmmPackage> packages, String path, int level) {
        Map<String, BmmPackage> bmmPackageMap = packageContainer.getPackages();
        if(bmmPackageMap != null && bmmPackageMap.size() > 0) {
            Collection bmmPackages = bmmPackageMap.values();
            bmmPackages.forEach(bmmPackage -> {
                String newpath = "";
                if(level == 0) {
                    newpath = ((BmmPackage)bmmPackage).getName();
                } else {
                    newpath = path + "." + ((BmmPackage)bmmPackage).getName();
                }
                packages.put(newpath, ((BmmPackage)bmmPackage));
                if(((BmmPackage)bmmPackage).getPackages() != null && ((BmmPackage)bmmPackage).getPackages().size() > 0) {
                    collectPackages((BmmPackage)bmmPackage, packages, newpath, level + 1);
                }
            });
        }
    }

    public PackageTreeNode buildPackageTree(BmmModel model) {
        PackageTreeNode tree = new PackageTreeNode();
        PackageListItem root = new PackageListItem();
        root.setName("model");
        root.setPath("");
        tree.setPackageItem(root);
        buildTree(model, tree, "", 0);
        return tree;
    }

    protected void buildTree(BmmPackageContainer packageContainer, PackageTreeNode tree, String path, int level) {
        Map<String, BmmPackage> bmmPackageMap = packageContainer.getPackages();
        if(bmmPackageMap != null && bmmPackageMap.size() > 0) {
            Collection bmmPackages = bmmPackageMap.values();
            bmmPackages.forEach(bmmPackage -> {
                String newpath = "";
                if(level == 0) {
                    newpath = ((BmmPackage)bmmPackage).getName();
                } else {
                    newpath = path + "." + ((BmmPackage)bmmPackage).getName();
                }
                PackageTreeNode node = new PackageTreeNode();
                PackageListItem item = buildPackageListItemFromPackage(newpath, (BmmPackage)bmmPackage);
                node.setPackageItem(item);
                tree.addChildNode(node);
                if(((BmmPackage)bmmPackage).getPackages() != null && ((BmmPackage)bmmPackage).getPackages().size() > 0) {
                    buildTree((BmmPackage)bmmPackage, node, newpath, level + 1);
                }
            });
        }
    }

    protected String buildPackageContentPagePath(String path, BmmPackage bmmPackage) {
        String outputPath = path.replaceAll("\\.", File.separator) + File.separator + bmmPackage.getName() + "_pkg.html";
        return outputPath;
    }

    protected String buildPackageInfoPagePath(String path, BmmPackage bmmPackage) {
        String outputPath = path.replaceAll("\\.", File.separator) + File.separator + bmmPackage.getName() + "_info.html";
        return outputPath;
    }

    protected void generateFolderStructureFromPath(BmmModel schema, String path, BmmPackage bmmPackage, PackageListItem item) {
        try {
            String absolutePath = outputDirectory + buildPackageContentPagePath(path, bmmPackage);
            String relativePath = buildPackageContentPagePath(path, bmmPackage);
            System.out.println(absolutePath);
            System.out.println(relativePath);
            Files.createDirectories(Paths.get(absolutePath.substring(0, absolutePath.lastIndexOf(File.separator))));
            populatePackageClassList(schema, bmmPackage, item, relativePath);
        } catch(Exception e) {
            throw new RuntimeException("Error writing package class details file", e);
        }

    }

    protected Template retrieveAllClassesTemplate() {
        return retrieveTemplate(ALL_CLASSES_FRAME_TEMPLATE);
    }

    protected Template retrieveOverviewSummaryTemplate() {
        return retrieveTemplate(OVERVIEW_SUMMARY_TEMPLATE);
    }

    protected Template retrieveAllPackagesTemplate() {
        return retrieveTemplate(ALL_PACKAGES_FRAME_TEMPLATE);
    }

    protected Template retrievePackageDetailsTemplate() {
        return retrieveTemplate(PACKAGE_DETAILS_TEMPLATE);
    }

    protected Template retrieveClassDetailsTemplate() {
        return retrieveTemplate(CLASS_DETAILS_TEMPLATE);
    }

    public Template retrieveTemplate(String templateName) {
        Template template = null;
        try {
            template = cfg.getTemplate(templateName);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error retrieving template " + templateName);
        }
        return template;
    }

    public void configure(File templateDirectory) {
        try {
            cfg = new Configuration(Configuration.VERSION_2_3_25);
            //cfg.setDirectoryForTemplateLoading(templateDirectory);

            cfg.setClassForTemplateLoading(this.getClass(), "/templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
        } catch(Exception e) {
            throw new RuntimeException("Error configuring Freemaker", e);
        }
    }

    public Configuration getCfg() {
        return cfg;
    }

    public void setCfg(Configuration cfg) {
        this.cfg = cfg;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        if(!this.outputDirectory.endsWith(File.separator)) {
            this.outputDirectory += File.separator;
        }
    }

    public String getSupplementalFilesDirectory() {
        return supplementalFilesDirectory;
    }

    public void setSupplementalFilesDirectory(String supplementalFilesDirectory) {
        this.supplementalFilesDirectory = supplementalFilesDirectory;
    }

    public String getStylesheetPath(int relativeSteps) {
        return getRelativePath(relativeSteps, "stylesheet.css");
    }

    public String getOverviewPath(int relativeSteps) {
        return getRelativePath(relativeSteps, "overview-summary.html");
    }

    protected String getRelativePath(int relativeSteps, String path) {
        String prefix = "";
        for(int i = 0; i < relativeSteps; i++) {
            prefix += ".." + File.separator;
        }
        return prefix + path;
    }

    public String getMMDDYYYYhmsFormattedDate() {
        return getCurrentTime(DocumentGenerator.defaultDateFormat);
    }

    protected String getCurrentTime(SimpleDateFormat format) {
        Date date = new Date();
        return format.format(date);
    }

    /**
     * Required to escape special BMM characters
     * @param documentation
     * @return
     */
    public static String revertSafeString(String documentation) {
        String safeString = documentation;
        safeString = safeString.replaceAll("&quot;","\"");
        safeString = safeString.replaceAll("&lt;", "<");
        safeString = safeString.replaceAll("&gt;", ">");
        safeString = safeString.replaceAll("&#47;", "/");
        return safeString;
    }
}
