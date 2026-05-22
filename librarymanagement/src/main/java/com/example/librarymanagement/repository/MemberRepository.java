package com.example.librarymanagement.repository;


import com.example.librarymanagement.entities.Member;
import com.example.librarymanagement.entities.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Check duplicate email
    boolean existsByEmail(String email);

    // Check duplicate email excluding current member on update
    boolean existsByEmailAndIdNot(String email, Long id);

    // Find member by email
    Optional<Member> findByEmail(String email);

    // Find all members by status
    List<Member> findAllByStatus(MemberStatus status);
}
