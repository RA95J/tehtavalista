package com.example.application.data;

import com.example.application.data.Category;
import com.example.application.data.Comment;
import com.example.application.data.Person;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Task extends AbstractEntity {

    @NotBlank(message = "Tehtävän nimi ei saa olla tyhjä")
    @Size(max = 255, message = "Tehtävän nimi saa olla enintään 255 merkkiä")
    private String task;

    @ManyToOne
    @NotNull(message = "Kategoria pitää valita")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL)
    private Comment comment;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Person> persons = new ArrayList<>();

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public String getPersonNames() {
        if (persons == null || persons.isEmpty()) {
            return "";
        }
        return persons.stream()
                .map(Person::getName)
                .filter(name -> name != null && !name.isEmpty())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

}
