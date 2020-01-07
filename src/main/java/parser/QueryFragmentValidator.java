package parser;

import model.CodeFragment;
import model.FragmentValidationResult;
import org.apache.commons.text.similarity.IntersectionResult;
import org.apache.commons.text.similarity.IntersectionSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class QueryFragmentValidator {

    private JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
    private IntersectionSimilarity<String> intersectionSimilarity;

    private static final List<String> tabooList = List.of(",", "\\.", ";");


    public FragmentValidationResult checkSimilarity(CodeFragment fragment, String query) {
        if (query.toLowerCase().contains(fragment.getQueryFragment().toLowerCase())) {
            return new FragmentValidationResult(fragment, 1.0, 1.0);
        }
        intersectionSimilarity = new IntersectionSimilarity<>(charSequenceCollectionFunction);
        Double jaroWinkler = jaroWinklerSimilarity.apply(fragment.getQueryFragment().toLowerCase(), query);
        IntersectionResult intersectionResult = intersectionSimilarity.apply(fragment.getQueryFragment().toLowerCase(), query);
        double overlapCoefficient = calculateOverlapCoefficient(intersectionResult);
        return new FragmentValidationResult(fragment, overlapCoefficient, jaroWinkler);
    }

    public double calculateResultSimilarity(String result1, String result2) {
        intersectionSimilarity = new IntersectionSimilarity<>(cs -> Arrays.asList(cs.toString().toLowerCase().trim().split("\\s")));
        Double jarowinkler = jaroWinklerSimilarity.apply(result1, result2);
        IntersectionResult intersectionResult = intersectionSimilarity.apply(result1, result2);
        double overlapCoefficient = calculateOverlapCoefficient(intersectionResult);
        return (jarowinkler + overlapCoefficient) / 2;
    }

    private double calculateOverlapCoefficient(IntersectionResult intersectionResult) {
        int intersection = intersectionResult.getIntersection();

        if (intersectionResult.getSizeA() < intersectionResult.getSizeB()) {
            return (double) intersection / intersectionResult.getSizeA();
        }
        return (double) intersection / intersectionResult.getSizeB();
    }

    Function<CharSequence, Collection<String>> charSequenceCollectionFunction = cs -> {
        String initialString = cs.toString().toLowerCase().trim();

        for (String tabooKey : tabooList) {
            initialString = initialString.replaceAll(tabooKey, " ");
        }

        return Arrays.asList(initialString.split("\\s"));
    };


}
