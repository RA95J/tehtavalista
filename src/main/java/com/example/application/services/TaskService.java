package com.example.application.services;

import com.example.application.data.Task;
import com.example.application.data.TaskRepository;
import com.example.application.data.CategoryRepository;
import com.example.application.data.CommentRepository;
import com.example.application.data.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

    public TaskService(TaskRepository taskRepository,
                       CategoryRepository categoryRepository,
                       CommentRepository commentRepository,
                       PersonRepository personRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.personRepository = personRepository;
    }

    public List<Task> list() {
        return taskRepository.findAll();
    }

    public Optional<Task> get(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public Task save(Task task) {
        // Tallennetaan kategoria, jos se on uusi
        if (task.getCategory() != null) {
            if (task.getCategory().getId() == null) {
                task.setCategory(categoryRepository.save(task.getCategory()));
            } else {
                // Päivitä olemassaoleva kategoria
                categoryRepository.findById(task.getCategory().getId()).ifPresent(existingCategory -> {
                    existingCategory.setName(task.getCategory().getName());
                    categoryRepository.save(existingCategory);
                    task.setCategory(existingCategory);
                });
            }
        }

        // Tallennetaan kommentti, jos se on uusi
        if (task.getComment() != null && task.getComment().getId() == null) {
            task.setComment(commentRepository.save(task.getComment()));
        }

        // Tallennetaan henkilöt, jos heitä on
        if (task.getPersons() != null) {
            task.setPersons(
                    task.getPersons().stream()
                            .map(person -> {
                                if (person.getId() == null) {
                                    return personRepository.save(person);
                                }
                                return person;
                            })
                            .toList()
            );
        }

        // Lopuksi tallennetaan Task itse
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> search(String title, String personName, String commentText, String categoryName) {
        return taskRepository.findAll().stream()
                .filter(task -> (title == null || title.isEmpty() || task.getTask().toLowerCase().contains(title.toLowerCase())))
                .filter(task -> (personName == null || personName.isEmpty() || task.getPersons().stream()
                        .anyMatch(p -> p.getName().toLowerCase().contains(personName.toLowerCase()))))
                .filter(task -> (commentText == null || commentText.isEmpty() ||
                        (task.getComment() != null && task.getComment().getComment().toLowerCase().contains(commentText.toLowerCase()))))
                .filter(task -> (categoryName == null || categoryName.isEmpty() ||
                        (task.getCategory() != null && task.getCategory().getName().toLowerCase().contains(categoryName.toLowerCase()))))
                .toList();
    }
}
