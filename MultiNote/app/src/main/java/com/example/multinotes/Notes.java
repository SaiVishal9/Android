package com.example.multinotes;

import java.io.Serializable;

public class Notes implements Serializable {

    public String title;
    public String timestamp;
    public String data;

    public String getTitle() { return title; }

    public String getTimestamp() { return timestamp; }

    public String getData() { return data; }

    public void setTitle(String title) { this.title = title; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public void setData(String data) { this.data = data; }
}
