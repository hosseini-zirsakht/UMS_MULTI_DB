package com.example.UMS_MultiDB.repository.main;

import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MainPersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByNationalCode(String nationalCode);

    List<Person> getAllByStatus(Status status);

    void deleteByNationalCode(String nationalCode);

    Optional<Person> findByUsername(String username);

    @Modifying
    @Query("UPDATE Person p SET p.password = :password WHERE p.nationalCode = :nationalCode")
    int updatePasswordByNationalCode(@Param("nationalCode") String nationalCode, @Param("password") String password);

}
