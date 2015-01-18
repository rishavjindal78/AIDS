package org.shunya.serverwatcher;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "name",
        "hostname",
        "volume",
        "diskThreshold"
})
public class DiskComponent {
    private String name;
    private String hostname;
    private String volume;
    private int diskThreshold;
    @XmlTransient
    private double diskUsage;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public double getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(double diskUsage) {
        this.diskUsage = diskUsage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiskThreshold() {
        return diskThreshold;
    }

    public void setDiskThreshold(int diskThreshold) {
        this.diskThreshold = diskThreshold;
    }
}