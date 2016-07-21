package org.apache.cxf.generator.generation;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.OutputStreamCodeWriter;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class CustomOutputStreamCodeWriter extends OutputStreamCodeWriter {
// ------------------------------ FIELDS ------------------------------

    private final List<GeneratorResult> results;

// --------------------------- CONSTRUCTORS ---------------------------

    public CustomOutputStreamCodeWriter(OutputStream os, String encoding, List<GeneratorResult> results) {
        super(os, encoding);
        this.results = results;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        GeneratorResult generatorResult = new GeneratorResult(pkg.name(), FilenameUtils.getBaseName(fileName), FilenameUtils.getExtension(fileName), stream);
        if (!ignoreFile(generatorResult)) {
            results.add(generatorResult);
        }
        return stream;
    }

    protected boolean ignoreFile(GeneratorResult generatorResult) {
        switch (generatorResult.getFilename()) {
            case "package-info":
                return true;
            default:
                return false;
        }
    }
}