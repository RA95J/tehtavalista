package com.example.application.data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        SELECT t FROM Task t
        LEFT JOIN t.persons p
        LEFT JOIN t.category c
        LEFT JOIN t.comment cm
        WHERE (:title IS NULL OR LOWER(t.task) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:personName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :personName, '%')))
        AND (:commentText IS NULL OR LOWER(cm.comment) LIKE LOWER(CONCAT('%', :commentText, '%')))
        AND (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%')))
    """)
    List<Task> search(
            @Param("title") String title,
            @Param("personName") String personName,
            @Param("commentText") String commentText,
            @Param("categoryName") String categoryName
    );
}
