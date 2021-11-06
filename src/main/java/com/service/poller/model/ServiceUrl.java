package com.service.poller.model;

import java.util.Random;

public class ServiceUrl {
    private static final Random RANDOM = new Random();
    private final static String[] STATUS_LIST = new String[]{"OK", "FAIL"};
    private String name;
    private String path;
    private String status;

    public static ServiceUrl of(String name, String path) {
        ServiceUrl serviceUrl = new ServiceUrl();
        serviceUrl.setName(name);
        serviceUrl.setPath(path);
        return serviceUrl;
    }

    public static String getRandomValue() {
        final int index = RANDOM.nextInt(STATUS_LIST.length);
        return STATUS_LIST[index];
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
        return getRandomValue();
    }

    public void setStatus(String status) {
        this.status = status;
    }
}