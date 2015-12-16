package com.test.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@JsonSerialize
public class Contact{

    @Id
    @GeneratedValue
    private Long id;
    //@Version
    //private Integer version;
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * для JPA
     */
    public Contact() {
        name = null;
    }

    @JsonCreator
    public Contact(String name) {
        this.name = name;
    }

    @JsonProperty("id")
    public Long getObjectId() {
        return id;
    }

    /*public Integer getVersion() {
        return version;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
