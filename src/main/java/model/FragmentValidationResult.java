package model;

public class FragmentValidationResult {

    private CodeFragment fragment;

    private Double overlapCoefficient;
    private Double jaroWinklerSimilarity;

    public FragmentValidationResult(CodeFragment fragment, Double overlapCoefficient, Double jaroWinklerSimilarity) {
        this.fragment = fragment;
        this.overlapCoefficient = overlapCoefficient;
        this.jaroWinklerSimilarity = jaroWinklerSimilarity;
    }

    public FragmentValidationResult(CodeFragment fragment) {
        this.fragment = fragment;
    }

    public CodeFragment getFragment() {
        return fragment;
    }

    public void setFragment(CodeFragment fragment) {
        this.fragment = fragment;
    }

    public Double getOverlapCoefficient() {
        return overlapCoefficient;
    }

    public void setOverlapCoefficient(Double overlapCoefficient) {
        this.overlapCoefficient = overlapCoefficient;
    }

    public Double getJaroWinklerSimilarity() {
        return jaroWinklerSimilarity;
    }

    public void setJaroWinklerSimilarity(Double jaroWinklerSimilarity) {
        this.jaroWinklerSimilarity = jaroWinklerSimilarity;
    }
}
