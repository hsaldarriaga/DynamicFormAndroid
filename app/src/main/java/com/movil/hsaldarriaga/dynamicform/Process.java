package com.movil.hsaldarriaga.dynamicform;

/**
 * Created by hass-pc on 21/05/2015.
 */
public class Process {
    public long process_id;
    public String name;
    public String description;

    public Process(long process_id, String name, String description) {
        this.process_id = process_id;
        this.name = name;
        this.description = description;
    }
}
