package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Category extends AbstractEntity {

    @NotBlank(message = "Nimi ei saa olla tyhjä")
    @Size(max = 255, message = "Nimi saa olla enintään 255 merkkiä")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}