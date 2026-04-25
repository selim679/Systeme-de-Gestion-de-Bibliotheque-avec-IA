package com.example.librarymanagment.service;


import com.library.dto.BookDTO;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé : " + id));
    }

    public Book createBook(BookDTO dto) {
        Book book = new Book();
        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookDTO dto) {
        Book book = getById(id);
        mapDtoToEntity(dto, book);
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String titre, String genre,
                                  String isbn, String authorName,
                                  Boolean disponible) {
        return bookRepository.searchBooks(titre, genre, isbn, authorName, disponible);
    }

    public List<Book> getTopBorrowed() {
        return bookRepository.findTopBorrowed();
    }

    private void mapDtoToEntity(BookDTO dto, Book book) {
        book.setTitre(dto.getTitre());
        book.setIsbn(dto.getIsbn());
        book.setDatePublication(dto.getDatePublication());
        book.setGenre(dto.getGenre());
        book.setNombreExemplaires(dto.getNombreExemplaires());
        book.setDisponibles(dto.getDisponibles());

        if (dto.getAuthorIds() != null) {
            List<Author> authors = authorRepository.findAllById(dto.getAuthorIds());
            book.setAuthors(authors);
        }
    }
}
