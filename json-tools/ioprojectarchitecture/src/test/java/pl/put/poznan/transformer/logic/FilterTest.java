package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    @Test
    void testDecorateSimpleNested() {

        String testJSON = "{\"name\": \"Kowalski\", \"details\": {\"age\": 20, \"city\": \"Poznan\"}}";
        String[] parameters = {"age"};

        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());
        Filter filter = new Filter(transformer, parameters);
        assertEquals("{\"name\":\"Kowalski\",\"details\":{\"city\":\"Poznan\"}}",
                filter.decorate(testJSON));
    }

    @Test
    void testDecorateMultipleNested() {

        String testJSON = "{\"id\":\"0001\",\"type\":\"donut\",\"name\":\"Cake\",\"ppu\":0.55,\"batters\":{\"batter\":[{\"id\":\"1001\",\"type\":\"Regular\"},{\"id\":\"1002\",\"type\":\"Chocolate\"},{\"id\":\"1003\",\"type\":\"Blueberry\"},{\"id\":\"1004\",\"type\":\"Devil's Food\"}]},\"topping\":[{\"id\":\"5001\",\"type\":\"None\"},{\"id\":\"5002\",\"type\":\"Glazed\"},{\"id\":\"5005\",\"type\":\"Sugar\"},{\"id\":\"5007\",\"type\":\"Powdered Sugar\"},{\"id\":\"5006\",\"type\":\"Chocolate with Sprinkles\"},{\"id\":\"5003\",\"type\":\"Chocolate\"},{\"id\":\"5004\",\"type\":\"Maple\"}]}";
        String[] parameters = {"id"};

        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());
        Filter filter = new Filter(transformer, parameters);
        assertEquals("{\"type\":\"donut\",\"name\":\"Cake\",\"ppu\":0.55,\"batters\":{\"batter\":[{\"type\":\"Regular\"},{\"type\":\"Chocolate\"},{\"type\":\"Blueberry\"},{\"type\":\"Devil's Food\"}]},\"topping\":[{\"type\":\"None\"},{\"type\":\"Glazed\"},{\"type\":\"Sugar\"},{\"type\":\"Powdered Sugar\"},{\"type\":\"Chocolate with Sprinkles\"},{\"type\":\"Chocolate\"},{\"type\":\"Maple\"}]}",
                filter.decorate(testJSON));
    }

    @Test
    void testDecorateMultipleParameters() {

        String testJSON = "{\"school_name\":\"Dunder Miflin\",\"class\":\"Year 1\",\"info\":{\"president\":\"Michael Scott\",\"address\":\"Scranton, Pennsylvania\",\"contacts\":{\"email\":\"admin@e.com\",\"tel\":\"123456789\"}},\"students\":[{\"id\":\"A1\",\"name\":\"Jim\",\"math\":60,\"physics\":66,\"chemistry\":61},{\"id\":\"A2\",\"name\":\"Dwight\",\"math\":89,\"physics\":76,\"chemistry\":51},{\"id\":\"A3\",\"name\":\"Kevin\",\"math\":79,\"physics\":90,\"chemistry\":78}]}";
        String[] parameters = {"id","info"};

        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());
        Filter filter = new Filter(transformer, parameters);
        assertEquals("{\"school_name\":\"Dunder Miflin\",\"class\":\"Year 1\",\"students\":[{\"name\":\"Jim\",\"math\":60,\"physics\":66,\"chemistry\":61},{\"name\":\"Dwight\",\"math\":89,\"physics\":76,\"chemistry\":51},{\"name\":\"Kevin\",\"math\":79,\"physics\":90,\"chemistry\":78}]}",
                filter.decorate(testJSON));


    }
}