package pl.put.poznan.transformer.logic;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;



class FieldInserterTest {

    //Basic
    @Test
    void testDecorate() throws IOException {
        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());
        FieldInserter inserter = new FieldInserter(transformer, "msg\\hello","text");

        assertEquals("{\"msg\":{\"goodbye\":\"text\",\"hello\":\"text\"}}",inserter.decorate("{\"msg\":{\"goodbye\":\"text\"}}}"));
    }

}