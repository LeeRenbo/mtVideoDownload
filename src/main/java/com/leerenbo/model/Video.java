package com.leerenbo.model;

public class Video {
    private Long id;
    private String name;
    private Integer reichVodid;

    public String toFileName() {
        return reichVodid + "_" + name.trim().replaceAll(Activity.reg, "_") + ".ts";
    }

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

    public Integer getReichVodid() {
        return reichVodid;
    }

    public void setReichVodid(Integer reichVodid) {
        this.reichVodid = reichVodid;
    }

}
