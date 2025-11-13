package com.apexprint.apexprint.dto;

public class ReportRequest {
    private String jrxml;
    private String xmlData;
    private String format = "pdf";

    public String getJrxml() {
        return jrxml;
    }

    public void setJrxml(String jrxml) {
        this.jrxml = jrxml;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
