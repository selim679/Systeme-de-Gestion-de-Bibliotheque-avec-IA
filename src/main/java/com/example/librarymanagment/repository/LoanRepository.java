package com.example.librarymanagment.repository;


import com.example.librarymanagment.entity.Author;
import com.example.librarymanagment.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByDateRetourEffectiveIsNull(); // Emprunts en cours
    List<Loan> findByDateRetourEffectiveIsNullAndDateRetourPrevueBefore(LocalDate currentDate); // Emprunts en retard

    @Query("SELECT a FROM Author a JOIN a.books b JOIN b.loans l GROUP BY a ORDER BY COUNT(l) DESC")
    List<Author> findTopPopularAuthors();
}