package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Comment extends AbstractEntity {

    @NotBlank(message = "Kommentti ei saa olla tyhjä")
    @Size(max = 500, message = "Kommentti saa olla enintään 500 merkkiä")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}