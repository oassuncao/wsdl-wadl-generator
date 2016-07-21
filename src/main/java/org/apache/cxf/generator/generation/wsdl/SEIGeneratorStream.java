package org.apache.cxf.generator.generation.wsdl;

import org.apache.cxf.generator.generation.GeneratorResult;
import org.apache.cxf.generator.generation.GeneratorUtils;
import org.apache.cxf.generator.generation.WriterResult;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class SEIGeneratorStream extends SEIGenerator implements WriterResult {
// ------------------------------ FIELDS ------------------------------

    private final List<GeneratorResult> results;

// --------------------------- CONSTRUCTORS ---------------------------

    public SEIGeneratorStream() {
        this(new ArrayList<>());
    }

    public SEIGeneratorStream(List<GeneratorResult> results) {
        this.results = results;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface WriterResult ---------------------

    @Override
    public List<GeneratorResult> getResult() {
        return results;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected Writer parseOutputName(String packageName, String filename, String ext) throws ToolException {
        return GeneratorUtils.parseOutputName(packageName, filename, ext, results);
    }
}
