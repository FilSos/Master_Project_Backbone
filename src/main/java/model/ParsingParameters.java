package model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import query.QueryData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParsingParameters {
    List<String> referenceQueries;
    Map<String,List<String>> typos;
    List<CodeFragment> codeFragments;
    List<String> usedColumns;
    List<String> usedTables;

    public List<String> getReferenceQueriesText() {
        return referenceQueries;
    }

    public Map<String, List<String>> getTypos() {
        return typos;
    }

    public List<CodeFragment> getCodeFragments() {
        return codeFragments;
    }

    public Multimap<String, String> getTyposAsMultimap() {
        Multimap<String, String> typoMap = ArrayListMultimap.create();
        typos.forEach(typoMap::putAll);
        return typoMap;
    }

    public List<QueryData> getReferenceQueries(){
        return referenceQueries.stream()
                .map(queryText -> new QueryData(queryText,"reference", true, referenceQueries.indexOf(queryText)))
                .collect(Collectors.toList());
    }

    public List<String> getUsedColumns() {
        return usedColumns;
    }

    public List<String> getUsedTables() {
        return usedTables;
    }


}
