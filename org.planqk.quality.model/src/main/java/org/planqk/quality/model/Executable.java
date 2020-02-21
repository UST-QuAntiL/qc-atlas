package org.planqk.quality.model;

public abstract class Executable {

    private String name;

    public Executable(String name){
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
