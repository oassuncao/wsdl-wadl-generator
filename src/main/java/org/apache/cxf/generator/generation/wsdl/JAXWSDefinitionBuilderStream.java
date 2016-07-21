package org.apache.cxf.generator.generation.wsdl;

import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.wsdl11.JAXWSDefinitionBuilder;

import javax.wsdl.Definition;

/**
 * @author Silvio Assunção
 * @since 1.0
 */
public class JAXWSDefinitionBuilderStream extends JAXWSDefinitionBuilder {
// -------------------------- OTHER METHODS --------------------------

    @Override
    public boolean validate(Definition def) throws ToolException {
        //TODO: Validate WSDL without file
        return true;
    }
}
