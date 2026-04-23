package com.example.librarymanagment.repository;


import com.example.librarymanagment.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Recherche multi-critères
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE " +
            "(:titre IS NULL OR LOWER(b.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
            "(:isbn IS NULL OR b.isbn = :isbn) AND " +
            "(:authorName IS NULL OR LOWER(CONCAT(a.nom, ' ', a.prenom)) LIKE " +
            "LOWER(CONCAT('%', :authorName, '%'))) AND " +
            "(:disponible IS NULL OR (:disponible = TRUE AND b.disponibles > 0))")
    List<Book> searchBooks(@Param("titre") String titre,
                           @Param("genre") String genre,
                           @Param("isbn") String isbn,
                           @Param("authorName") String authorName,
                           @Param("disponible") Boolean disponible);

    // Top livres empruntés
    @Query("SELECT b FROM Book b LEFT JOIN b.loans l " +
            "GROUP BY b ORDER BY COUNT(l) DESC")
    List<Book> findTopBorrowed();

    // Recherche par genre (pour chatbot)
    List<Book> findByGenreContainingIgnoreCase(String genre);

    // Recherche par titre (pour chatbot)
    List<Book> findByTitreContainingIgnoreCase(String keyword);
}
