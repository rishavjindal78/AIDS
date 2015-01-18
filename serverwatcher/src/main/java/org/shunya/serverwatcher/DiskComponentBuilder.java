package org.shunya.serverwatcher;

public class DiskComponentBuilder extends DiskComponent {
    
    public DiskComponentBuilder withName(String name){
        setName(name);
        return this;
    }

    public DiskComponentBuilder withHostname(String hostname){
        setHostname(hostname);
        return this;
    }
    
    public DiskComponentBuilder withVolume(String volume){
        setVolume(volume);
        return this;
    }

    public DiskComponentBuilder withDiskThreshold(int threshold){
        setDiskThreshold(threshold);
        return this;
    }

    public DiskComponent build(){
        return this;
    }
}
