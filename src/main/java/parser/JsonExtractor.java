package parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import model.QueryDataSet;

import java.util.ArrayList;
import java.util.List;

public class JsonExtractor {

    public List<QueryDataSet> extractData(JsonArray jsonArray) {
        JsonElement root = jsonArray.get(0);
        List<QueryDataSet> queryDataSets = new ArrayList<>();
        for (JsonElement query: root.getAsJsonArray()) {
            JsonArray queryArray = query.getAsJsonArray();
            String name = queryArray.get(1).getAsString();
            String surname = queryArray.get(0).getAsString();
            String email = queryArray.get(2).getAsString();
            List<String> queries = new ArrayList<>();
            for (int i = 10; i < queryArray.size(); i++){
                queries.add(queryArray.get(i).getAsString().replaceAll("\\h"," "));
            }
            queryDataSets.add(QueryDataSet.newBuilder()
                    .withName(name)
                    .withSurname(surname)
                    .withEmail(email)
                    .withExcercises(queries)
                    .build());
        }
        return queryDataSets;
    }
}
