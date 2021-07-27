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
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface FixCaseRepository extends JpaRepository<FixCase, Long> {
    LinkedList<Long> addedFixCaseId = new LinkedList<>();

    void deleteById(long id);

    default List<FixCase> top3Times(){
        Comparator<FixCase> dateDiffComparator = Comparator.comparingLong(x ->
                (x.getProblemStartTime().getTime() - x.getProblemEndTime().getTime()));

        return findAll().stream()
                .sorted(dateDiffComparator)
                .limit(3)
                .collect(Collectors.toList());
    }

    default List<FixCase> top3Reasons(){
        return findAll().stream()
                .collect( Collectors.groupingBy(FixCase::getReasonName, Collectors.counting()))
                .entrySet().stream()
                .sorted( Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .flatMap(reasonName ->  findAll().stream()
                            .filter( x -> reasonName.equals(x.getReasonName())))
                .collect( Collectors.toList());
    }

    default List<FixCase> repeatedReasons(){
        List<FixCase> casesList = findAll().stream()
                .collect( Collectors.groupingBy(FixCase::getReasonName))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .flatMap(entry -> entry.getValue().stream()
                        .sorted(Comparator.comparing(FixCase::getProblemStartTime)))
                .collect(Collectors.toList());
        //хотелось бы [.filter((x,y) -> (x.getTime - y.getTime).toDays < 15)], но печаль
        return deleteOutOf15Days(casesList);

    }

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

    default List<FixCase> deleteOutOf15Days(List<FixCase> casesList){
        Set<FixCase> in15Days = new HashSet<>();

        FixCase currentFC = casesList.get(0);
        for( int i = 1; i < casesList.size(); i++){
            if(currentFC.getReasonName().equals(casesList.get(i).getReasonName()) &&
                    ((currentFC.getProblemStartTime().getTime())
                            - (casesList.get(i).getProblemStartTime().getTime()))/(1000*60*60*24) < 15){
                in15Days.add(currentFC);
                in15Days.add(casesList.get(i));
            }
            currentFC = casesList.get(i);
        }
        return in15Days.stream().sorted(Comparator.comparing(FixCase::getReasonName)
                                        .thenComparing(FixCase::getProblemStartTime))
                .collect(Collectors.toList());
    }

}