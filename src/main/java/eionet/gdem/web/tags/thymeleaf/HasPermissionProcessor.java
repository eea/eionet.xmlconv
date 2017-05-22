package eionet.gdem.web.tags.thymeleaf;

import eionet.acl.SignOnException;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.*;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 *
 */
public class HasPermissionProcessor extends AbstractElementTagProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HasPermissionProcessor.class);
    private static final String ELEM_NAME = "hasperm";
    private static final int PRECEDENCE = 500;

    protected HasPermissionProcessor(TemplateMode templateMode, final String dialectPrefix) {
        super(
                templateMode,
            dialectPrefix,
            ELEM_NAME,
            true,
            null,
            false,
                PRECEDENCE);
    }
/*
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        IEngineConfiguration configuration = context.getConfiguration();
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        IAttribute aclPath = tag.getAttribute("acl");
        IAttribute permission = tag.getAttribute("permission");
        IStandardExpression userExp = parser.parseExpression(context, "user");
        String user = (String) userExp.execute(context);
        boolean hasPerm = false;
        try {
            hasPerm = SecurityUtil.hasPerm(user, "/" + aclPath, permission.getValue());
        } catch (SignOnException ex) {
            LOGGER.error("Can't validate permission for user: " + user, ex);
        }

        if (!hasPerm) { structureHandler.removeBody(); }
    }*/

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        IEngineConfiguration configuration = context.getConfiguration();
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        IAttribute aclPath = tag.getAttribute("acl");
        IAttribute permission = tag.getAttribute("permission");
        IStandardExpression userExp = parser.parseExpression(context, "${session.user}");
        String user = (String) userExp.execute(context);
        boolean hasPerm = false;
        try {
            hasPerm = SecurityUtil.hasPerm(user, "/" + aclPath.getValue(), permission.getValue());
        } catch (SignOnException ex) {
            LOGGER.error("Can't validate permission for user: " + user, ex);
        }
        if (!hasPerm) { structureHandler.removeElement(); }
    }

}
