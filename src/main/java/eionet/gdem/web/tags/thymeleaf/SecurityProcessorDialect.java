package eionet.gdem.web.tags.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 */
public class SecurityProcessorDialect extends AbstractProcessorDialect {

    protected SecurityProcessorDialect() {
        super(
            "Permission dialect",
                "perm",
                StandardDialect.PROCESSOR_PRECEDENCE
        );
    }

    @Override
    public Set<IProcessor> getProcessors(String s) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        final TemplateMode[] templateModes =
                new TemplateMode[] {
                        TemplateMode.HTML, TemplateMode.XML,
                        TemplateMode.TEXT, TemplateMode.JAVASCRIPT, TemplateMode.CSS };
        for (final TemplateMode templateMode : templateModes) {
            processors.add(new HasPermissionProcessor(templateMode, s));
            processors.add(new StandardXmlNsTagProcessor(templateMode, s));
        }
        return processors;
    }
}
