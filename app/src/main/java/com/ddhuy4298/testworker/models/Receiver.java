package com.ddhuy4298.testworker.models;

import com.google.gson.annotations.SerializedName;

public class Receiver {
    @SerializedName("to")
    private String to;
    @SerializedName("notification")
    private Notification notification;

    public Receiver() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
