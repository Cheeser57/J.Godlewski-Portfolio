package pl.put.poznan.transformer.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import pl.put.poznan.transformer.logic.JSONTransformer;
import pl.put.poznan.transformer.logic.JsonDecoratorBuilder;
import pl.put.poznan.transformer.logic.BasicJsonTransformer;
import pl.put.poznan.transformer.logic.MinifyDecorator;
import pl.put.poznan.transformer.logic.PrettifyDecorator;
import pl.put.poznan.transformer.logic.Filter;
import pl.put.poznan.transformer.logic.FilterOnly;

import java.io.IOException;

class JsonDecoratorBuilderTest {


    @Test
    void test_getDecorator_shouldReturnBasicJsonTransformer_whenNoParameters() throws IOException {
        // Arrange
        JsonDecoratorBuilder builder = new JsonDecoratorBuilder();
        String format = "minify";
        String[] filterParameter = {};
        String[] filterOnlyParameter = {};

        // Act
        JSONTransformer transformer = builder.getDecorator(format, filterParameter, filterOnlyParameter);

        // Assert
        assertTrue(transformer instanceof JSONTransformer);
        assertEquals("{\"json1\":{\"id\":999,\"value\":\"content\"},\"json2\":{\"id\":999}}",
                     transformer.decorate("{\"json1\":{\"id\":999,\"value\":\"content\"},\"json2\":{\"id\":999}}"));
    }


    @Test
    void test_getDecorator_shouldReturnErrorWhenWrongJson() throws IOException {
        // Arrange
        JsonDecoratorBuilder builder = new JsonDecoratorBuilder();
        String format = "minify";
        String[] filterParameter = {};
        String[] filterOnlyParameter = {};

        // Act
        JSONTransformer transformer = builder.getDecorator(format, filterParameter, filterOnlyParameter);

        // Assert
        assertTrue(transformer instanceof JSONTransformer);
        assertEquals("Invalid json",
                transformer.decorate("{\"json1\":{\"id\""));
    }

    @Test
    void test_getDecorator_shouldReturnFilteredDecorator_whenFilterParametersPresent() throws IOException {
        // Arrange
        JsonDecoratorBuilder builder = new JsonDecoratorBuilder();
        String format = "minify";
        String[] filterParameter = {"value"};
        String[] filterOnlyParameter = {};

        // Act
        JSONTransformer transformer = builder.getDecorator(format, filterParameter, filterOnlyParameter);

        // Assert
        assertTrue(transformer instanceof JSONTransformer);
        assertEquals("{\"json1\":{\"id\":999},\"json2\":{\"id\":999}}",
                transformer.decorate("{\"json1\":{\"id\":999,\"value\":\"content\"},\"json2\":{\"id\":999}}"));
    }

    @Test
    void test_getDecorator_shouldReturnFilterOnlyDecorator_whenFilterOnlyParametersPresent() throws IOException {
        // Arrange
        JsonDecoratorBuilder builder = new JsonDecoratorBuilder();
        String format = "minify";
        String[] filterParameter = {};
        String[] filterOnlyParameter = {"id"};

        // Act
        JSONTransformer transformer = builder.getDecorator(format, filterParameter, filterOnlyParameter);

        // Assert
        assertTrue(transformer instanceof JSONTransformer);
        assertEquals("{\"json1\":{\"id\":999},\"json2\":{\"id\":999}}",
                transformer.decorate("{\"json1\":{\"id\":999,\"value\":\"content\"},\"json2\":{\"id\":999}}"));
    }

    //    @Test
//    void test_getDecorator_shouldReturnMinifyDecorator_whenFormatIsPrettify() throws IOException {
//        // Arrange
//        JsonDecoratorBuilder builder = new JsonDecoratorBuilder();
//        String format = "prettify";
//        String[] filterParameter = {};
//        String[] filterOnlyParameter = {};
//
//        // Act
//        JSONTransformer transformer = builder.getDecorator(format, filterParameter, filterOnlyParameter);
//
//        // Assert
//        assertTrue(transformer instanceof JSONTransformer);
//        assertEquals("{\r\n" +
//                              "  \"json1\" : {\r\n" +
//                              "    \"id\" : 999\r\n" +
//                              "  }\r\n" +
//                              "}",
//                transformer.decorate("{\"json1\":{\"id\":999}}"));
//    }

}
