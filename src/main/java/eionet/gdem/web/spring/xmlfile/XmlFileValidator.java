package eionet.gdem.web.spring.xmlfile;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 *
 */
public class XmlFileValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return XmlFileForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        XmlFileForm xmlFileForm = (XmlFileForm) o;
        MultipartFile xmlfile = xmlFileForm.getXmlFile();
        if (xmlfile == null || xmlfile.getSize() == 0) {
//            messageService.getMessage
            errors.rejectValue("xmlFile","label.uplXmlFile.validation");
        }
    }
}
