package eionet.gdem.services.fme.request;

import eionet.gdem.services.fme.exceptions.HttpRequestHeaderInitializationException;
import org.apache.http.Header;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHeader<T> {

    private Header[] headers;
    private Class<T> headerType;


  private HttpRequestHeader(){

  }

    public static class Builder<T1 extends Header> {
        private List<Header> headers;
        private Class<T1> headerType;


        public Builder(Class<T1> headerType) {

            this.headers = new ArrayList<>();
            this.headerType = headerType;
        }


        public HttpRequestHeader<T1 > build(){
            HttpRequestHeader<T1> httpRequestHeader = new HttpRequestHeader<>();
            httpRequestHeader.headers = (Header[]) this.headers.toArray();
            return httpRequestHeader;
        }

        public Builder createHeader(String key,String value) throws HttpRequestHeaderInitializationException {
            try {
                Header header = headerType.getDeclaredConstructor(headerType.getDeclaringClass()).newInstance(key,value);
                headers.add(header);
                return this;
            } catch (InstantiationException e) {
                throw new HttpRequestHeaderInitializationException("Instantiation exception for class "+headerType.getName()+": "+e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new HttpRequestHeaderInitializationException("Illegal access creating class "+headerType.getName()+": "+e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                throw new HttpRequestHeaderInitializationException("No such method exception for class "+headerType.getName()+": "+e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new HttpRequestHeaderInitializationException("Exception instantiating class "+headerType.getName()+": "+e.getMessage(), e);
            }
        }
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }
}
