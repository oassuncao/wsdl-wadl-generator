package org.apache.cxf.generator.generation.wsdl;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import org.apache.cxf.generator.generation.CustomOutputStreamCodeWriter;
import org.apache.cxf.generator.generation.GeneratorResult;
import org.apache.cxf.generator.generation.WriterResult;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.util.ClassCollector;
import org.apache.cxf.tools.wsdlto.databinding.jaxb.JAXBDataBinding;
import org.springframework.util.ReflectionUtils;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class JAXBDataBindingStream extends JAXBDataBinding implements WriterResult {
// ------------------------------ FIELDS ------------------------------

    private final List<GeneratorResult> results;

// -------------------------- STATIC METHODS --------------------------

    public static URL getCustomJABXBindingURL() {
        return JAXBDataBindingStream.class.getClassLoader().getResource("binding/jabx-binding.xml");
    }

    public static InputSource getCustomJABXBinding() {
        return new InputSource(getCustomJABXBindingURL().toString());
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public JAXBDataBindingStream() {
        this(new ArrayList<>());
    }

    public JAXBDataBindingStream(List<GeneratorResult> results) {
        this.results = results;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DataBindingProfile ---------------------

    @Override
    public void generate(ToolContext context) throws ToolException {
        //Sorry but I needed to rewrite the code, doesn't have any point to override
        boolean initialized = getFieldValue("initialized");
        if (!initialized) {
            initialize(context);
        }

        S2JJAXBModel rawJaxbModelGenCode = getFieldValue("rawJaxbModelGenCode");
        if (rawJaxbModelGenCode == null) {
            return;
        }

        if (context.getErrorListener().getErrorCount() > 0) {
            return;
        }

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            CustomOutputStreamCodeWriter fileCodeWriter = new CustomOutputStreamCodeWriter(stream, "utf-8", results);
            ClassCollector classCollector = context.get(ClassCollector.class);
            for (JClass cls : rawJaxbModelGenCode.getAllObjectFactories()) {
                classCollector.getTypesPackages().add(cls._package().name());
            }
            JCodeModel jcodeModel = rawJaxbModelGenCode.generateCode(null, null);

            jcodeModel.build(fileCodeWriter);

            context.put(JCodeModel.class, jcodeModel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(ToolContext context) throws ToolException {
        context.getJaxbBindingFile().add(getCustomJABXBinding());
        super.initialize(context);
    }

// --------------------- Interface WriterResult ---------------------

    @Override
    public List<GeneratorResult> getResult() {
        return results;
    }

// -------------------------- OTHER METHODS --------------------------

    private <T> T getFieldValue(String fieldName) {
        Field field = ReflectionUtils.findField(JAXBDataBindingStream.class, fieldName);
        ReflectionUtils.makeAccessible(field);
        try {
            return (T) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
