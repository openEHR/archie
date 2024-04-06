package org.s2.rm.base.data_types.encapsulated;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;
import org.s2.rm.base.foundation_types.primitive_types.Uri;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;

/**
* BMM name: Multimedia
* BMM ancestors: Encapsulated
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Multimedia", propOrder = {
  "alternateText",
  "data",
  "mediaType",
  "hash",
  "thumbnail",
  "size"
})
public class Multimedia extends Encapsulated {
  /**
  * BMM name: alternate_text | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "alternate_text")
  private @Nullable String alternateText;

  /**
  * BMM name: data | BMM type: Array<Byte>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "data")
  private @Nullable List<Byte> data;

  /**
  * BMM name: media_type | BMM type: Terminology_code
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "media_type")
  private TerminologyCode mediaType;

  /**
  * BMM name: hash | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "hash")
  private @Nullable String hash;

  /**
  * BMM name: thumbnail | BMM type: Multimedia
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "thumbnail")
  private @Nullable Multimedia thumbnail;

  /**
  * BMM name: size | BMM type: Integer
  * isMandatory: false | isComputed: false | isImRuntime: true | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "size")
  private int size;

  public Multimedia() {}

  public Multimedia(TerminologyCode mediaType) {
    this.mediaType = mediaType;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Multimedia otherAsMultimedia = (Multimedia) other;
    return Objects.equals(getLanguage(), otherAsMultimedia.getLanguage()) &&
      Objects.equals(getUri(), otherAsMultimedia.getUri()) &&
      Objects.equals(alternateText, otherAsMultimedia.alternateText) &&
      Objects.equals(data, otherAsMultimedia.data) &&
      Objects.equals(mediaType, otherAsMultimedia.mediaType) &&
      Objects.equals(hash, otherAsMultimedia.hash) &&
      Objects.equals(thumbnail, otherAsMultimedia.thumbnail) &&
      Objects.equals(size, otherAsMultimedia.size);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), alternateText, mediaType, hash, thumbnail, size);
    result = data == null ? 0 : 31 * result + data.hashCode();
    return result;
  }

  public @Nullable String getAlternateText() {
    return alternateText;
  }

  public void setAlternateText(@Nullable String alternateText) {
    this.alternateText = alternateText;
  }

  public @Nullable List<Byte> getData() {
    return data;
  }

  public void setData(@Nullable List<Byte> data) {
    this.data = data;
  }

  public TerminologyCode getMediaType() {
    return mediaType;
  }

  public void setMediaType(TerminologyCode mediaType) {
    this.mediaType = mediaType;
  }

  public @Nullable String getHash() {
    return hash;
  }

  public void setHash(@Nullable String hash) {
    this.hash = hash;
  }

  public @Nullable Multimedia getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(@Nullable Multimedia thumbnail) {
    this.thumbnail = thumbnail;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public String bmmClassName() {
    return "Multimedia";
  }

  @Override
  public String toString() {
    return "Multimedia";
  }
}
