package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.transformer.logic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * TransformerController jest odpowiedzialny za obsługę żądań HTTP do formatowania plików JSON.
 *
 * @author Alicja Lis, Józef Godlewski
 */
@RestController
@RequestMapping("/transformer")
public class TransformerController {
    private JsonDecoratorBuilder builder;

    /**
     * Repozytorium do przechowywania JSONów.
     */
    private final Map<String, JSON> jsonRepository = new HashMap<>();

    /**
     * Przy użyciu obiektu logger zapisywane są informacje dotyczące zapytań oraz wystąpionych błędów
     */
    private static final Logger logger = LoggerFactory.getLogger(TransformerController.class);

    /**
     * Konstruktor klasy TransformerController.
     */
    public TransformerController() {
        this.builder = new JsonDecoratorBuilder();
    }



    /**
     * Porównuje dwa JSONy i zwraca różnice pomiędzy nimi.
     *
     * @param objectNode JSONy do porównania zapakowane w strukture {"json1":{jsonToBecompared},"json2":{jsonToBecompared2}}
     * @return różnice między dwoma JSONami
     */
    @PostMapping("/compare/")
    public String compare(@RequestBody ObjectNode objectNode) {
        try {
            String s1 = new ObjectMapper().writeValueAsString(objectNode.get("json1"));
            String s2 = new ObjectMapper().writeValueAsString(objectNode.get("json2"));
            logger.debug("Compare POST - json: "+s1+" "+s2);
            JSONTransformer transform = new JsonComparator(new JsonDecorator( new BasicJsonTransformer()),s1);
            return transform.decorate(s2);
        } catch (Exception e) {
            return "Wrong input";
        }
    }

    /**
     * Filtruje plik JSON na podstawie podanych parametrów.
     *
     * @param text JSON w foramcie tekstu do przekształcenia
     * @param format format jsona wynikowaego: minify - json zminifikowany, prettify - json czytelny
     * @param filterParameter opcjonalna lista własności do odfiltrowania z jsona
     * @param filterOnlyParameter opcjonalna lista jedynych własności do pozostawienia
     * @return JSON po przekształceniach
     */
    @PostMapping("/")
    public String filter(@RequestBody String text, @RequestParam(value="format", defaultValue="minify") String format,
                         @RequestParam(value="filter", defaultValue="") String[] filterParameter,
                         @RequestParam(value="filterOnly", defaultValue="") String[] filterOnlyParameter) {
        logger.debug("Filter POST - json: "+text);

        JSONTransformer transform = builder.getDecorator(format,filterParameter, filterOnlyParameter);
        try {
            return transform.decorate(text);
        } catch (Exception e) {
            return "Wrong input";
        }
    }


    /**
     * Dodawanie nowego pola do JSON. Pole może być nowe lub napisywać wartość poprzedniego.
     * Aby wstawiać pola jak pod element można wykorzystać składnię \ (np. master\detail)
     *
     * @param text JSON w foramcie tekstu do przekształcenia
     * @param format format jsona wynikowaego: minify - json zminifikowany, prettify - json czytelny
     * @param field nazwa pola wsawianego lub ścieżka\nazwa
     * @param value wartość wstawianego pola
     * @return JSON po przekształceniach
     */
    @PostMapping("/insert")
    public String insert(@RequestBody String text, @RequestParam(value="format", defaultValue="minify") String format,
                         @RequestParam(value="field", defaultValue="") String field,
                         @RequestParam(value="value", defaultValue="") String value) throws IOException {
        logger.debug("Insert POST - json: "+text);

        JSONTransformer transformer = new JsonDecorator( new BasicJsonTransformer());
        FieldInserter inserter = new FieldInserter(transformer, field,value);
        if(format.equals("prettify")) {
            return new PrettifyDecorator(inserter).decorate(text);
        }
        return inserter.decorate(text);
    }

    /**
     * Tworzy nowego JSONa i zapisuje go do repozytorium.
     *
     * @param jsonString JSON do zapisania
     * @return identyfikator zapisanego JSONa
     */
    @PostMapping("/json")
    public String createJSON(@RequestBody String jsonString) {
        logger.debug("Create json POST - json: "+jsonString);
        JSON json = new JSON(jsonString);
        String id = UUID.randomUUID().toString();
        jsonRepository.put(id, json);
        return id;
    }

    /**
     * Pobiera plik JSON z repozytorium na podstawie podanego identyfikatora.
     *
     * @param id identyfikator JSONa do pobrania
     * @return JSON po pobraniu
     */
    @GetMapping("/json/{id}")
    public String getJSON(@PathVariable String id) {
        logger.debug("GET json, id - : "+id);
        JSON json = jsonRepository.get(id);
        if (json == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "JSON with id " + id + " not found");
        }
        try {
            return json.getString();
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON", e);
        }
    }

    /**
     * Setter for JsonDecoratorBuilder builder.
     *
     * @param Builder JsonDecoratorBuilder
     */
    public void SetDecoratorBuilder(JsonDecoratorBuilder Builder) {
        this.builder = Builder;
    }
}
