package eionet.gdem.web.spring.xmlfile;


import org.apache.commons.lang3.StringUtils;
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

    public void validateUpdate(Object o, Errors errors) {
        XmlFileForm xmlFileForm = (XmlFileForm) o;
        String xmlFileId = xmlFileForm.getXmlfileId();
        if (StringUtils.isEmpty(xmlFileId)) {
            errors.rejectValue("xmlfileId", "label.uplXmlFile.error.notSelected");
        }
    }

    public void validateDelete(Object o, Errors errors) {
        XmlFileForm xmlFileForm = (XmlFileForm) o;
        String xmlFileId = xmlFileForm.getXmlfileId();
        if (StringUtils.isEmpty(xmlFileId)) {
            errors.rejectValue("xmlfileId", "label.uplXmlFile.error.notSelected");
        }
    }
}
