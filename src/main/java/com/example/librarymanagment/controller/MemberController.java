package com.example.librarymanagment.controller;

import com.example.librarymanagment.dto.MemberDTO;
import com.example.librarymanagment.entity.Member;
import com.example.librarymanagment.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members"  )
@Tag(name = "Members", description = "API pour la gestion des membres")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Operation(summary = "Récupérer tous les membres")
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @Operation(summary = "Récupérer un membre par son ID")
    @ApiResponse(responseCode = "200", description = "Membre trouvé")
    @ApiResponse(responseCode = "404", description = "Membre non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouveau membre")
    @ApiResponse(responseCode = "201", description = "Membre créé avec succès")
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody MemberDTO memberDTO) {
        Member createdMember = memberService.createMember(memberDTO);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un membre existant")
    @ApiResponse(responseCode = "200", description = "Membre mis à jour")
    @ApiResponse(responseCode = "404", description = "Membre non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        return memberService.updateMember(id, memberDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un membre")
    @ApiResponse(responseCode = "204", description = "Membre supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Membre non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (memberService.deleteMember(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}