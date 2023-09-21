package com.example.findmyway01;

public class AlertModelClass {

    String title;
    String message;
    String origin_lan;
    String origin_lat;
    String destination_lan;
    String destination_lat;

    public AlertModelClass( String title, String message, String origin_lan, String origin_lat,String destination_lan,String destination_lat) {
        this.title = title;
        this.message = message;
        this.origin_lan = origin_lan;
        this.origin_lat = origin_lat;
        this.destination_lan = destination_lan;
        this.destination_lat = destination_lat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message ) {
        this.message = message;
    }

    public String getOrigin_lan() {
        return origin_lan;
    }

    public void setOrigin_lan(String origin_lan) {
        this.origin_lan = origin_lan;
    }

    public String getOrigin_lat() {
        return origin_lat;
    }

    public void setOrigin_lat(String origin_lat) {
        this.origin_lat = origin_lat;
    }

    public String getDestination_lan() {
        return destination_lan;
    }

    public void setDestination_lan(String destination_lan) {
        this.destination_lan = destination_lan;
    }

    public String getDestination_lat() {
        return destination_lat;
    }

    public void setDestination_lat(String destination_lat) {
        this.destination_lat = destination_lat;
    }
}
