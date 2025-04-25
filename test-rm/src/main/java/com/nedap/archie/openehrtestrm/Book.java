package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;
import java.util.List;

public class Book extends TestRMBase {
  private String author;

  private String title;

  @Nullable
  private List<Chapter> chapters;

  public String getAuthor() {
    return author;}

  public void setAuthor(String value) {
    this.author = value;
  }

  public String getTitle() {
    return title;}

  public void setTitle(String value) {
    this.title = value;
  }

  public List<Chapter> getChapters() {
    return chapters;}

  public void setChapters(List<Chapter> value) {
    this.chapters = value;
  }
}
