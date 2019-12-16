package parser;

import com.google.common.collect.Multimap;
import model.FragmentValidationResult;
import model.ParsingParameters;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import query.QueryData;

import java.util.List;
import java.util.stream.Collectors;


public class SqlDissecter {

    private static Logger logger = LogManager.getLogger(SqlDissecter.class);


    private QueryExecuter queryExecuter = new QueryExecuter();
    private QueryFragmentValidator queryFragmentValidator = new QueryFragmentValidator();
    private ParsingParameters parsingParameters;
    private QueryScorer queryScorer = new QueryScorer();
    private TableColumnMatcher tableColumnMatcher;

    public List<QueryData> evaluateQueries(List<QueryData> queries, ParsingParameters parsingParameters) {
        this.parsingParameters = parsingParameters;
        this.tableColumnMatcher = new TableColumnMatcher(parsingParameters);

        List<QueryData> validated = validateQueries(queries);
        List<QueryData> executed = executeOnDb(validated);

        executed.addAll(validated.stream()
                .filter(query -> !query.isValid())
                .collect(Collectors.toList()));

        List<QueryData> references = parsingParameters.getReferenceQueries().stream()
                .map(this::validateQuery)
                .map(queryData -> queryExecuter.execute(queryData))
                .collect(Collectors.toList());

        return scoreQuery(executed, references);
    }

    private List<QueryData> executeOnDb(List<QueryData> validated) {
        return validated.stream()
                .filter(query -> query.isValid() || query.isRef())
                .map(query -> queryExecuter.execute(query))
                .collect(Collectors.toList());
    }

    private List<QueryData> validateQueries(List<QueryData> queries) {
        return queries.stream()
                .map(this::validateQuery)
                .collect(Collectors.toList());
    }

    private QueryData validateQuery(QueryData query) {
        QueryData queryData = detectTypos(query);
        if (queryData.getTypos() > 0) {
            return validateQuery(queryData);
        }
        List<FragmentValidationResult> fragmentValidationResults = getFragmentValidationResults(query);
        try {
            Statement statement = CCJSqlParserUtil.parse(query.getQueryString());

            double tableMatches = tableColumnMatcher.getTableMatches(query, statement);
            double columnsMatches = tableColumnMatcher.getColumnsMatches(query, statement);

            logger.info("Dobre:" + statement);
            return QueryData.newBuilder(query)
                    .withStatement(statement)
                    .withIsValid(true)
                    .withColumnMatched(columnsMatches)
                    .withTableMatched(tableMatches)
                    .withFragmentValidationResults(fragmentValidationResults)
                    .build();

        } catch (JSQLParserException e) {
            String errorMessage = "Error during parsing: " + e.getMessage();
            if (query.getQueryString().trim().equals("-") || query.getQueryString().trim().isEmpty()) {
                logger.error("Zadanie puste");
                errorMessage = "Error during parsing: No valid query";
            } else {
                e.printStackTrace();
            }
            return QueryData.newBuilder(query)
                    .withIsValid(false)
                    .withErrorReason(errorMessage)
                    .withFragmentValidationResults(fragmentValidationResults)
                    .build();
        }
    }

    private List<FragmentValidationResult> getFragmentValidationResults(QueryData query) {
        String queryString = query.getQueryString();
        return parsingParameters.getCodeFragments().stream()
                .filter(codeFragment -> codeFragment.getExNumber() == query.getExNumber())
                .map(fragment -> queryFragmentValidator.checkSimilarity(fragment, queryString))
                .collect(Collectors.toList());
    }

    private QueryData detectTypos(QueryData queryData) {
        //TODO: needs sth more advanced to check cases like typo SELEC(SELECT)/UPDAT(UPDATE)/SERT(INSERT).
        // This cases cannot be handled by simple contains. Proposed: split string by spaces and search typos in created that way tokens.
        // After all fixes, recreate string.

        String stringToCheck = queryData.getQueryString().toLowerCase();
        Multimap<String, String> typoMap = parsingParameters.getTyposAsMultimap();
        int numberOfTypos = 0;

        for (String typo : typoMap.values()) {
            if (stringToCheck.contains(typo.toLowerCase())) {
                String fixer = typoMap.entries().stream()
                        .filter(entry -> entry.getValue().equalsIgnoreCase(typo))
                        .findFirst()
                        .get()
                        .getKey();
                stringToCheck = stringToCheck.replaceAll(typo.toLowerCase(), fixer.toLowerCase());
                numberOfTypos++;
            }
        }
        return QueryData.newBuilder(queryData)
                .withQueryString(stringToCheck)
                .withTypos(numberOfTypos)
                .build();
    }

    private List<QueryData> scoreQuery(List<QueryData> queries, List<QueryData> references) {
        return queries.stream()
                .map(queryData -> queryScorer.evaluate(queryData, parsingParameters.getWeights(), references))
                .collect(Collectors.toList());
    }

   /* private Map<Boolean, List<QueryData>> printReport(List<QueryData> queries) {
        QueryData referenceResult = queries.stream()
                .findFirst()
                .get();

        Map<Boolean, List<QueryData>> validQueries = queries.stream()
                .filter(QueryData::isValid)
                .filter(query -> !query.isRef())
                .collect(Collectors.partitioningBy(query -> queryExecuter.compareResults(referenceResult.getResult(), query.getResult())));

        logger.info("----------------------------REPORT-----------------------------------------------");
        logger.info("Ref query: " + referenceResult.getQueryString());
        logger.info("------------------------VALID QUERIES--------------------------------------------");
        validQueries.forEach((key, value) -> value.forEach(query -> System.out
                .println("Query " + query.getQueryString() + " Identifier: " + query.getIdentifier() + " isCorrect: " + key)));
        logger.info("----------------------INVALID QUERIES--------------------------------------------");
        queries.stream()
                .filter(queryData -> !queryData.isValid())
                .forEach(queryData -> logger.info(
                        "Failed to process query: " + queryData.getQueryString() + " it belongs to: " + queryData.getIdentifier()));

        return queries.stream()
                .filter(queryData -> !queryData.isRef())
                .collect(Collectors.partitioningBy(query -> queryExecuter.compareResults(referenceResult.getResult(), query.getResult())));
    }

    /*private List<QueryData> scoreQuery(Map<Boolean, List<QueryData>> queries) {
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
    }*/

}
