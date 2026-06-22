package com.learnloop.backend.dto;

public class SharedResourceDto {
    private String sessionId;
    private String title;
    private String resourceUrl;
    private String filePath;

    public SharedResourceDto() {}

    public SharedResourceDto(String sessionId, String title, String resourceUrl, String filePath) {
        this.sessionId = sessionId;
        this.title = title;
        this.resourceUrl = resourceUrl;
        this.filePath = filePath;
    }

    public String getSessionId() { return sessionId; }
    public String getTitle() { return title; }
    public String getResourceUrl() { return resourceUrl; }
    public String getFilePath() { return filePath; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setTitle(String title) { this.title = title; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
