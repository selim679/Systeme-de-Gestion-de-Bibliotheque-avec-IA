package com.example.librarymanagment.repository;
import com.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Emprunts en cours (pas encore rendus)
    @Query("SELECT l FROM Loan l WHERE l.dateRetourEffective IS NULL")
    List<Loan> findCurrentLoans();

    // Emprunts en retard
    @Query("SELECT l FROM Loan l WHERE l.dateRetourEffective IS NULL " +
            "AND l.dateRetourPrevue < CURRENT_DATE")
    List<Loan> findOverdueLoans();
}
