package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.LoanDTO;
import com.example.librarymanagment.dto.LoanReturnDTO;
import com.example.librarymanagment.entity.Book;
import com.example.librarymanagment.entity.Loan;
import com.example.librarymanagment.entity.Member;
import com.example.librarymanagment.repository.BookRepository;
import com.example.librarymanagment.repository.LoanRepository;
import com.example.librarymanagment.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    @Transactional
    public Loan createLoan(LoanDTO loanDTO) {
        Book book = bookRepository.findById(loanDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        Member member = memberRepository.findById(loanDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Membre non trouvé"));

        if (book.getDisponibles() <= 0) {
            throw new RuntimeException("Livre non disponible pour l'emprunt");
        }

        book.setDisponibles(book.getDisponibles() - 1);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setDateEmprunt(LocalDate.now());
        loan.setDateRetourPrevue(LocalDate.now().plusWeeks(2)); // Exemple: 2 semaines

        return loanRepository.save(loan);
    }

    @Transactional
    public Optional<Loan> returnBook(Long loanId, LoanReturnDTO loanReturnDTO) {
        return loanRepository.findById(loanId).map(loan -> {
            if (loan.getDateRetourEffective() != null) {
                throw new RuntimeException("Ce livre a déjà été retourné.");
            }

            loan.setDateRetourEffective(loanReturnDTO.getDateRetourEffective());

            // Calcul des pénalités
            if (loan.getDateRetourEffective().isAfter(loan.getDateRetourPrevue())) {
                long joursRetard = java.time.temporal.ChronoUnit.DAYS.between(loan.getDateRetourPrevue(), loan.getDateRetourEffective());
                loan.setPenalite(joursRetard * 0.5); // Exemple: 0.5€ par jour de retard
            }

            // Incrémenter le nombre de livres disponibles
            Book book = loan.getBook();
            book.setDisponibles(book.getDisponibles() + 1);
            bookRepository.save(book);

            return loanRepository.save(loan);
        });
    }

    public List<Loan> getCurrentLoans() {
        return loanRepository.findByDateRetourEffectiveIsNull();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findByDateRetourEffectiveIsNullAndDateRetourPrevueBefore(LocalDate.now());
    }
}