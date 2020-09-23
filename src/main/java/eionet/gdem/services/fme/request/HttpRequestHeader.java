package eionet.gdem.services.fme.request;

import eionet.gdem.services.fme.exceptions.HttpRequestHeaderInitializationException;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHeader {

    private Header[] headers;


  private HttpRequestHeader(){

  }

    public static class Builder {
        private List<Header> headers;


        public Builder() {
            this.headers = new ArrayList<>();
        }

        public HttpRequestHeader build(){
            HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
            httpRequestHeader.headers =  this.headers.toArray(new Header[0]);
            return httpRequestHeader;
        }

        public Builder createHeader(String key,String value) throws HttpRequestHeaderInitializationException {
                Header header = new BasicHeader(key,value);
                headers.add(header);
                return this;
        }
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }
}
