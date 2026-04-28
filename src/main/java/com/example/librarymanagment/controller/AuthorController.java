package com.example.librarymanagment.controller;



import com.example.librarymanagment.dto.AuthorDTO;
import com.example.librarymanagment.entity.Author;
import com.example.librarymanagment.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors"  )
@Tag(name = "Authors", description = "API pour la gestion des auteurs")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Operation(summary = "Récupérer tous les auteurs")
    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @Operation(summary = "Récupérer un auteur par son ID")
    @ApiResponse(responseCode = "200", description = "Auteur trouvé")
    @ApiResponse(responseCode = "404", description = "Auteur non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouvel auteur")
    @ApiResponse(responseCode = "201", description = "Auteur créé avec succès")
    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody AuthorDTO authorDTO) {
        Author createdAuthor = authorService.createAuthor(authorDTO);
        return new ResponseEntity<>(createdAuthor, HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un auteur existant")
    @ApiResponse(responseCode = "200", description = "Auteur mis à jour")
    @ApiResponse(responseCode = "404", description = "Auteur non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody AuthorDTO authorDTO) {
        return authorService.updateAuthor(id, authorDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un auteur")
    @ApiResponse(responseCode = "204", description = "Auteur supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Auteur non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (authorService.deleteAuthor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Récupérer les auteurs les plus populaires")
    @GetMapping("/most-popular")
    public List<Author> getMostPopularAuthors() {
        return authorService.getMostPopularAuthors();
    }
}