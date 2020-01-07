package parser;

import com.google.gson.Gson;
import model.FragmentValidationResult;
import model.Weights;
import query.QueryData;

import java.util.List;
import java.util.Optional;

public class QueryScorer {

    QueryExecuter queryExecuter = new QueryExecuter();
    QueryFragmentValidator queryFragmentValidator = new QueryFragmentValidator();

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
            } else {
                Gson gson = new Gson();
                String result1 = gson.toJson(refData.get().getResult());
                String result2 = gson.toJson(queryData.getResult());
                referenceMatchScore = queryFragmentValidator.calculateResultSimilarity(result1, result2);
                finalScore += referenceMatchScore * weights.getRefQueries();
            }
        }
        double weightsum = weights.getCodeFragments() + weights.getRefQueries() + weights.getUsedColumns() + weights.getUsedTables();

        finalScore = ((finalScore / weightsum) - (queryData.getTypos() * weights.getTypos())) * 100;

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
