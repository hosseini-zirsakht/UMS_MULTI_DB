package com.example.UMS_MultiDB.repository;

import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.model.enums.Role;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ExcelPersonRepository {

    private static final String FILE_PATH = "D:\\project\\UMS_MultiDB-2\\users.xlsx";
    private static final String SHEET_NAME = "Users";
    private static final Status DEFAULT_STATUS = Status.ACTIVE;

    public Person save(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        if (person.getStatus() == null) {
            person.setStatus(DEFAULT_STATUS);
        }

        try (InputStream inputStream = new FileInputStream(FILE_PATH);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                sheet = workbook.createSheet(SHEET_NAME);
                createHeaderRow(sheet, workbook);
            }

            Optional<Integer> userRow = findRowByNationalCode(sheet, person.getNationalCode());

            if (userRow.isPresent()) {
                updateUserRow(sheet, userRow.get(), person, workbook);
            } else {
                addNewUserRow(sheet, person, workbook);
            }

            saveWorkbook(workbook);
            return person;
        } catch (FileNotFoundException e) {
            return createNewExcelFile(person);
        } catch (IOException e) {
            throw new RuntimeException("Error saving user to Excel file", e);
        }
    }

    private Person createNewExcelFile(Person user) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            createHeaderRow(sheet, workbook);
            addNewUserRow(sheet, user, workbook);
            saveWorkbook(workbook);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("Error creating new Excel file", e);
        }
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);

        String[] headers = {
                "National Code", "Username", "Password", "Email",
                "Status", "First Name", "Last Name", "Phone Number", "Role"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
    }

    private void addNewUserRow(Sheet sheet, Person user, Workbook workbook) {
        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.createRow(lastRowNum + 1);
        fillUserRow(row, user, workbook);
    }

    private void updateUserRow(Sheet sheet, int rowNum, Person user, Workbook workbook) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        fillUserRow(row, user, workbook);
    }

    private void fillUserRow(Row row, Person user, Workbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        safeSetCellValue(row, 0, user.getNationalCode(), dataStyle);
        safeSetCellValue(row, 1, user.getUsername(), dataStyle);
        safeSetCellValue(row, 2, user.getPassword(), dataStyle);
        safeSetCellValue(row, 3, user.getEmail(), dataStyle);
        safeSetCellValue(row, 4, user.getStatus().name(), dataStyle);
        safeSetCellValue(row, 5, user.getFirstName(), dataStyle);
        safeSetCellValue(row, 6, user.getLastName(), dataStyle);
        safeSetCellValue(row, 7, user.getPhoneNumber(), dataStyle);
        safeSetCellValue(row, 8, user.getRole().name(), dataStyle);
    }

    private void safeSetCellValue(Row row, int cellIndex, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void saveWorkbook(Workbook workbook) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            workbook.write(outputStream);
        }
    }

    public Optional<Person> findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream inputStream = new FileInputStream(FILE_PATH);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null)
                return Optional.empty();

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                Cell usernameCell = row.getCell(1);
                if (usernameCell != null && usernameCell.getStringCellValue().equals(username)) {
                    return Optional.of(mapRowToUser(row));
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Error reading from Excel file", e);
        }
    }

    public Optional<Person> findUserByNationalCode(String nationalCode) {
        if (nationalCode == null || nationalCode.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream inputStream = new FileInputStream(FILE_PATH);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) return Optional.empty();

            Optional<Integer> rowNum = findRowByNationalCode(sheet, nationalCode);
            return rowNum.map(integer -> mapRowToUser(sheet.getRow(integer)));
        } catch (IOException e) {
            throw new RuntimeException("Error reading from Excel file", e);
        }
    }

    public Optional<Person> findByNationalCode(String nationalCode) {
        String excelFilePath = "D:\\project\\UMS_MultiDB-2\\users.xlsx";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String currentNationalCode = row.getCell(1).getStringCellValue();

                if (nationalCode.equals(currentNationalCode)) {
                    Person person = new Person();
                    person.setId((long) row.getCell(0).getNumericCellValue());
                    person.setNationalCode(currentNationalCode);

                    return Optional.of(person);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        }

        return Optional.empty();
    }

    public List<Person> getAllByStatus(Status status) {
        List<Person> users = new ArrayList<>();
        if (status == null) {
            return users;
        }

        try (InputStream inputStream = new FileInputStream(FILE_PATH);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) return users;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String statusValue = getStringCellValue(row, 4);
                if (!statusValue.isEmpty() && Status.valueOf(statusValue) == status) {
                    users.add(mapRowToUser(row));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading from Excel file", e);
        }
        return users;
    }

    public void deleteByNationalCode(String nationalCode) {
        if (nationalCode == null || nationalCode.isEmpty()) {
            return;
        }

        try (InputStream inputStream = new FileInputStream(FILE_PATH);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) return;

            Optional<Integer> rowNum = findRowByNationalCode(sheet, nationalCode);
            if (rowNum.isPresent()) {
                removeRow(sheet, rowNum.get());
                saveWorkbook(workbook);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting user from Excel file", e);
        }
    }

    private Person mapRowToUser(Row row) {
        if (row == null) {
            return null;
        }

        Person user = new Person();
        user.setNationalCode(getStringCellValue(row, 0));
        user.setUsername(getStringCellValue(row, 1));
        user.setPassword(getStringCellValue(row, 2));
        user.setEmail(getStringCellValue(row, 3));

        String statusValue = getStringCellValue(row, 4);
        if (!statusValue.isEmpty()) {
            try {
                user.setStatus(Status.valueOf(statusValue));
            } catch (IllegalArgumentException e) {
                user.setStatus(DEFAULT_STATUS);
            }
        } else {
            user.setStatus(DEFAULT_STATUS);
        }

        user.setFirstName(getStringCellValue(row, 5));
        user.setLastName(getStringCellValue(row, 6));
        user.setPhoneNumber(getStringCellValue(row, 7));

        String roleValue = getStringCellValue(row, 8);
        if (!roleValue.isEmpty()) {
            try {
                user.setRole(Role.valueOf(roleValue));
            } catch (IllegalArgumentException e) {
            }
        }

        return user;
    }

    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private Optional<Integer> findRowByNationalCode(Sheet sheet, String nationalCode) {
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            String rowNationalCode = getStringCellValue(row, 0);
            if (nationalCode.equals(rowNationalCode)) {
                return Optional.of(row.getRowNum());
            }
        }
        return Optional.empty();
    }

    private void removeRow(Sheet sheet, int rowNum) {
        int lastRowNum = sheet.getLastRowNum();
        if (rowNum >= 0 && rowNum < lastRowNum) {
            sheet.shiftRows(rowNum + 1, lastRowNum, -1);
        }
        if (rowNum == lastRowNum) {
            Row removingRow = sheet.getRow(rowNum);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
    }


    public boolean existsById(Long id) {
        if (id == null) return false;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_PATH)) {
            if (is == null) return false;

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) return false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Long rowId = (long) row.getCell(0).getNumericCellValue();
                if (id.equals(rowId)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Error checking existence in Excel", e);
        }
    }

    public List<Person> readFromExcel() {
        List<Person> persons = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_PATH)) {
            if (is == null) return persons;

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) return persons;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                persons.add(mapRowToUser(row));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + FILE_PATH, e);
        }
        return persons;
    }
}