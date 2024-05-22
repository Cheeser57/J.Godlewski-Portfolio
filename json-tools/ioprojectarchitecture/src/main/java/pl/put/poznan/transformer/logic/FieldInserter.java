package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class FieldInserter extends JsonDecorator {
    private final String field;
    private final String value;
    private String[] properties;


    public FieldInserter(JSONTransformer json, String Field, String Value) {
        super(json);
        field = Field;
        value = Value;
    }

    public String decorate(String text) throws IOException {
        return addField(super.decorate(text));
    }

    public String addField(String text) {
        properties = field.split(Pattern.quote("\\"));
        JSON jsNode = new JSON(text);

        AddNodeRecursive(jsNode.get(),0);
        try {
            return jsNode.getString();
        } catch (JsonProcessingException e) {
            return "Failure during inserting process";
        }
    }

    private int AddNodeRecursive(JsonNode jsonNode,int atNode) {
        if(atNode == properties.length-1) {
            if(jsonNode.isObject()) {
                ((ObjectNode) jsonNode).put(properties[atNode], value);
            }
            return 1;
        }

        int change = 0;
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();

                if(field.getKey().equals(properties[atNode])) {
                    //System.out.print(field.getKey()+properties[atNode]);
                    change = AddNodeRecursive((JsonNode) field.getValue(), atNode+1);
                }
            }
        }

        if(change == 0) {
            ((ObjectNode) jsonNode).putObject(properties[atNode]);
            AddNodeRecursive(jsonNode, atNode);
        }
        return 1;
    }

}
