package com.example.librarymanagment.repository;


import com.example.librarymanagment.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Author a ORDER BY a.id DESC")
    List<Author> findTopPopularAuthors();
}