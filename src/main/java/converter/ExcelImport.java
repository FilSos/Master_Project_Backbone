package converter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import query.QueryData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static view.Start.mainController;

public class ExcelImport {

    private static List<Object[][]> dataRows = null;

    public static void doImport(List<QueryData> resultList) throws IOException {
        File dir = new File("/wyniki/");
        dir.mkdirs();
        dataRows = new ArrayList();
        String FILE_NAME = "/wyniki/" + resultList.hashCode() + ".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Results");
        Object[][] columns = {{"Student", "Numer zadania", "Zapytanie", "Czy parsowanie się udało", "Zgodność kolumn", "Zgodność tabeli", "Zgodność z zapytaniem referencyjnym", "Zgodność fragmentów - fragment", "Zgodność fragmentów - współczynnik przesunięcia", "Zgodność fragmentów - odległóść Jaro - Winklera", "Literówki", "Poprawność %"}};
        dataRows.add(columns);
        for (QueryData data : resultList) {
            if (!data.getFragmentValidationResults().isEmpty()) {
                Object[][] row = {{data.getIdentifier(), String.valueOf(data.getExNumber()), data.getQueryString(), data.isValid() ? "Tak" : "Nie",
                        String.valueOf(data.getMatchedColumns()), String.valueOf(data.getMatchedTables()),
                        String.valueOf(data.getResultMatchScore()), data.getFragmentValidationResults().get(0).getFragment().getQueryFragment(),
                        String.valueOf(Math.floor(data.getFragmentValidationResults().get(0).getJaroWinklerSimilarity() * 100) / 100),
                        String.valueOf(Math.floor(data.getFragmentValidationResults().get(0).getOverlapCoefficient() * 100) / 100),
                        String.valueOf(data.getTypos()), String.valueOf(data.getScore())}};
                dataRows.add(row);
            } else {
                Object[][] row = {{data.getIdentifier(), String.valueOf(data.getExNumber()), data.getQueryString(), data.isValid() ? "Tak" : "Nie",
                        String.valueOf(data.getMatchedColumns()), String.valueOf(data.getMatchedTables()),
                        String.valueOf(data.getResultMatchScore()), "brak fragmentu",
                        "0",
                        "0",
                        String.valueOf(data.getTypos()), String.valueOf(data.getScore())}};
                dataRows.add(row);
            }

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
        //12 - liczba wskazujaca kolumny, z braku lepszego pomyslu, poki co na sztywno
        for (int i = 0; i < 12; i++) {
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