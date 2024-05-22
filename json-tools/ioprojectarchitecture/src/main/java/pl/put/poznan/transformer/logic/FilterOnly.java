package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.*;

/**
 * Klasa FilterOnly służy do filtrowania pól JSON, usuwając wszystkie pola
 * Zawiera metody do filtrowania tekstu.
 */
public class FilterOnly extends JsonDecorator {
    private final String[] filterParameters;

    /**
     * Konstruktor klasy FilterOnly.
     *
     * @param json           Obiekt klasy JSONTransformer.
     * @param filterParameter Tablica kluczy filtrowania, do pozostawienia w jsonie.
     */
    public FilterOnly(JSONTransformer json, String[] filterParameter) {
        super(json);
        filterParameters = filterParameter;
    }

    /**
     * Dekorator do filtrowania jsona
     *
     * @param text json do filtrowania
     * @return Zwraca json z polami z tylko podanymi kluczami
     */
    public String decorate(String text) throws IOException {
        return filterOutString(super.decorate(text));
    }

    /**
     * Metoda filtrująca jsona
     *
     * @param text Json do filtrowania.
     * @return Zwraca przefiltrowany Json.
     */
    private String filterOutString(String text) {
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
            return "Failure during filtering proccess";
        }
    }

    /**
     * Metoda rekurencyjnie filtrująca plik JSON.
     *
     * @param jsonNode Węzeł JSON do filtrowania.
     * @param filter  Parametry filtrowania.
     * @param forRemoval Węzły do usunięcia.
     * @param forRemovalName Nazwy węzłów do usunięcia.
     * @return Zwraca liczbę węzłów, które spełniają warunki filtrowania.
     */
    private int filterNodeRecursive(JsonNode jsonNode, String[] filter, List<JsonNode> forRemoval, ArrayList<String> forRemovalName) {
        int numberOfFilterNodesAbove = 0;
        int numberOfFilterNodesCurrent = 0;
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                numberOfFilterNodesAbove = filterNodeRecursive((JsonNode) field.getValue(), filter,forRemoval,forRemovalName);

                if(Arrays.asList(filter).contains(field.getKey())) {
                    numberOfFilterNodesCurrent += 1;
                } else if(numberOfFilterNodesAbove == 0) {
                    forRemoval.add(jsonNode);
                    forRemovalName.add(field.getKey());
                }
            }

        } else if (jsonNode.isArray()) {
            ArrayNode arrayField = (ArrayNode) jsonNode;
            for(int i=0; i < arrayField.size(); i++) {
                numberOfFilterNodesAbove = filterNodeRecursive(arrayField.get(i), filter,forRemoval, forRemovalName);
            }

        }
        return numberOfFilterNodesCurrent + numberOfFilterNodesAbove;
    }
}