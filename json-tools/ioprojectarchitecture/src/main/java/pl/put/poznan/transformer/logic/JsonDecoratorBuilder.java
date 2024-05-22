package pl.put.poznan.transformer.logic;



/**
 * Klasa JsonDecoratorBuilder jest klasą do tworzenia obiektów implementujących interfejs JSONTransformer.
 * Buduje obiekt, dodając do niego różne dekoratory w zależności od podanych parametrów.
 */
public class JsonDecoratorBuilder {

    /**
     * Metoda tworząca i zwracająca obiekt implementujący interfejs JSONTransformer.
     * Na podstawie podanych parametrów opakowuje transformer w odpowiednie dekoratory.
     *
     * @param format Format, w którym ma być zwrócony ciąg JSON. Może to być "minify" lub "prettify".
     * @param filterParameter Tablica zawierająca opcjonelne parametry, które mają być odfiltrowane z json.
     * @param filterOnlyParameter Tablica zawierająca opcjonelne parametry do pozostawienia w json(odfiltorowania pozostałych)
     * @return Zwraca obiekt implementujący interfejs JSONTransformer.
     */
    public JSONTransformer getDecorator(String format, String[] filterParameter,String[] filterOnlyParameter) {
        try {
            JSONTransformer transformer = new JsonDecorator(new BasicJsonTransformer());

            if (filterParameter.length != 0) {
                transformer = new Filter(transformer, filterParameter);
            }

            if (filterOnlyParameter.length != 0) {
                transformer = new FilterOnly(transformer, filterOnlyParameter);
            }

            if (format.equals("minify")) {
                transformer = new MinifyDecorator(transformer);
            } else if (format.equals("prettify")) {
                transformer = new PrettifyDecorator(transformer);
            }

            return transformer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

