package com.example.UMS_MultiDB.repository.main;

import com.example.UMS_MultiDB.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainPersonRepository extends JpaRepository<Person, Long> {
}
