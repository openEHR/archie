package com.nedap.archie.rminfo;

import java.util.Objects;

/**
 * Representation of an ID of a reference model package, with a publisher and a package. For example OpenEHR-EHR, or CIMI-CORE
 */
public class RMPackageId {
    private String publisher;
    private String aPackage;//package is reserved word in java

    public RMPackageId(String publisher, String aPackage) {
        this.publisher = publisher;
        this.aPackage = aPackage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPackage() {
        return aPackage;
    }

    public void setPackage(String aPackage) {
        this.aPackage = aPackage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMPackageId that = (RMPackageId) o;
        return Objects.equals(publisher.toLowerCase(), that.publisher.toLowerCase()) &&
                Objects.equals(aPackage.toLowerCase(), that.aPackage.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher.toLowerCase(), aPackage.toLowerCase());
    }
}
