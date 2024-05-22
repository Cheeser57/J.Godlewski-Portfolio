package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PrettifyDecorator extends JsonDecorator{
    public PrettifyDecorator(JSONTransformer json) {
        super(json);
    }

    public String decorate(String jsonString) throws IOException {
        return prettify(super.decorate(jsonString));
    }

    private String prettify(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return "Invalid json";
        }
    }
}
