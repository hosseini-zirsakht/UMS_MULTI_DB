//package com.example.UMS_MultiDB.repository.test;
//
//import com.example.UMS_MultiDB.model.entity.Person;
//import com.example.UMS_MultiDB.model.enums.Status;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Collection;
//import java.util.Optional;
//
//public interface TestPersonRepository extends JpaRepository<Person, Long> {
//
//    Optional<Person> findByUsername(String username);
//
//    Optional<Person> findByNationalCode(String nationalCode);
//
//    @Modifying
//    @Query("UPDATE Person p SET p.password = :password WHERE p.nationalCode = :nationalCode")
//    int updatePasswordByNationalCode(@Param("nationalCode") String nationalCode, @Param("password") String password);
//
//    Collection<? extends Person> getAllByStatus(Status status);
//
//    void deleteByNationalCode(String nationalCode);
//
//
//}
