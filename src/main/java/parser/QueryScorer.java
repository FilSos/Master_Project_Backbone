package parser;

import model.FragmentValidationResult;
import model.Weights;
import query.QueryData;

import java.util.List;
import java.util.Optional;

public class QueryScorer {

    QueryExecuter queryExecuter = new QueryExecuter();

    public QueryData evaluate(QueryData queryData, Weights weights, List<QueryData> references) {

        Optional<QueryData> refData = references.stream()
                .filter(reference -> reference.getExNumber() == queryData.getExNumber())
                .findFirst();

        double finalScore = queryData.getMatchedColumns() * weights.getUsedColumns() +
                queryData.getMatchedTables() * weights.getUsedTables() +
                scoreCodeFragments(queryData.getFragmentValidationResults()) * weights.getCodeFragments();

        double referenceMatchScore = 0;
        if (refData.isPresent() && queryData.getResult() != null && !queryData.getResult().isEmpty()) {
            if (queryExecuter.compareResults(refData.get().getResult(), queryData.getResult())) {
                finalScore += 1 * weights.getRefQueries();
                referenceMatchScore = 1;
            }
        }
        finalScore = Math.round(((finalScore / 4) - (queryData.getTypos() * weights.getTypos())) * 100);

        return QueryData.newBuilder(queryData)
                .withScore(finalScore)
                .withResultMatchScore(referenceMatchScore)
                .build();
    }

    private double scoreCodeFragments(List<FragmentValidationResult> fragmentValidationResults) {
        double jaroWinklerSimilarityAverage = fragmentValidationResults.stream()
                .mapToDouble(FragmentValidationResult::getJaroWinklerSimilarity)
                .average()
                .orElse(0.0);
        double overlapCoefficientAverage = fragmentValidationResults.stream()
                .mapToDouble(FragmentValidationResult::getOverlapCoefficient)
                .average()
                .orElse(0.0);

        return (jaroWinklerSimilarityAverage + overlapCoefficientAverage) / 2;
    }

}
