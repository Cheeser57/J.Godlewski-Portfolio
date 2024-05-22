package pl.put.poznan.transformer.logic;

import java.io.IOException;

/**
 * Klasa JsonDecorator implementuje interfejs JSONTransformer.
 * Służy do dekorowania obiektów implementujących ten sam interfejs, dodając do nich dodatkowe funkcje.
 */
public class JsonDecorator implements JSONTransformer {
    /**
     * Zmienna chroniona typu JSONTransformer przechowująca obecnie dekorowany obiekt.
     */
    protected JSONTransformer transformer;

    /**
     * Konstruktor klasy JsonDecorator. Przyjmuje obiekt implementujący interfejs JSONTransformer jako argument.
     *
     * @param t Obiekt do dekorowania.
     */
    public JsonDecorator(JSONTransformer t) {
        transformer = t;
    }

    /**
     * Metoda dekorująca. Wywołuje metodę dekorującą na obecnym obiekcie.
     *
     * @param JsonString Ciąg JSON do dekorowania.
     * @return Zwraca dekorowany ciąg JSON.
     */
    @Override
    public String decorate(String JsonString) throws IOException {
        return transformer.decorate(JsonString);
    }
}
