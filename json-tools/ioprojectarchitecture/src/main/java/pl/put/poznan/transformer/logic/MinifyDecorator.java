package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Klasa MinifyDecorator służy do minifikacji jsona
 */
public class MinifyDecorator extends JsonDecorator {

    /**
     * Konstruktor klasy MinifyDecorator.
     * @param json Obiekt klasy JSONTransformer.
     */
    public MinifyDecorator(JSONTransformer json) {
        super(json);
    }


    /**
     * Dekorator do minifikacji jsona
     *
     * @param jsonString json do zminifikowania
     * @return Zwraca zminifikowanego jsona
     */
    public String decorate(String jsonString) throws IOException {
        return minify(super.decorate(jsonString));
    }


    /**
     * Metoda minifikująca jsona za pomocą konwersji biblioteką Jackson
     *
     * @param jsonString Tekst do minifikacji.
     * @return Zwraca zminifikowany tekst.
     */
    private String minify(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return "Invalid json";
        }
    }
}
