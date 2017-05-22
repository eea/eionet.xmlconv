package eionet.gdem.data.obligations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 *
 *
 */
public class ObligationFormatter implements Formatter<Obligation> {

    @Autowired
    private ObligationService obligationService;

    @Override
    public Obligation parse(String s, Locale locale) throws ParseException {
        return obligationService.findById(Integer.parseInt(s));
    }

    @Override
    public String print(Obligation obligation, Locale locale) {
        return obligation.getId().toString();
    }
}
