package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.AuthorDTO;
import com.example.librarymanagment.entity.Author;
import com.example.librarymanagment.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author createAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setNom(authorDTO.getNom());
        author.setPrenom(authorDTO.getPrenom());
        author.setDateNaissance(authorDTO.getDateNaissance());
        author.setNationalite(authorDTO.getNationalite());
        return authorRepository.save(author);
    }

    public Optional<Author> updateAuthor(Long id, AuthorDTO authorDTO) {
        return authorRepository.findById(id).map(existingAuthor -> {
            existingAuthor.setNom(authorDTO.getNom());
            existingAuthor.setPrenom(authorDTO.getPrenom());
            existingAuthor.setDateNaissance(authorDTO.getDateNaissance());
            existingAuthor.setNationalite(authorDTO.getNationalite());
            return authorRepository.save(existingAuthor);
        });
    }

    public boolean deleteAuthor(Long id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Author> getMostPopularAuthors() {
        return authorRepository.findTopPopularAuthors();
    }
}