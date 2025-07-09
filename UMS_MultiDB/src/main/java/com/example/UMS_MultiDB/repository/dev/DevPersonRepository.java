package com.example.UMS_MultiDB.repository.dev;

import com.example.UMS_MultiDB.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevPersonRepository extends JpaRepository<Person, Long> {
}
