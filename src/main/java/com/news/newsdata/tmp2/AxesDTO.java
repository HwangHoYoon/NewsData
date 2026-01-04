package com.news.newsdata.tmp2;


public class AxesDTO {
    private double authority;
    private double urgency;
    private double linkTrust;
    private double noCallback;

    public AxesDTO() {}
    public AxesDTO(double authority, double urgency, double linkTrust, double noCallback) {
        this.authority = authority;
        this.urgency = urgency;
        this.linkTrust = linkTrust;
        this.noCallback = noCallback;
    }

    // Getter / Setter
    public double getAuthority() { return authority; }
    public void setAuthority(double authority) { this.authority = authority; }
    public double getUrgency() { return urgency; }
    public void setUrgency(double urgency) { this.urgency = urgency; }
    public double getLinkTrust() { return linkTrust; }
    public void setLinkTrust(double linkTrust) { this.linkTrust = linkTrust; }
    public double getNoCallback() { return noCallback; }
    public void setNoCallback(double noCallback) { this.noCallback = noCallback; }

    // 합산/최댓값/클리핑 유틸
    public double axisSum() { return authority + urgency + linkTrust + noCallback; }
    public double axisMax() { return Math.max(Math.max(authority, urgency), Math.max(linkTrust, noCallback)); }
    public void clip() {
        authority = Math.min(1.0, Math.max(0.0, authority));
        urgency = Math.min(1.0, Math.max(0.0, urgency));
        linkTrust = Math.min(1.0, Math.max(0.0, linkTrust));
        noCallback = Math.min(1.0, Math.max(0.0, noCallback));
    }
    public void scale(double factor) {
        authority *= factor;
        urgency *= factor;
        linkTrust *= factor;
        noCallback *= factor;
    }
}
