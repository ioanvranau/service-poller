package com.service.poller.model;

public class ServiceUrl {
    private String name;
    private String path;
    private String creationTime;
    private String status;

    public static ServiceUrl of(String name, String path, String date) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setName(name);
        serviceUrl.setPath(path);
        serviceUrl.setCreationTime(date);
        return serviceUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}