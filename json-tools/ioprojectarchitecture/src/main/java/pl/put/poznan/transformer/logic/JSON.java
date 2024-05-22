package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Klasa JSON służy do przetwarzania ciągów JSON w formacie JsonNode.
 * Wykorzystuje bibliotekę Jackson do odczytywania ciągów JSON i konwersji ich na obiekty JsonNode
 * które mogą być następnie manipulowane i przekształcane z powrotem na ciągi JSON
 */
public class JSON {
    /**
     * Prywatna zmienna typu JsonNode przechowująca obecnie przetwarzany obiekt JSON.
     */
    private JsonNode node ;

    /**
     * Konstruktor klasy JSON. Przyjmuje ciąg JSON jako argument i próbuje go przekształcić na obiekt JsonNode.
     * W przypadku błędu podczas przetwarzania ciągu JSON, konstruktor rzuca wyjątek RuntimeException.
     *
     * @param jsonString Ciąg JSON do przetworzenia.
     */
    public JSON(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            node = objectMapper.readValue(jsonString, JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metoda zwracająca obecny obiekt JsonNode.
     *
     * @return obiekt JsonNode.
     */
    public JsonNode get() {return node;}

    /**
     * Metoda konwertująca obecny obiekt JsonNode z powrotem na ciąg JSON.
     * W przypadku błędu podczas konwersji, metoda rzuca wyjątek JsonProcessingException.
     *
     * @return Ciąg JSON reprezentujący obecny obiekt JsonNode.
     * @throws JsonProcessingException Jeżeli wystąpi błąd podczas konwersji obiektu JsonNode na ciąg JSON.
     */
    public String getString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(node);
    }
}
