//package com.example.UMS_MultiDB.service.impl;
//
//import com.example.UMS_MultiDB.model.entity.Person;
//import com.example.UMS_MultiDB.model.enums.Role;
//import com.example.UMS_MultiDB.model.enums.Status;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//public class ExcelService {
//
//    private static final String EXCEL_FILENAME = "persons.xlsx";
//    private final String excelFilePath;
//
//    public ExcelService() {
//        // مسیر ذخیره‌سازی در پوشه پروژه
//        this.excelFilePath = Paths.get("D:\\project\\UMS_MultiDB-2", EXCEL_FILENAME).toString();
//        initializeExcelFile();
//        System.out.println("فایل اکسل در مسیر زیر ایجاد/بارگذاری شد: " + this.excelFilePath);
//    }
//
//    private void initializeExcelFile() {
//        try {
//            File excelFile = new File(excelFilePath);
//
//            // ایجاد پوشه پروژه اگر وجود ندارد
//            if (!excelFile.getParentFile().exists()) {
//                boolean dirsCreated = excelFile.getParentFile().mkdirs();
//                if (!dirsCreated) {
//                    throw new IOException("امکان ایجاد پوشه‌های مورد نظر وجود ندارد");
//                }
//            }
//
//            // ایجاد فایل با هدر اگر وجود ندارد
//            if (!excelFile.exists()) {
//                try (Workbook workbook = new XSSFWorkbook();
//                     FileOutputStream out = new FileOutputStream(excelFile)) {
//
//                    Sheet sheet = workbook.createSheet("Persons");
//                    createHeaderRow(sheet);
//                    workbook.write(out);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("خطا در راه‌اندازی فایل اکسل", e);
//        }
//    }
//
//    public List<Person> findAll() {
//        List<Person> persons = new ArrayList<>();
//
//        try (FileInputStream file = new FileInputStream(excelFilePath);
//             Workbook workbook = new XSSFWorkbook(file)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // رد کردن هدر
//
//                Person person = mapRowToPerson(row);
//                if (person != null) {
//                    persons.add(person);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("خطا در خواندن از فایل اکسل", e);
//        }
//
//        return persons;
//    }
//
//    public Person save(Person person) {
//        File excelFile = new File(excelFilePath);
//
//        // خواندن کل محتوا
//        Workbook workbook;
//        try (FileInputStream fis = new FileInputStream(excelFile)) {
//            workbook = new XSSFWorkbook(fis);
//        } catch (IOException e) {
//            throw new RuntimeException("خطا در خواندن فایل اکسل", e);
//        }
//
//        // ویرایش داده
//        Sheet sheet = workbook.getSheetAt(0);
//        Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
//        mapPersonToRow(person, newRow);
//
//        // ذخیره تغییرات
//        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
//            workbook.write(fos);
//        } catch (IOException e) {
//            throw new RuntimeException("خطا در نوشتن فایل اکسل", e);
//        } finally {
//            try {
//                workbook.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return person;
//    }
//
//    private void createHeaderRow(Sheet sheet) {
//        Row headerRow = sheet.createRow(0);
//        String[] headers = {
//                "نام", "نام خانوادگی", "کد ملی",
//                "شماره تلفن", "نام کاربری", "رمز عبور",
//                "ایمیل", "وضعیت", "نقش"
//        };
//
//        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
//
//        for (int i = 0; i < headers.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(headers[i]);
//            cell.setCellStyle(headerStyle);
//        }
//    }
//
//    private CellStyle createHeaderStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        font.setColor(IndexedColors.WHITE.getIndex());
//        style.setFont(font);
//        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        return style;
//    }
//
//    private Person mapRowToPerson(Row row) {
//        try {
//            return Person.builder()
//                    .firstName(getStringCellValue(row, 0))
//                    .lastName(getStringCellValue(row, 1))
//                    .nationalCode(getStringCellValue(row, 2))
//                    .phoneNumber(getStringCellValue(row, 3))
//                    .username(getStringCellValue(row, 4))
//                    .password(getStringCellValue(row, 5))
//                    .email(getStringCellValue(row, 6))
//                    .status(Status.valueOf(getStringCellValue(row, 7)))
//                    .role(Role.valueOf(getStringCellValue(row, 8)))
//                    .build();
//        } catch (Exception e) {
//            System.err.println("خطا در تبدیل ردیف به شیء Person: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private void mapPersonToRow(Person person, Row row) {
//        row.createCell(0).setCellValue(Objects.toString(person.getFirstName(), ""));
//        row.createCell(1).setCellValue(Objects.toString(person.getLastName(), ""));
//        row.createCell(2).setCellValue(Objects.toString(person.getNationalCode(), ""));
//        row.createCell(3).setCellValue(Objects.toString(person.getPhoneNumber(), ""));
//        row.createCell(4).setCellValue(Objects.toString(person.getUsername(), ""));
//        row.createCell(5).setCellValue(Objects.toString(person.getPassword(), ""));
//        row.createCell(6).setCellValue(Objects.toString(person.getEmail(), ""));
//        row.createCell(7).setCellValue(person.getStatus() != null ? person.getStatus().name() : "");
//        row.createCell(8).setCellValue(person.getRole() != null ? person.getRole().name() : "");
//    }
//
//    private String getStringCellValue(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                return String.valueOf((int) cell.getNumericCellValue());
//            default:
//                return "";
//        }
//    }
//
//    public String getExcelFilePath() {
//        return this.excelFilePath;
//    }
//
//    public void clearAllData() {
//        try (Workbook workbook = new XSSFWorkbook();
//             FileOutputStream out = new FileOutputStream(excelFilePath)) {
//
//            Sheet sheet = workbook.createSheet("Persons");
//            createHeaderRow(sheet);
//            out.flush();
//        } catch (IOException e) {
//            throw new RuntimeException("خطا در پاک کردن داده‌های فایل اکسل", e);
//        }
//    }
//}