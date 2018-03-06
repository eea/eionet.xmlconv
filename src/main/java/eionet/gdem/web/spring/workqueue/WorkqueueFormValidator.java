package eionet.gdem.web.spring.workqueue;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 *
 *
 */
public class WorkqueueFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return WorkqueueForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        WorkqueueForm form = (WorkqueueForm) o;
        List jobs = form.getJobs();
        if (CollectionUtils.isEmpty(jobs)) {
            errors.rejectValue("jobs", "label.workqueue.error.nojobsselected");
        }
    }

}
