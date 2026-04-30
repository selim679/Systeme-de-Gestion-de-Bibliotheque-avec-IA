package com.example.librarymanagment.controller;



import com.example.librarymanagment.dto.BookDTO;
import com.example.librarymanagment.entity.Book;
import com.example.librarymanagment.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books"  )
@Tag(name = "Books", description = "API pour la gestion des livres")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Récupérer tous les livres")
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Récupérer un livre par son ID")
    @ApiResponse(responseCode = "200", description = "Livre trouvé")
    @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouveau livre")
    @ApiResponse(responseCode = "201", description = "Livre créé avec succès")
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookDTO bookDTO) {
        Book createdBook = bookService.createBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un livre existant")
    @ApiResponse(responseCode = "200", description = "Livre mis à jour")
    @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un livre")
    @ApiResponse(responseCode = "204", description = "Livre supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookService.deleteBook(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Rechercher des livres par critères")
    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Boolean disponibles) {
        return bookService.searchBooks(titre, genre, isbn, disponibles);
    }

    @Operation(summary = "Récupérer les livres les plus empruntés")
    @GetMapping("/top-borrowed")
    public List<Book> getTopBorrowedBooks() {
        return bookService.getTopBorrowedBooks();
    }
}