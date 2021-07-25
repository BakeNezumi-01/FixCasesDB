package com.example.demo.repository;

import com.example.demo.domain.FixCase;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Repository
public interface FixCaseRepository extends JpaRepository<FixCase, Long> {
    LinkedList<Long> addedFixCaseId = new LinkedList<>();

    FixCase findByCaseId(Long id);
    void deleteById(long id);

    @Query(
            value = "SELECT * FROM FIX_CASE \n" +
                    "ORDER BY PROBLEM_START_TIME - PROBLEM_END_TIME \n" +
                    "LIMIT 3",
            nativeQuery = true)
    List<FixCase> top3Times();

    @Query(
            value = "select * from FIX_CASE \n" +
                    "where REASON_NAME in (SELECT REASON_NAME FROM FIX_CASE \n" +
                    "                      GROUP BY REASON_NAME \n" +
                    "                      ORDER BY COUNT(*) DESC\n" +
                    "                      limit 3)\n" +
                    "order by REASON_NAME desc;",
            nativeQuery = true)
    List<FixCase> top3Reasons();

    @Query(
            value = "select *  from FIX_CASE a\n" +
                    "where exists(select * from FIX_CASE b\n" +
                                "where a.REASON_NAME = b.REASON_NAME \n" +
                                "and datediff(day, a.PROBLEM_START_TIME , b.PROBLEM_START_TIME) <= 15\n" +
                                "and datediff(day, a.PROBLEM_START_TIME , b.PROBLEM_START_TIME) > 0)\n" +
                    "order by REASON_NAME, PROBLEM_START_TIME ",
            nativeQuery = true)
    List<FixCase> repeatedReasons();

    default int deleteAdded(){
        int count = addedFixCaseId.size();
        for(long id : addedFixCaseId){
            deleteById(id);
        }
        addedFixCaseId.clear();
        return count;
    }

    default int putFileToDB(MultipartFile file) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(convertMultipartFileToFile(file));
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            try{
                FixCase currentFixCase = new FixCase(
                        (long) Double.parseDouble(row.getCell(0).toString()),
                        tryToReadColumnAsString(row, 1),
                        row.getCell(2).toString(),
                        row.getCell(3).getDateCellValue(),
                        row.getCell(4).getDateCellValue(),
                        tryToReadColumnAsString(row, 5),
                        row.getCell(6).toString(),
                        row.getCell(7).toString());
                save(currentFixCase);
                flush();
                addedFixCaseId.add((long) Double.parseDouble(row.getCell(0).toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        return addedFixCaseId.size();
    }

    private static String tryToReadColumnAsString(Row row, int columnNumber){
        Exception e = null;
        String arg = "";
        try{
            arg = String.valueOf((long) row.getCell(columnNumber).getNumericCellValue());
        }catch (Exception exception){
            e = exception;
        }
        if( e!=null){
            try{
                arg = row.getCell(columnNumber).getStringCellValue();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
        return arg;
    }

    private static File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}