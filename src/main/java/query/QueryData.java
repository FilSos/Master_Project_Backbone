package query;

import model.FragmentValidationResult;
import net.sf.jsqlparser.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class QueryData {
    String identifier; // miejsce na nr indeksu
    String queryString; //wczytany sql
    Statement statement; // sparsowany sql
    boolean isValid; // czy sie sparsowal
    boolean isRef; // czy jest referencyjny, tj oznaczenie przykladu
    List result; // wynik wykonania na bazie
    int typos; //miejsce na spisanie literowek
    Double score; //ocena koncowa
    int exNumber; //numerZadania
    double matchedColumns; //liczba uzytych kolumn z pliku parametrow
    double matchedTables; //liczba uzytych tabel z pliku parametrow
    double resultMatchScore; //stopien zgodnosci wyniku z zapytaniem referencyjnym
    List<FragmentValidationResult> fragmentValidationResults; //fragmenty ktore zostaly sprawdzone i wyniki poprawnosci
    String errorReason; //message z exceptiona ktory polecial jak zapytanie jest niepoprawne

    public QueryData(String queryString, String identifier, boolean isRef, int exNumber) {
        this.queryString = queryString.toLowerCase().trim();
        this.identifier = identifier;
        this.isRef = isRef;
        this.typos = 0;
        this.score = 0.0;
        this.exNumber = exNumber;
        this.matchedColumns = 0.0;
        this.matchedTables = 0.0;
        this.result = new ArrayList();
        this.fragmentValidationResults = new ArrayList<>();
        this.resultMatchScore = 0.0;
    }

    private QueryData(Builder builder) {
        setIdentifier(builder.identifier);
        setQueryString(builder.queryString);
        setStatement(builder.statement);
        setValid(builder.isValid);
        setRef(builder.isRef);
        setResult(builder.result);
        setTypos(builder.typos);
        setScore(builder.score);
        setExNumber(builder.exNumber);
        setMatchedColumns(builder.matchedColumns);
        setMatchedTables(builder.matchedTables);
        setFragmentValidationResults(builder.fragmentValidationResults);
        setResultMatchScore(builder.resultMatchScore);
        setErrorReason(builder.errorReason);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(QueryData copy) {
        Builder builder = new Builder();
        builder.identifier = copy.getIdentifier();
        builder.queryString = copy.getQueryString();
        builder.statement = copy.getStatement();
        builder.isValid = copy.isValid();
        builder.isRef = copy.isRef();
        builder.result = copy.getResult();
        builder.typos = copy.getTypos();
        builder.exNumber = copy.getExNumber();
        builder.matchedTables = copy.getMatchedTables();
        builder.matchedColumns = copy.getMatchedColumns();
        builder.fragmentValidationResults = copy.getFragmentValidationResults();
        builder.resultMatchScore = copy.getResultMatchScore();
        builder.errorReason = copy.getErrorReason();
        return builder;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isRef() {
        return isRef;
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public int getTypos() {
        return typos;
    }

    public void setTypos(int typos) {
        this.typos = typos;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public int getExNumber() {
        return exNumber;
    }

    public double getMatchedColumns() {
        return matchedColumns;
    }

    public double getMatchedTables() {
        return matchedTables;
    }

    public void setExNumber(int exNumber) {
        this.exNumber = exNumber;
    }

    public void setMatchedColumns(double matchedColumns) {
        this.matchedColumns = matchedColumns;
    }

    public void setMatchedTables(double matchedTables) {
        this.matchedTables = matchedTables;
    }

    public void setFragmentValidationResults(List<FragmentValidationResult> fragmentValidationResults) {
        this.fragmentValidationResults = fragmentValidationResults;
    }

    public List<FragmentValidationResult> getFragmentValidationResults() {
        return fragmentValidationResults;
    }

    public double getResultMatchScore() {
        return resultMatchScore;
    }

    public void setResultMatchScore(double resultMatchScore) {
        this.resultMatchScore = resultMatchScore;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public static final class Builder {
        private String identifier;
        private String queryString;
        private Statement statement;
        private boolean isValid;
        private boolean isRef;
        private List result;
        private int typos;
        private Double score;
        private int exNumber;
        private double matchedColumns;
        private double matchedTables;
        private double resultMatchScore;
        List<FragmentValidationResult> fragmentValidationResults;
        String errorReason;

        private Builder() {
        }

        public Builder withIdentifier(String val) {
            identifier = val;
            return this;
        }

        public Builder withQueryString(String val) {
            queryString = val.toLowerCase().trim();
            return this;
        }

        public Builder withErrorReason(String val) {
            errorReason = val;
            return this;
        }

        public Builder withStatement(Statement val) {
            statement = val;
            return this;
        }

        public Builder withIsValid(boolean val) {
            isValid = val;
            return this;
        }

        public Builder withIsRef(boolean val) {
            isRef = val;
            return this;
        }

        public Builder withResult(List val) {
            result = val;
            return this;
        }

        public Builder withScore(Double val) {
            score = val;
            return this;
        }

        public Builder withTypos(int val) {
            typos = val;
            return this;
        }

        public Builder withExNumber(int val) {
            exNumber = val;
            return this;
        }

        public Builder withColumnMatched(double val) {
            matchedColumns = val;
            return this;
        }

        public Builder withTableMatched(double val) {
            matchedTables = val;
            return this;
        }

        public Builder withResultMatchScore(double val) {
            resultMatchScore = val;
            return this;
        }

        public Builder withFragmentValidationResults(List<FragmentValidationResult> validationResults) {
            fragmentValidationResults = validationResults;
            return this;
        }

        public QueryData build() {
            return new QueryData(this);
        }
    }
}
