package pl.put.poznan.transformer.rest;

import org.junit.jupiter.api.Test;




import pl.put.poznan.transformer.logic.JSONTransformer;
import pl.put.poznan.transformer.logic.JsonDecoratorBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransformerControllerTest {

    @Test
    public void testFilterBasicUsage() throws Exception {
        TransformerController tc = new TransformerController();
        String[] filter = {"msg2"};
        String[] filter2 = {""};

        JsonDecoratorBuilder mockBuilder = mock(JsonDecoratorBuilder.class);
        JSONTransformer mockFilter = mock(JSONTransformer.class);
        when(mockFilter.decorate("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}")).thenReturn("{\"msg\":\"hello\"}");
        when(mockBuilder.getDecorator("minify",filter,filter2)).thenReturn(mockFilter);

        tc.SetDecoratorBuilder(mockBuilder);

        assertEquals("{\"msg\":\"hello\"}",tc.filter("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}","minify",filter,filter2));
        verify(mockBuilder, times(1)).getDecorator("minify",filter,filter2);
        verify(mockFilter, times(1)).decorate("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}");
    }

    @Test
    public void testFilterOnlyBasicUsage() throws Exception {
        TransformerController tc = new TransformerController();
        String[] filter = {""};
        String[] filter2 = {"msg2"};

        JsonDecoratorBuilder mockBuilder = mock(JsonDecoratorBuilder.class);
        JSONTransformer mockFilter = mock(JSONTransformer.class);
        when(mockFilter.decorate("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}")).thenReturn("{\"msg2\":\"goodbye\"}");
        when(mockBuilder.getDecorator("minify",filter,filter2)).thenReturn(mockFilter);

        tc.SetDecoratorBuilder(mockBuilder);

        assertEquals("{\"msg2\":\"goodbye\"}",tc.filter("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}","minify",filter,filter2));
        verify(mockBuilder, times(1)).getDecorator("minify",filter,filter2);
        verify(mockFilter, times(1)).decorate("{\"msg\":\"hello\",\"msg2\":\"goodbye\"}");
    }


    @Test
    public void testFilterBasicUsagePrettify() throws Exception {
        TransformerController tc = new TransformerController();
        String[] filter = {""};

        JsonDecoratorBuilder mockBuilder = mock(JsonDecoratorBuilder.class);
        JSONTransformer mockPrettify = mock(JSONTransformer.class);
        when(mockPrettify.decorate("{\"json1\":{\"id\":999}}")).thenReturn( "{\r\n" +
                                                                                                        "  \"json1\" : {\r\n" +
                                                                                                        "    \"id\" : 999\r\n" +
                                                                                                        "  }\r\n" +
                                                                                                        "}"
        );
        when(mockBuilder.getDecorator("prettify",filter,filter)).thenReturn(mockPrettify);
        tc.SetDecoratorBuilder(mockBuilder);

        assertEquals(  "{\r\n" +
                                "  \"json1\" : {\r\n" +
                                "    \"id\" : 999\r\n" +
                                "  }\r\n" +
                                "}"
                ,tc.filter("{\"json1\":{\"id\":999}}","prettify",filter,filter));
        verify(mockBuilder, times(1)).getDecorator("prettify",filter,filter);
        verify(mockPrettify, times(1)).decorate("{\"json1\":{\"id\":999}}");
    }

}