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


        if (refData.isPresent()) {
            if (queryExecuter.compareResults(refData.get().getResult(), queryData.getResult())) {
                finalScore += 1 * weights.getRefQueries();
            }
        }
        finalScore = finalScore - queryData.getTypos() * weights.getTypos();

        return QueryData.newBuilder(queryData)
                .withScore(finalScore)
                .build();
    }

    private double scoreCodeFragments(List<FragmentValidationResult> fragmentValidationResults) {
        double jaroWinklerSimilarityAverage = fragmentValidationResults.stream()
                .mapToDouble(FragmentValidationResult::getJaroWinklerSimilarity)
                .average().getAsDouble();
        double overlapCoefficientAverage = fragmentValidationResults.stream()
                .mapToDouble(FragmentValidationResult::getOverlapCoefficient)
                .average().getAsDouble();

        return (jaroWinklerSimilarityAverage + overlapCoefficientAverage) / 2;
    }

}
