package org.apache.cxf.generator.generation.wsdl;

import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.cxf.BusException;
import org.apache.cxf.wsdl11.WSDLManagerImpl;
import org.xml.sax.InputSource;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import java.io.InputStream;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class WSDLManagerStream extends WSDLManagerImpl {
// ------------------------------ FIELDS ------------------------------

    private final InputStream stream;

// --------------------------- CONSTRUCTORS ---------------------------

    public WSDLManagerStream(InputStream stream) throws BusException {
        super();
        this.stream = stream;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected Definition loadDefinition(String url) throws WSDLException {
        WSDLReaderImpl wsdlReader = new WSDLReaderImpl();
        return wsdlReader.readWSDL(null, new InputSource(stream));
    }
}
