package org.apache.cxf.generator.generation;

import java.io.ByteArrayOutputStream;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class GeneratorResult {
// ------------------------------ FIELDS ------------------------------

    private String packageName;
    private String filename;
    private String extension;
    private ByteArrayOutputStream stream;

// --------------------------- CONSTRUCTORS ---------------------------

    public GeneratorResult() {
    }

    public GeneratorResult(String packageName, String filename, String extension, ByteArrayOutputStream stream) {
        this.stream = stream;
        this.packageName = packageName;
        this.filename = filename;
        this.extension = extension;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }
}