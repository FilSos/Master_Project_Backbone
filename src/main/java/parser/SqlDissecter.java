package parser;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import model.ParsingParameters;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.checkerframework.checker.units.qual.C;
import query.QueryData;
import visitors.ColumnNamesFinder;

public class SqlDissecter {

    private QueryExecuter queryExecuter = new QueryExecuter();
    private ParsingParameters parsingParameters;

    public List<QueryData> evaluateQueries(List<QueryData> queries, ParsingParameters parsingParameters) {
        this.parsingParameters = parsingParameters;
        List<QueryData> validated = validateQueries(queries);
        List<QueryData> executed = executeOnDb(validated);
        executed.addAll(validated.stream().filter(query -> !query.isValid()).collect(Collectors.toList()));
        return scoreQuery(printReport(executed));
    }

    private List<QueryData> executeOnDb(List<QueryData> validated) {
        return validated.stream()
                .filter(QueryData::isValid)
                .map(query -> queryExecuter.execute(query))
                .collect(Collectors.toList());
    }

    private List<QueryData> validateQueries(List<QueryData> queries) {
        return queries.stream()
                .map(this::validateQuery)
                .collect(Collectors.toList());
    }

    private QueryData validateQuery(QueryData query) {
        try {
            Statement statement = CCJSqlParserUtil.parse(query.getQueryString());
            double tableMatches = getTableMatches(query, statement);
            double columnsMatches = getColumnsMatches(query,statement);

            System.out.println("Dobre:" + statement);
            return QueryData.newBuilder(query)
                    .withStatement(statement)
                    .withIsValid(true)
                    .withColumnMatched(columnsMatches)
                    .withTableMatched(tableMatches)
                    .build();

        } catch (JSQLParserException e) {
            QueryData queryData = detectTypos(query);
            if (queryData.getTypos() > 0) {
                return validateQuery(queryData);
            } else {
                query.setValid(false);
                if(query.getQueryString().trim().equals("-")) {
                    System.out.println("Zadanie puste");
                }else {
                    e.printStackTrace();
                }
                return QueryData.newBuilder(query)
                        .withIsValid(false)
                        .build();
            }
        }
    }

    private double getTableMatches(QueryData query, Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        Set<String> tableList = new HashSet<>(tablesNamesFinder.getTableList(statement));
        tableList = normalizeNames(tableList);
        String[] split = parsingParameters.getUsedTables().get(query.getExNumber()).split(",");
        int matches = 0;
        for(String table : split){
            if(tableList.contains(table.toLowerCase().trim())){
                matches++;
            }
        }
        return (double)matches/split.length;
    }

    private double getColumnsMatches(QueryData query, Statement statement) {
        ColumnNamesFinder columnNamesFinder = new ColumnNamesFinder();
        Set<String> tableColumns = columnNamesFinder.getTableColumns(statement);
        tableColumns = normalizeNames(tableColumns);
        String[] split = parsingParameters.getUsedColumns().get(query.getExNumber()).split(",");
        int matches = 0;
        for(String column : split){
            if(tableColumns.contains(column.trim().toLowerCase())){
                matches++;
            }
        }
        return (double)matches/split.length;
    }

    private Set<String> normalizeNames(Set<String> strings) {
        return strings.stream()
                .map(this::deletePrefix)
                .collect(Collectors.toSet());
    }

    private String deletePrefix(String name){
        if(name.contains(".")){
            return name.split("\\.")[1];
        }
        return name;
    }

    private QueryData detectTypos(QueryData queryData) {
        String stringToCheck = queryData.getQueryString();
        Multimap<String, String> typoMap = parsingParameters.getTyposAsMultimap();
        int numberOfTypos = 0;

        for (String typo : typoMap.values()) {
            if (stringToCheck.contains(typo)) {
                String fixer = typoMap.entries().stream()
                        .filter(entry -> entry.getValue().equals(typo))
                        .findFirst()
                        .get()
                        .getKey();
                stringToCheck = stringToCheck.replaceAll(typo, fixer);
                numberOfTypos++;
            }
        }
        return QueryData.newBuilder(queryData)
                .withQueryString(stringToCheck)
                .withTypos(numberOfTypos)
                .build();
    }

    private Map<Boolean, List<QueryData>> printReport(List<QueryData> queries) {
        QueryData referenceResult = queries.stream()
                .findFirst()
                .get();

        Map<Boolean, List<QueryData>> validQueries = queries.stream()
                .filter(QueryData::isValid)
                .filter(query -> !query.isRef())
                .collect(Collectors.partitioningBy(query -> queryExecuter.compareResults(referenceResult.getResult(), query.getResult())));

        System.out.println("----------------------------REPORT-----------------------------------------------");
        System.out.println("Ref query: " + referenceResult.getQueryString());
        System.out.println("------------------------VALID QUERIES--------------------------------------------");
        validQueries.forEach((key, value) -> value.forEach(query -> System.out
                .println("Query " + query.getQueryString() + " Identifier: " + query.getIdentifier() + " isCorrect: " + key)));
        System.out.println("----------------------INVALID QUERIES--------------------------------------------");
        queries.stream()
                .filter(queryData -> !queryData.isValid())
                .forEach(queryData -> System.out.println(
                        "Failed to process query: " + queryData.getQueryString() + " it belongs to: " + queryData.getIdentifier()));

        return queries.stream()
                .filter(queryData -> !queryData.isRef())
                .collect(Collectors.partitioningBy(query -> queryExecuter.compareResults(referenceResult.getResult(), query.getResult())));
    }

    private List<QueryData> scoreQuery(Map<Boolean, List<QueryData>> queries) {
        List<QueryData> correct = queries.get(true)
                .stream()
                .map(query -> QueryData.newBuilder(query).withScore(100 - (query.getTypos() * 5)).build())
                .collect(Collectors.toList());
        List<QueryData> failed = queries.get(false)
                .stream()
                .map(query -> QueryData.newBuilder(query).withScore(0).build())
                .collect(Collectors.toList());

        correct.addAll(failed);
        return correct;
    }

}
