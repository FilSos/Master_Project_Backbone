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
        intersectionSimilarity = new IntersectionSimilarity<>(charSequenceCollectionFunction);
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

    Function<CharSequence, Collection<String>> charSequenceCollectionFunction = cs -> {
        String initialString = cs.toString().toLowerCase().trim();

        for (String tabooKey : tabooList) {
            initialString = initialString.replaceAll(tabooKey, " ");
        }

        return Arrays.asList(initialString.split("\\s"));
    };

}
