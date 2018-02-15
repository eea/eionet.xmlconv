package eionet.gdem.web.spring.converter;

import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 *
 */
public class Json2xmlForm {

    @NotEmpty(message = "label.conversion.json2xml.empty")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
