package eionet.gdem.notifications;

import java.util.Vector;

public class NotificationTriple {

    /** */
    private String subject = null;
    private String property = null;
    private String value = null;

    /*
     *
     */
    public NotificationTriple() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /*
     *
     */
    public Vector toVector() {

        Vector v = new Vector();
        v.add(subject);
        v.add(property);
        v.add(value);

        return v;
    }
}
