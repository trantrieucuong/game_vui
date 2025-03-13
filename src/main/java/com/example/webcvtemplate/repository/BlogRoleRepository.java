package com.example.webcvtemplate.repository;

import com.example.webcvtemplate.entity.BlogRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRoleRepository extends JpaRepository<BlogRole, String> {
    Optional<BlogRole> findByRoleName(String roleName);
    @Query("SELECT br.blogRoleCode FROM BlogRole br WHERE br.roleName = :roleName")
    String findRoleCodeByRoleName(@Param("roleName") String roleName);

}
