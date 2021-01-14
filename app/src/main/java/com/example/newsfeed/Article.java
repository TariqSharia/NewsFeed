package com.example.newsfeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Article {
    private String articleID, title, description,snippet,url, image_url,language, publishDate,source,keywords,country;
    ArrayList<String> categories;

    public Article(){
    }

    public Article(String articleID, String title, String description, String snippet, String url, String image_url,
                   String language, String publishDate, String source, String keywords, ArrayList<String> categories,String country) throws ParseException {
        this.articleID = articleID;
        this.title = title;
        this.description = description;
        this.snippet = snippet;
        this.url = url;
        this.image_url = image_url;
        this.language = language;
        this.publishDate = getPublishDate(publishDate);
        this.source = source;
        this.keywords = keywords;
        this.categories = categories;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublishDate(String publishDate) throws ParseException {
        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(publishDate.split("T")[0]);
        return date.toString();
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleID='" + articleID + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", snippet='" + snippet + '\'' +
                ", url='" + url + '\'' +
                ", image_url='" + image_url + '\'' +
                ", language='" + language + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", source='" + source + '\'' +
                ", keywords=" + keywords +
                ", categories=" + categories +
                '}';
    }
}
