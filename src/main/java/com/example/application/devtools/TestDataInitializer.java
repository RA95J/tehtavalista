package com.example.application.devtools;

import com.example.application.data.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class TestDataInitializer {

    @Bean
    CommandLineRunner initData(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            CommentRepository commentRepository,
            PersonRepository personRepository
    ) {
        return args -> {
            // Tehtävä 1
            Category cat1 = new Category();
            cat1.setName("Koti");
            cat1 = categoryRepository.save(cat1);

            Comment com1 = new Comment();
            com1.setComment("Imuroi nopeasti");


            Person per1 = new Person();
            per1.setName("Maija");


            Task task1 = new Task();
            task1.setTask("Imuroi olohuone");
            task1.setCategory(cat1);
            task1.setComment(com1);
            task1.setPersons(List.of(per1));
            taskRepository.save(task1);

            // Tehtävä 2
            Category cat2 = new Category();
            cat2.setName("Töissä");
            cat2 = categoryRepository.save(cat2);

            Comment com2 = new Comment();
            com2.setComment("Päivitä dokumentaatio");


            Person per2 = new Person();
            per2.setName("Pekka");


            Task task2 = new Task();
            task2.setTask("Kirjoita raportti");
            task2.setCategory(cat2);
            task2.setComment(com2);
            task2.setPersons(List.of(per2));
            taskRepository.save(task2);

            // Tehtävä 3
            Category cat3 = new Category();
            cat3.setName("Opiskelu");
            cat3 = categoryRepository.save(cat3);

            Comment com3 = new Comment();
            com3.setComment("Tenttiin valmistautuminen");


            Person per3 = new Person();
            per3.setName("Laura");


            Task task3 = new Task();
            task3.setTask("Lue kirja");
            task3.setCategory(cat3);
            task3.setComment(com3);
            task3.setPersons(List.of(per3));
            taskRepository.save(task3);

            // Tehtävä 4
            Category cat4 = new Category();
            cat4.setName("Harrastus");
            cat4 = categoryRepository.save(cat4);


            Comment com4 = new Comment();
            com4.setComment("Muista venytellä");


            Person per4 = new Person();
            per4.setName("Sami");


            Task task4 = new Task();
            task4.setTask("Juoksulenkki metsässä");
            task4.setCategory(cat4);
            task4.setComment(com4);
            task4.setPersons(List.of(per4));
            taskRepository.save(task4);

            // Tehtävä 5
            Category cat5 = new Category();
            cat5.setName("Muu");
            cat5 = categoryRepository.save(cat5);

            Comment com5 = new Comment();
            com5.setComment("Tarkista sähköpostit");


            Person per5 = new Person();
            per5.setName("Anni");


            Task task5 = new Task();
            task5.setTask("Sähköpostit läpi");
            task5.setCategory(cat5);
            task5.setComment(com5);
            task5.setPersons(List.of(per5));
            taskRepository.save(task5);
        };
    }
}
