package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;



/**
 * Dekorator do filtrowania kluczy z jsona
 * Zawiera metody do filtrowania tekstu.
 */
public class Filter extends JsonDecorator  {
    private final String[] filterParameters;

    /**
     * Konstruktor klasy Filter
     *
     * @param json Obiekt klasy JSONTransformer.
     * @param parameters Tablica kluczy do odfiltrowania z jsona.
     */
    public Filter(JSONTransformer json, String[] parameters) {
        super(json);
        filterParameters = parameters;
    }

    /**
     * Dekorator do filtrowania jsona
     *
     * @param text json do filtrowania
     * @return Zwraca json bez pól z podanych kluczy
     */
    public String decorate(String text)  {
        try {
            return filterString(super.decorate(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Metoda filtrująca tekst.
     *
     * @param text Tekst do filtrowania.
     * @return Zwraca przefiltrowany tekst.
     */
    private String filterString(String text) throws IOException {
        JSON jsNode = new JSON(text);

        ArrayList<JsonNode> toBeRemovedField = new ArrayList<>();
        ArrayList<String> toBeRemovedFieldName = new ArrayList<>();
        filterNodeRecursive(jsNode.get(),filterParameters, toBeRemovedField, toBeRemovedFieldName);
        for(int i =0;i<toBeRemovedField.size();i++) {
            ((ObjectNode) toBeRemovedField.get(i)).remove(toBeRemovedFieldName.get(i));
        }


        try {
            return jsNode.getString();
        } catch (JsonProcessingException e) {
            return "Failure during filtering process";
        }
    }


    /**
     * Metoda rekurencyjnie filtrująca plik JSON.
     *
     * @param jsonNode Węzeł JSON do filtrowania.
     * @param filter  Parametry filtrowania.
     * @param forRemoval Węzły do usunięcia.
     * @param forRemovalName Nazwy węzłów do usunięcia.
     */
    private void filterNodeRecursive(JsonNode jsonNode, String[] filter, List<JsonNode> forRemoval, ArrayList<String> forRemovalName) {

        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            fields.forEachRemaining(field -> {

                if(Arrays.asList(filter).contains(field.getKey())) {
                    forRemoval.add(jsonNode);
                    forRemovalName.add(field.getKey());
                } else {
                    filterNodeRecursive((JsonNode) field.getValue(), filter,forRemoval, forRemovalName);
                }
            });
        } else if (jsonNode.isArray()) {
            ArrayNode arrayField = (ArrayNode) jsonNode;
            arrayField.forEach(node -> {
                filterNodeRecursive(node, filter,forRemoval, forRemovalName);
            });
        }
    }
}