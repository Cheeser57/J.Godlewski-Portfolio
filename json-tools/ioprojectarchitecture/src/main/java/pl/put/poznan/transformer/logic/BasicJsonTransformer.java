package pl.put.poznan.transformer.logic;


/**
 * Podstawowa implementacja interface-u JSONTransformer
 */
public class BasicJsonTransformer implements JSONTransformer{

    /**
     * Podtswowa transformacja - zwraca string bez zmian
     * @param JsonString json do transformacji
     * @return transformowany json
     */
    @Override
    public String decorate(String JsonString) {
        return JsonString;
    }
}
