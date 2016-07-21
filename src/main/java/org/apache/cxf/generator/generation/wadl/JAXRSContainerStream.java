package org.apache.cxf.generator.generation.wadl;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.generator.generation.GeneratorResult;
import org.apache.cxf.generator.generation.WriterResult;
import org.apache.cxf.tools.common.ClassUtils;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.common.toolspec.ToolSpec;
import org.apache.cxf.tools.util.ClassCollector;
import org.apache.cxf.tools.wadlto.WadlToolConstants;
import org.apache.cxf.tools.wadlto.jaxb.CustomizationParser;
import org.apache.cxf.tools.wadlto.jaxrs.JAXRSContainer;
import org.apache.cxf.tools.wadlto.jaxrs.SourceGenerator;
import org.springframework.util.ReflectionUtils;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class JAXRSContainerStream extends JAXRSContainer implements WriterResult {
// ------------------------------ FIELDS ------------------------------

    private final InputStream stream;
    private final List<GeneratorResult> results;

// --------------------------- CONSTRUCTORS ---------------------------

    public JAXRSContainerStream(ToolSpec toolspec, InputStream stream) throws Exception {
        super(toolspec);
        this.stream = stream;
        this.results = new ArrayList<>();
    }

    public JAXRSContainerStream(ToolSpec toolspec, InputStream stream, List<GeneratorResult> results) throws Exception {
        super(toolspec);
        this.stream = stream;
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
    public void execute() throws ToolException {
        buildToolContext();

        processWadl();
    }

    protected void processWadl() {
        File outDir = new File((String)context.get(WadlToolConstants.CFG_OUTPUTDIR));
        String wadlURL = getAbsoluteWadlURL();

        String wadl = readWadl(wadlURL);

        SourceGeneratorStream sg = getSourceGenerator();
        sg.setBus(getBus());

        boolean generateImpl = context.optionSet(WadlToolConstants.CFG_IMPL);
        sg.setGenerateImplementation(generateImpl);
        if (generateImpl) {
            sg.setGenerateInterfaces(context.optionSet(WadlToolConstants.CFG_INTERFACE));
        }
        sg.setPackageName((String)context.get(WadlToolConstants.CFG_PACKAGENAME));
        sg.setResourceName((String)context.get(WadlToolConstants.CFG_RESOURCENAME));
        sg.setEncoding((String)context.get(WadlToolConstants.CFG_ENCODING));

        String wadlNs = (String)context.get(WadlToolConstants.CFG_WADL_NAMESPACE);
        if (wadlNs != null) {
            sg.setWadlNamespace(wadlNs);
        }

        sg.setSupportMultipleXmlReps(context.optionSet(WadlToolConstants.CFG_MULTIPLE_XML_REPS));
        // set the base path
        sg.setWadlPath(wadlURL);

        CustomizationParser parser = new CustomizationParser(context);
        parser.parse(context);

        List<InputSource> bindingFiles = parser.getJaxbBindings();
        sg.setBindingFiles(bindingFiles);

        sg.setCompilerArgs(parser.getCompilerArgs());

        List<InputSource> schemaPackageFiles = parser.getSchemaPackageFiles();
        sg.setSchemaPackageFiles(schemaPackageFiles);
        sg.setSchemaPackageMap(context.getNamespacePackageMap());

        sg.setJavaTypeMap(getInternalDefaultJavaTypeMap());
        sg.setSchemaTypeMap(getInternalSchemaTypeMap());
        sg.setMediaTypeMap(getInternalMediaTypeMap());

        sg.setSuspendedAsyncMethods(getSuspendedAsyncMethods());
        sg.setResponseMethods(getResponseMethods());

        sg.setGenerateEnums(context.optionSet(WadlToolConstants.CFG_GENERATE_ENUMS));
        sg.setValidateWadl(context.optionSet(WadlToolConstants.CFG_VALIDATE_WADL));
        boolean inheritResourceParams = context.optionSet(WadlToolConstants.CFG_INHERIT_PARAMS);
        sg.setInheritResourceParams(inheritResourceParams);
        if (inheritResourceParams) {
            sg.setInheritResourceParamsFirst(isInternalInheritResourceParamsFirst());
        }
        sg.setSkipSchemaGeneration(context.optionSet(WadlToolConstants.CFG_NO_TYPES));

        boolean noVoidForEmptyResponses = context.optionSet(WadlToolConstants.CFG_NO_VOID_FOR_EMPTY_RESPONSES);
        if (noVoidForEmptyResponses) {
            sg.setUseVoidForEmptyResponses(false);
        }

        sg.setGenerateResponseIfHeadersSet(context.optionSet(WadlToolConstants.CFG_GENERATE_RESPONSE_IF_HEADERS_SET));

        // generate
        String codeType = context.optionSet(WadlToolConstants.CFG_TYPES)
                ? SourceGenerator.CODE_TYPE_GRAMMAR : SourceGenerator.CODE_TYPE_PROXY;
        sg.generateSource(wadl, outDir, codeType);

        // compile
        if (context.optionSet(WadlToolConstants.CFG_COMPILE)) {
            ClassCollector collector = createClassCollector();
            List<String> generatedServiceClasses = sg.getGeneratedServiceClasses();
            for (String className : generatedServiceClasses) {
                int index = className.lastIndexOf(".");
                collector.addServiceClassName(className.substring(0, index),
                        className.substring(index + 1),
                        className);
            }

            List<String> generatedTypeClasses = sg.getGeneratedTypeClasses();
            for (String className : generatedTypeClasses) {
                int index = className.lastIndexOf(".");
                collector.addTypesClassName(className.substring(0, index),
                        className.substring(index + 1),
                        className);
            }

            context.put(ClassCollector.class, collector);
            new ClassUtils().compile(context);
        }
    }

    @Override
    protected String readWadl(String wadlURI) {
        try {
            return IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected SourceGeneratorStream getSourceGenerator() {
        return new SourceGeneratorStream(results);
    }

    private Map<String, String> getInternalDefaultJavaTypeMap() {
        return getFieldValue("DEFAULT_JAVA_TYPE_MAP");
    }

    private <T> T getFieldValue(String fieldName) {
        Field field = ReflectionUtils.findField(JAXRSContainerStream.class, fieldName);
        ReflectionUtils.makeAccessible(field);
        try {
            return (T) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getInternalSchemaTypeMap() {
        return getMethodValue("getSchemaTypeMap");
    }

    private <T> T getMethodValue(String methodName) {
        Method method = ReflectionUtils.findMethod(JAXRSContainerStream.class, methodName);
        ReflectionUtils.makeAccessible(method);
        try {
            return (T) method.invoke(this, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getInternalMediaTypeMap() {
        return getMethodValue("getMediaTypeMap");
    }

    private boolean isInternalInheritResourceParamsFirst() {
        return getMethodValue("isInheritResourceParamsFirst");
    }
}
