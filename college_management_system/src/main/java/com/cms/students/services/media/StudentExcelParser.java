package com.cms.students.services.media;


import com.cms.students.dto.StudentUploadDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentExcelParser {

    public static List<StudentUploadDTO> parseStudents(MultipartFile file) throws IOException {

        List<StudentUploadDTO> list = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row → start from row index 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row r = sheet.getRow(i);
                if (r == null) continue;

                StudentUploadDTO dto = new StudentUploadDTO();

                dto.setFirstName(get(r, 0));
                dto.setMiddleName(get(r, 1));
                dto.setLastName(get(r, 2));

                // Date of Birth (YYYY-MM-DD)
                String dobStr = get(r, 3);
                if (!dobStr.isEmpty()) {
                    dto.setDob(LocalDate.parse(dobStr));
                }

                dto.setGender(get(r, 4));

                dto.setPhone(get(r, 5));
                dto.setEmail(get(r, 6));
                dto.setAltPhone(get(r, 7));

                dto.setAddressLine1(get(r, 8));
                dto.setAddressLine2(get(r, 9));
                dto.setCity(get(r, 10));
                dto.setDistrict(get(r, 11));
                dto.setState(get(r, 12));
                dto.setCountry(get(r, 13));
                dto.setPin(get(r, 14));

                dto.setDepartmentCode(get(r, 15));
                dto.setCourseCode(get(r, 16));

                String yearStr = get(r, 17);
                if (!yearStr.isEmpty()) {
                    dto.setAdmissionYear(Integer.parseInt(yearStr));
                }

                dto.setAdmissionNumber(get(r, 18));
                dto.setRollNumber(get(r, 19));

                dto.setBloodGroup(get(r, 20));
                dto.setCategory(get(r, 21));
                dto.setNationality(get(r, 22));

                list.add(dto);
            }
        }

        return list;
    }

    /**
     * Safely reads a column from Excel and returns String value.
     */
    private static String get(Row r, int col) {
        Cell c = r.getCell(col);
        if (c == null) return "";

        switch (c.getCellType()) {
            case STRING:
                return c.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(c)) {
                    // Convert date to yyyy-MM-dd
                    return c.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    // Convert numeric to string (remove .0 if integer)
                    double val = c.getNumericCellValue();
                    long longVal = (long) val;
                    return (val == longVal) ? String.valueOf(longVal) : String.valueOf(val);
                }

            case BOOLEAN:
                return String.valueOf(c.getBooleanCellValue());

            case FORMULA:
                return c.getCellFormula();

            default:
                return "";
        }
    }
}
