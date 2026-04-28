package com.example.librarymanagment.repository;



import com.example.librarymanagment.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitreContainingIgnoreCase(String titre);
    List<Book> findByGenreContainingIgnoreCase(String genre);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.nom) LIKE LOWER(CONCAT('%', :authorName, '%')) OR LOWER(a.prenom) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b WHERE " +
            "(:titre IS NULL OR LOWER(b.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) " +
            "AND (:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) " +
            "AND (:isbn IS NULL OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%'))) " +
            "AND (:disponibles IS NULL OR " +
            "     (:disponibles = true AND b.disponibles > 0) OR " +
            "     (:disponibles = false AND b.disponibles = 0)" +
            ")")
    List<Book> searchBooks(@Param("titre") String titre,
                           @Param("genre") String genre,
                           @Param("isbn") String isbn,
                           @Param("disponibles") Boolean disponibles);
    @Query("SELECT b FROM Book b JOIN b.loans l GROUP BY b ORDER BY COUNT(l) DESC")
    List<Book> findTopBorrowedBooks();
}