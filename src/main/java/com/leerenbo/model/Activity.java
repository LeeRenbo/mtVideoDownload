package com.leerenbo.model;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Activity {
    public static String reg = ":|/|\\\\|\\*|\\?|<|>|\\||\"|\t|\u000b";

    private Long id;
    private Long parentId;
    private String name;
    private List<Video> videos;
    private String slide;
    private Organization organization;
    private List<Activity> children;

    public List<Activity> getChildren() {
        return children;
    }

    public void setChildren(List<Activity> children) {
        this.children = children;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public String getSlide() {
        return slide;
    }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public String toFilePath() {
        return id + "_" + name.trim().replaceAll(reg,"_") + "/";
    }

    public String toPDFFileName() {
        String s = StringUtils.substringAfterLast(slide, "/");
        s = StringUtils.substringBefore(s, "?").replaceAll(reg,"_");
        if (s.endsWith(".pdf")) {
            return s;
        } else {
            return s + ".pdf";
        }
    }

    public static void main(String[] args) {
        Activity activity = new Activity();
        activity.setId(30584l);
        activity.setName("基于多目标优化遗传算法的加权混合推荐\u000b及融合标签与评分的协同过滤推荐");
        System.out.println(activity.toFilePath());
    }
}
