package com.example.librarymanagment.controller;



import com.example.librarymanagment.dto.LoanDTO;
import com.example.librarymanagment.dto.LoanReturnDTO;
import com.example.librarymanagment.entity.Loan;
import com.example.librarymanagment.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans"  )
@Tag(name = "Loans", description = "API pour la gestion des emprunts")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Operation(summary = "Récupérer tous les emprunts")
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @Operation(summary = "Récupérer un emprunt par son ID")
    @ApiResponse(responseCode = "200", description = "Emprunt trouvé")
    @ApiResponse(responseCode = "404", description = "Emprunt non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouvel emprunt")
    @ApiResponse(responseCode = "201", description = "Emprunt créé avec succès")
    @ApiResponse(responseCode = "400", description = "Livre ou membre non trouvé, ou livre non disponible")
    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanDTO loanDTO) {
        try {
            Loan createdLoan = loanService.createLoan(loanDTO);
            return new ResponseEntity<>(createdLoan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Enregistrer le retour d'un livre")
    @ApiResponse(responseCode = "200", description = "Retour enregistré avec succès")
    @ApiResponse(responseCode = "400", description = "Livre déjà retourné")
    @ApiResponse(responseCode = "404", description = "Emprunt non trouvé")
    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id, @RequestBody LoanReturnDTO loanReturnDTO) {
        try {
            return loanService.returnBook(id, loanReturnDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer tous les emprunts en cours")
    @GetMapping("/current")
    public List<Loan> getCurrentLoans() {
        return loanService.getCurrentLoans();
    }

    @Operation(summary = "Récupérer tous les emprunts en retard")
    @GetMapping("/overdue")
    public List<Loan> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }
}