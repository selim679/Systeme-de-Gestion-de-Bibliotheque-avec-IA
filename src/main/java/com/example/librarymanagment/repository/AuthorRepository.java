package com.example.librarymanagment.repository;

import com.example.librarymanagment.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a LEFT JOIN a.books b LEFT JOIN b.loans l " +
            "GROUP BY a ORDER BY COUNT(l) DESC")
    List<Author> findMostPopular();
}
