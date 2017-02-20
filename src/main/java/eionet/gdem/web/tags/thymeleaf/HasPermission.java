package eionet.gdem.web.tags.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 *
 */
public class HasPermission extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "hasPermission";
    private static final int PRECEDENCE = 10000;

    protected HasPermission(final String dialectPrefix) {
        super(
            TemplateMode.HTML,
            dialectPrefix,
            null,
            false,
            ATTR_NAME,
            true,
            PRECEDENCE,
            true);
    }

    @Override
    protected void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String s, IElementTagStructureHandler iElementTagStructureHandler) {
        iElementTagStructureHandler.setBody("", false);
    }
}
