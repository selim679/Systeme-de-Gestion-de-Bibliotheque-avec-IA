package com.example.librarymanagment.service;

import com.example.librarymanagment.dto.LoanDTO;
import com.example.librarymanagment.entity.*;
import com.example.librarymanagment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    private static final double PENALITE_PAR_JOUR = 0.5; // 0.5 € par jour

    @Transactional
    public Loan createLoan(LoanDTO dto) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));

        if (book.getDisponibles() <= 0) {
            throw new RuntimeException("Aucun exemplaire disponible pour ce livre");
        }

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Membre non trouvé"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setDateEmprunt(LocalDate.now());
        // Date retour prévue = 14 jours par défaut
        loan.setDateRetourPrevue(LocalDate.now().plusDays(14));

        // Décrémenter le nombre d'exemplaires disponibles
        book.setDisponibles(book.getDisponibles() - 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));

        if (loan.getDateRetourEffective() != null) {
            throw new RuntimeException("Ce livre a déjà été retourné");
        }

        loan.setDateRetourEffective(LocalDate.now());

        // Calculer la pénalité si en retard
        if (LocalDate.now().isAfter(loan.getDateRetourPrevue())) {
            long joursRetard = ChronoUnit.DAYS.between(
                    loan.getDateRetourPrevue(), LocalDate.now());
            loan.setPenalite(joursRetard * PENALITE_PAR_JOUR);
        }
        // Incrémenter les exemplaires disponibles
        Book book = loan.getBook();
        book.setDisponibles(book.getDisponibles() + 1);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public List<Loan> getCurrentLoans() {
        return loanRepository.findCurrentLoans();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }
}
