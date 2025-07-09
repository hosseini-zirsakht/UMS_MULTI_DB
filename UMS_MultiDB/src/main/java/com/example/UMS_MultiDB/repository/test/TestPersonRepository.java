package com.example.UMS_MultiDB.repository.test;

import com.example.UMS_MultiDB.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestPersonRepository extends JpaRepository<Person, Long> {
}
