package com.leerenbo.model;

public class Organization {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toFilePath() {
        return id + "_" + name.trim().replaceAll(Activity.reg,"_") + "/";
    }
}
