package com.youngpotato.firsttoyprojectback.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {
    Member findByUsername(String username);
}
