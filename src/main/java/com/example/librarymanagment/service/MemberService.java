package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.MemberDTO;
import com.example.librarymanagment.entity.Member;
import com.example.librarymanagment.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(MemberDTO memberDTO) {
        Member member = new Member();
        member.setNom(memberDTO.getNom());
        member.setPrenom(memberDTO.getPrenom());
        member.setEmail(memberDTO.getEmail());
        member.setDateAdhesion(memberDTO.getDateAdhesion());
        return memberRepository.save(member);
    }

    public Optional<Member> updateMember(Long id, MemberDTO memberDTO) {
        return memberRepository.findById(id).map(existingMember -> {
            existingMember.setNom(memberDTO.getNom());
            existingMember.setPrenom(memberDTO.getPrenom());
            existingMember.setEmail(memberDTO.getEmail());
            existingMember.setDateAdhesion(memberDTO.getDateAdhesion());
            return memberRepository.save(existingMember);
        });
    }

    public boolean deleteMember(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }
}