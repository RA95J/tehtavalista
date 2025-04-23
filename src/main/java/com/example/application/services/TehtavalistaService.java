package com.example.application.services;

import com.example.application.data.Tehtavalista;
import com.example.application.data.TehtavalistaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TehtavalistaService {

    private final TehtavalistaRepository repository;

    public TehtavalistaService(TehtavalistaRepository repository) {
        this.repository = repository;
    }

    public Optional<Tehtavalista> get(Long id) {
        return repository.findById(id);
    }

    public Tehtavalista save(Tehtavalista entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Tehtavalista> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Tehtavalista> list(Pageable pageable, Specification<Tehtavalista> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
