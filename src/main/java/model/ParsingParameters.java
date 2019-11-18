package model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParsingParameters {
    List<String> referenceQueries;
    Map<String,List<String>> typos;
    Map<String,String> codeFragments;
    List<String> usedColumns;
    List<String> usedTables;

    public List<String> getReferenceQueries() {
        return referenceQueries;
    }

    public Map<String, List<String>> getTypos() {
        return typos;
    }

    public Map<String, String> getCodeFragments() {
        return codeFragments;
    }

    public Multimap<String, String> getTyposAsMultimap() {
        Multimap<String, String> typoMap = ArrayListMultimap.create();
        typos.forEach(typoMap::putAll);
        return typoMap;
    }

    public List<String> getUsedColumns() {
        return usedColumns;
    }

    public List<String> getUsedTables() {
        return usedTables;
    }
}
