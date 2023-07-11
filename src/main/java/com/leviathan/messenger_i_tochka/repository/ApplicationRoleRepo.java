package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRoleRepo extends JpaRepository<ApplicationRole, Integer> {
    Optional<ApplicationRole> findByName(String name);
}
