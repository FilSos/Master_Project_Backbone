package converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import query.QueryData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static view.Main.mainController;

public class ExcelImport {

    private static List<Object[][]> dataRows = null;

    public static void doImport(List<QueryData> resultList) {
        dataRows = new ArrayList();
        String FILE_NAME = "/tmp/" + resultList.hashCode() + ".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Results");
        Object[][] columns = {{"Student", "Numer zadania", "Zapytanie", "Czy parsowanie się udało", "Zgodność kolumn", "Zgodność tabeli", "Zgodność z zapytaniem referencyjnym", "Literówki", "Poprawność %"}};
        dataRows.add(columns);
        for (QueryData data : resultList) {
            Object[][] row = {{data.getIdentifier(), String.valueOf(data.getExNumber()), data.getQueryString(), data.isValid() ? "Tak" : "Nie", String.valueOf(data.getMatchedColumns()), String.valueOf(data.getMatchedTables()), String.valueOf(data.getResultMatchScore()), String.valueOf(data.getTypos()), String.valueOf(data.getScore())}};
            dataRows.add(row);
        }


        int rowNum = 0;
        for (String name : mainController.fileNames) {
            System.out.println("Creating excel for " + name);
        }
        for (Object[][] oneRow : dataRows) {

            for (Object[] datatype : oneRow) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (Object field : datatype) {
                    Cell cell = row.createCell(colNum++);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }

                }
            }
        }
        //9 - liczba wskazujaca kolumny, z braku lepszego pomyslu, poki co na sztywno
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Zrobione");
    }
}