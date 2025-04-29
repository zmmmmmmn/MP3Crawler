package model;

/**
 * @Author: zmmmmmm
 * @Date: 2025/4/29
 * @Time: 11:11
 * @Description:
 */


public class Mp3ReSource {
    private String url;

    private String name;
    public Mp3ReSource(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
