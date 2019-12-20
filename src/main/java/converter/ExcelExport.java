package converter;

import model.FragmentValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import query.QueryData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ExcelExport {

    private static Logger logger = LogManager.getLogger(ExcelExport.class);
    private static Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private static List<Object[][]> dataRows = null;

    public static void doImport(List<QueryData> resultList, String fileName) throws IOException, URISyntaxException {
        String programPath = JarPathConverter.getPathToResources();
        File dir = new File(programPath + "/wyniki/");
        dir.mkdirs();
        dataRows = new ArrayList();
        String FILE_NAME = programPath + "/wyniki/" + fileName + "_" + timestamp.getTime() + ".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Results");
        Object[][] columns = {{"Student", "Numer zadania", "Zapytanie", "Czy parsowanie się udało", "Zgodność kolumn", "Zgodność tabeli", "Zgodność z zapytaniem referencyjnym", "Zgodność fragmentów - fragment", "Zgodność fragmentów - współczynnik pokrycia", "Zgodność fragmentów - podobieństwo Jaro - Winklera", "Literówki", "Poprawność %"}};
        dataRows.add(columns);
        for (QueryData data : resultList) {
            if (!data.getFragmentValidationResults().isEmpty()) {
                List<FragmentValidationResult> fragmentValidationResults = data.getFragmentValidationResults();
                String fragments = fragmentValidationResults.stream().map(fragmentValidationResult -> fragmentValidationResult.getFragment().getQueryFragment())
                        .collect(Collectors.joining(", "));
                String overlapCoefficient = fragmentValidationResults.stream().map(fragmentValidationResult -> String.valueOf(Math.floor(fragmentValidationResult.getOverlapCoefficient() * 100) / 100))
                        .collect(Collectors.joining(", "));
                String jaroWinklerSimilarity = fragmentValidationResults.stream().map(fragmentValidationResult -> String.valueOf(Math.floor(fragmentValidationResult.getJaroWinklerSimilarity() * 100) / 100))
                        .collect(Collectors.joining(", "));
                Object[][] row = {{data.getIdentifier(), String.valueOf(data.getExNumber()), data.getQueryString(), data.isValid() ? "Tak" : "Nie",
                        String.valueOf(data.getMatchedColumns()), String.valueOf(data.getMatchedTables()), String.valueOf(data.getResultMatchScore()), fragments, overlapCoefficient, jaroWinklerSimilarity,
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
        logger.info("Zrobione");
    }
}