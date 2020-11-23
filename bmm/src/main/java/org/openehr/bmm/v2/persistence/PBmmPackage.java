package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class PBmmPackage extends PBmmPackageContainer {

    private String documentation;
    private String name;
    private List<String> classes;

    public PBmmPackage() {

    }

    public PBmmPackage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getClasses() {
        if(classes == null) {
            classes = new ArrayList<>();
        }
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }


    /**
     * Adds all classes and packages from the other package, without further checks. Mainly used in the
     * CanonicalPackageGenerator
     * @param other
     */
    public void setClassesAndPackagesFrom(PBmmPackage other) {
        setClasses(new ArrayList<>(other.getClasses()));
        CaseInsensitiveLinkedHashMap<PBmmPackage> packages = new CaseInsensitiveLinkedHashMap<>();
        packages.putAll(other.getPackages());
        //bit tricky because packages is not cloned first. However, cloning it would make the
        //CanonicalPackageGenerator contain errors.
        setPackages(packages);
    }

    public String getDocumentation() {
        return documentation;
    }

    /** perform an action first on all classes of the root package, then traverse the package tree and
     * perform on all classes
     * @param action an action, which is a consumer of two Strings: the first the packagename including "."s, the second the className
     */
    public void doRecursiveClasses(BiConsumer<String, String> action) {
        doRecursiveClasses(action, "");
    }

    private void doRecursiveClasses(BiConsumer<String, String> action, String parentPackageName) {
        String thisPackageName = parentPackageName.isEmpty() ? name : parentPackageName + BmmDefinitions.PACKAGE_NAME_DELIMITER + name;
        getClasses().forEach(bmmClass -> action.accept(thisPackageName, bmmClass));
        getPackages().forEach((key, bmmPackage) -> bmmPackage.doRecursiveClasses(action, thisPackageName));
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}

