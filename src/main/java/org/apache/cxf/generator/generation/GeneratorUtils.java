package org.apache.cxf.generator.generation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class GeneratorUtils {
// -------------------------- STATIC METHODS --------------------------

    public static Writer parseOutputName(String packageName, String filename, String extension, List<GeneratorResult> results) {
        GeneratorResult result = new GeneratorResult();
        result.setFilename(filename);
        result.setPackageName(packageName);
        result.setExtension(extension);
        result.setStream(new ByteArrayOutputStream());
        results.add(result);
        return new OutputStreamWriter(result.getStream(), Charset.defaultCharset());
    }
}
