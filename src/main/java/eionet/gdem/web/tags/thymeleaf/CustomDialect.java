package eionet.gdem.web.tags.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 */
public class CustomDialect extends AbstractProcessorDialect {

    protected CustomDialect() {
        super(
            "Custom dialect",
                "perm",
                1000
        );
    }

    @Override
    public Set<IProcessor> getProcessors(String s) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new HasPermission(s));
        return processors;
    }
}
