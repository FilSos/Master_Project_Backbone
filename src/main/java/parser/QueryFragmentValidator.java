package parser;

import model.CodeFragment;
import model.FragmentValidationResult;
import org.apache.commons.text.similarity.IntersectionResult;
import org.apache.commons.text.similarity.IntersectionSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.Arrays;

public class QueryFragmentValidator {
    private JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
    private IntersectionSimilarity<String> intersectionSimilarity = new IntersectionSimilarity<>(cs -> Arrays.asList(cs.toString().toLowerCase().trim().split("\\s")));

    public FragmentValidationResult checkSimilarity(CodeFragment fragment, String query) {
        Double jaroWinkler = jaroWinklerSimilarity.apply(fragment.getQueryFragment().toLowerCase(), query);
        IntersectionResult intersectionResult = intersectionSimilarity.apply(fragment.getQueryFragment().toLowerCase(), query);
        double overlapCoefficient = calculateOverlapCoefficient(intersectionResult);
        return new FragmentValidationResult(fragment, overlapCoefficient, jaroWinkler);
    }

    private double calculateOverlapCoefficient(IntersectionResult intersectionResult) {
        int intersection = intersectionResult.getIntersection();

        if (intersectionResult.getSizeA() < intersectionResult.getSizeB()) {
            return (double) intersection / intersectionResult.getSizeA();
        }
        return (double) intersection / intersectionResult.getSizeB();
    }
}
