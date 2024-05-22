package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class FilterOnlyTest {

    @Test
    void testDecorateNested() throws IOException {

        String testJSON = "{\"name\": \"Jan\",\"surname\": \"Kowalski\", \"details\": {\"age\": 20, \"city\": \"Poznan\"},\"sex\": \"male\"}";
        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());


        String[] parameters = {"surname"};
        FilterOnly filter = new FilterOnly(transformer, parameters);
        assertEquals("{\"surname\":\"Kowalski\"}",
                filter.decorate(testJSON));

        String[] parameters2 = {"city"};
        FilterOnly filter2 = new FilterOnly(transformer, parameters2);
        assertEquals("{\"details\":{\"city\":\"Poznan\"}}",
                filter2.decorate(testJSON));

    }

    @Test
    void testDecorateMultipleNested() throws IOException {

        String testJSON = "{\"id\":\"0001\",\"type\":\"donut\",\"name\":\"Cake\",\"ppu\":0.55,\"batters\":{\"batter\":[{\"id\":\"1001\",\"type\":\"Regular\"},{\"id\":\"1002\",\"type\":\"Chocolate\"},{\"id\":\"1003\",\"type\":\"Blueberry\"},{\"id\":\"1004\",\"type\":\"Devil's Food\"}]},\"topping\":[{\"id\":\"5001\",\"type\":\"None\"},{\"id\":\"5002\",\"type\":\"Glazed\"},{\"id\":\"5005\",\"type\":\"Sugar\"},{\"id\":\"5007\",\"type\":\"Powdered Sugar\"},{\"id\":\"5006\",\"type\":\"Chocolate with Sprinkles\"},{\"id\":\"5003\",\"type\":\"Chocolate\"},{\"id\":\"5004\",\"type\":\"Maple\"}]}";
        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());


        String[] parameters = {"type"};
        FilterOnly filter = new FilterOnly(transformer, parameters);
        assertEquals("{\"type\":\"donut\",\"batters\":{\"batter\":[{\"type\":\"Regular\"},{\"type\":\"Chocolate\"},{\"type\":\"Blueberry\"},{\"type\":\"Devil's Food\"}]},\"topping\":[{\"type\":\"None\"},{\"type\":\"Glazed\"},{\"type\":\"Sugar\"},{\"type\":\"Powdered Sugar\"},{\"type\":\"Chocolate with Sprinkles\"},{\"type\":\"Chocolate\"},{\"type\":\"Maple\"}]}",
                filter.decorate(testJSON));
    }

    @Test
    void testDecorateMultipleParameters() throws IOException {

        String testJSON = "{\"school_name\":\"Dunder Miflin\",\"class\":\"Year 1\",\"info\":{\"president\":\"Michael Scott\",\"address\":\"Scranton, Pennsylvania\",\"contacts\":{\"email\":\"admin@e.com\",\"tel\":\"123456789\"}},\"students\":[{\"id\":\"A1\",\"name\":\"Jim\",\"math\":60,\"physics\":66,\"chemistry\":61},{\"id\":\"A2\",\"name\":\"Dwight\",\"math\":89,\"physics\":76,\"chemistry\":51},{\"id\":\"A3\",\"name\":\"Kevin\",\"math\":79,\"physics\":90,\"chemistry\":78}]}";
        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());


        String[] parameters = {"school_name","name"};
        FilterOnly filter = new FilterOnly(transformer, parameters);
        assertEquals("{\"school_name\":\"Dunder Miflin\",\"students\":[{\"name\":\"Jim\"},{\"name\":\"Dwight\"},{\"name\":\"Kevin\"}]}",
                filter.decorate(testJSON));

    }

}
