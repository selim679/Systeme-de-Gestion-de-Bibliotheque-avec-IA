package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.BookDTO;
import com.example.librarymanagment.entity.Book;
import com.example.librarymanagment.repository.AuthorRepository;
import com.example.librarymanagment.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(1L, "Titre 1", "ISBN1", LocalDate.now(), "Genre 1", 10, 10, null, null);
        book2 = new Book(2L, "Titre 2", "ISBN2", LocalDate.now(), "Genre 2", 5, 5, null, null);
    }

    @Test
    void getAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
        assertEquals(2, books.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        Optional<Book> foundBook = bookService.getBookById(1L);
        assertTrue(foundBook.isPresent());
        assertEquals("Titre 1", foundBook.get().getTitre());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void createBook() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitre("Nouveau Livre");
        bookDTO.setIsbn("NEWISBN");
        bookDTO.setNombreExemplaires(10);

        Book newBook = new Book();
        newBook.setTitre("Nouveau Livre");
        newBook.setIsbn("NEWISBN");
        newBook.setNombreExemplaires(10);
        newBook.setDisponibles(10);

        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        Book createdBook = bookService.createBook(bookDTO);
        assertNotNull(createdBook);
        assertEquals("Nouveau Livre", createdBook.getTitre());
        assertEquals(10, createdBook.getDisponibles());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void deleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        boolean deleted = bookService.deleteBook(1L);
        assertTrue(deleted);
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
}