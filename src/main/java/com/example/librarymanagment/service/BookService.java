package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.BookDTO;
import com.example.librarymanagment.entity.Author;
import com.example.librarymanagment.entity.Book;
import com.example.librarymanagment.repository.AuthorRepository;
import com.example.librarymanagment.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitre(bookDTO.getTitre());
        book.setIsbn(bookDTO.getIsbn());
        book.setDatePublication(bookDTO.getDatePublication());
        book.setGenre(bookDTO.getGenre());
        book.setNombreExemplaires(bookDTO.getNombreExemplaires());
        book.setDisponibles(bookDTO.getNombreExemplaires()); // Au début, tous sont disponibles

        if (bookDTO.getAuthorIds() != null && !bookDTO.getAuthorIds().isEmpty()) {
            Set<Author> authors = bookDTO.getAuthorIds().stream()
                    .map(authorRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            book.setAuthors(authors);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public Optional<Book> updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id).map(existingBook -> {
            existingBook.setTitre(bookDTO.getTitre());
            existingBook.setIsbn(bookDTO.getIsbn());
            existingBook.setDatePublication(bookDTO.getDatePublication());
            existingBook.setGenre(bookDTO.getGenre());
            existingBook.setNombreExemplaires(bookDTO.getNombreExemplaires());
            // Logique pour ajuster 'disponibles' si 'nombreExemplaires' change, à affiner si besoin
            existingBook.setDisponibles(bookDTO.getDisponibles());

            if (bookDTO.getAuthorIds() != null) {
                Set<Author> authors = bookDTO.getAuthorIds().stream()
                        .map(authorRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                existingBook.setAuthors(authors);
            }

            return bookRepository.save(existingBook);
        });
    }

    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Book> searchBooks(String titre, String genre, String isbn, Boolean disponibles) {
        return bookRepository.searchBooks(titre, genre, isbn, disponibles);
    }

    public List<Book> getTopBorrowedBooks() {
        return bookRepository.findTopBorrowedBooks();
    }
}