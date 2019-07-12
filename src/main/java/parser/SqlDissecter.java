package parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import query.QueryData;

public class SqlDissecter {
    
    QueryExecuter queryExecuter = new QueryExecuter();
    
    public List<QueryData> evaluateQueries(List<QueryData> queries) {
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
            System.out.println("Dobre:" + statement);
            return QueryData.newBuilder(query)
                    .withStatement(statement)
                    .withIsValid(true)
                    .build();
        } catch (JSQLParserException e) {
            QueryData queryData = detectTypos(query);
            if (queryData.getTypos() > 0) {
                return validateQuery(queryData);
            } else {
                query.setValid(false);
                e.printStackTrace();
                return QueryData.newBuilder(query)
                        .withIsValid(false)
                        .build();
            }
        }
    }
    
    private QueryData detectTypos(QueryData queryData) {
        String stringToCheck = queryData.getQueryString();
        Multimap<String, String> typoMap = ArrayListMultimap.create();
        typoMap.put("SELECT", "SELECTO");
        typoMap.put("SELECT", "SELECTADO");
        typoMap.put("SELECT", "SELCT");
        typoMap.put("FROM", "FORM");
        typoMap.put("FROM", "FRM");
        int numberOfTypos = 0;
        
        for (String typo: typoMap.values()) {
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
                .map(query -> QueryData.newBuilder().withScore(100 - (query.getTypos() * 5)).build())
                .collect(Collectors.toList());
        List<QueryData> failed = queries.get(false)
                .stream()
                .map(query -> QueryData.newBuilder().withScore(0).build())
                .collect(Collectors.toList());

        correct.addAll(failed);
        return correct;
    }
    
}
