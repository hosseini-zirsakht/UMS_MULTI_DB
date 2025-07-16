package com.example.UMS_MultiDB;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.repository.ExcelPersonRepository;
import com.example.UMS_MultiDB.service.AdminService;
import com.example.UMS_MultiDB.service.BaseService;
import com.example.UMS_MultiDB.service.impl.AdminServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class UmsMultiDbApplication {



    public static void main(String[] args) {
        SpringApplication.run(UmsMultiDbApplication.class, args);
         ExcelPersonRepository excelPersonRepository = new ExcelPersonRepository();
    }


    @Bean
    public CommandLineRunner demo(BaseService adminService) {
        return args -> {
//            adminService.deleteByNationalCode("5556565656");
        };
    }

}
