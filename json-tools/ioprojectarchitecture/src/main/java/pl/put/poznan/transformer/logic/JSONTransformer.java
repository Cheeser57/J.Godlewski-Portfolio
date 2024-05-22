package pl.put.poznan.transformer.logic;

import java.io.IOException;

public interface JSONTransformer {
    String decorate(String JsonString) throws IOException;
}
