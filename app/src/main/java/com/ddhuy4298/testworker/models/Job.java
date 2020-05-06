package com.ddhuy4298.testworker.models;

public class Job {

    private Integer image;
    private String service;
    private String englishService;
    private boolean checked = false;

    public Job(Integer image, String service, String englishService) {
        this.image = image;
        this.service = service;
        this.englishService = englishService;
    }

    public String getEnglishService() {
        return englishService;
    }

    public void setEnglishService(String englishService) {
        this.englishService = englishService;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
