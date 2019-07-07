package parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import query.QueryData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlDissecter {

    QueryExecuter queryExecuter = new QueryExecuter();

    public void evaluateQueries(List<QueryData> queries) {
        List<QueryData> validated = validateQueries(queries);
        List<QueryData> executed = executeOnDb(validated);
        executed.addAll(validated.stream().filter(query -> !query.isValid()).collect(Collectors.toList()));
        printReport(executed);
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
            query.setValid(false);
            e.printStackTrace();
            return QueryData.newBuilder(query)
                    .withIsValid(false)
                    .build();
        }
    }

    private void printReport(List<QueryData> queries) {
        QueryData referenceResult = queries.stream()
                .filter(QueryData::isRef)
                .findFirst()
                .get();

        Map<Boolean, List<QueryData>> collect = queries.stream()
                .filter(QueryData::isValid)
                .filter(query -> !query.isRef())
                .collect(Collectors.partitioningBy(query -> queryExecuter.compareResults(referenceResult.getResult(), query.getResult())));

        System.out.println("----------------------------REPORT-----------------------------------------------");
        System.out.println("Ref query: " + referenceResult.getQueryString());
        System.out.println("------------------------VALID QUERIES--------------------------------------------");
        collect.forEach((key, value) -> value.forEach(query ->
                System.out.println("Query " + query.getQueryString() + " Identifier: " + query.getIdentifier() + " isCorrect: " + key)
        ));
        System.out.println("----------------------INVALID QUERIES--------------------------------------------");
        queries.stream()
                .filter(queryData -> !queryData.isValid())
                .forEach(queryData -> System.out.println("Failed to process query: " + queryData.getQueryString() + " it belongs to: " + queryData.getIdentifier()));
    }

}
