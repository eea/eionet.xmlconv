package eionet.gdem.web.servlets;

import eionet.acl.AppUser;
import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.security.errors.JWTException;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 *
 */
public abstract class FileServlet extends HttpServlet {

    // Constants ------------------------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServlet.class.getName());

    private static final Long DEFAULT_EXPIRE_TIME_IN_SECONDS = TimeUnit.DAYS.toSeconds(30);
    private static final long ONE_SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private static final String ETAG = "W/\"%s-%s\"";
    private static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=[0-9]*-[0-9]*(,[0-9]*-[0-9]*)*$");
    private static final String MULTIPART_BOUNDARY = UUID.randomUUID().toString();
    private static final String CONTENT_DISPOSITION_HEADER = "%s;filename=\"%2$s\"; filename*=UTF-8''%2$s";
    private static final int DEFAULT_STREAM_BUFFER_SIZE = 10240;
    private static final String ERROR_UNSUPPORTED_ENCODING = "UTF-8 is apparently not supported on this platform.";

    // Actions --------------------------------------------------------------------------------------------------------

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response, true);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response, false);
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response, boolean head) throws IOException {
        response.reset();
        Resource resource = null;

        try {
            resource = new Resource(getFile(request));
        }
        catch (IllegalArgumentException e) {
            LOGGER.info("Got an IllegalArgumentException from user code; interpreting it as 400 Bad Request.", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (SignOnException e) {
            LOGGER.info(e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
            return;
        }

        if (resource.file == null) {
            handleFileNotFound(request, response);
            return;
        }

        if (preconditionFailed(request, resource)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        setCacheHeaders(response, resource, getExpireTime(request, resource.file));

        if (notModified(request, resource)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        List<Range> ranges = getRanges(request, resource);

        if (ranges == null) {
            response.setHeader("Content-Range", "bytes */" + resource.length);
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        if (!ranges.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        else {
            ranges.add(new Range(0, resource.length - 1)); // Full content.
        }

        String contentType = setContentHeaders(request, response, resource, ranges);

        if (head) {
            return;
        }

        writeContent(response, resource, ranges, contentType);
    }

    /**
     * Returns the file associated with the given HTTP servlet request.
     * If this method throws {@link IllegalArgumentException}, then the servlet will return a HTTP 400 error.
     * If this method returns <code>null</code>, or if {@link File#isFile()} returns <code>false</code>, then the
     * servlet will invoke {@link #handleFileNotFound(HttpServletRequest, HttpServletResponse)}.
     * @param request The involved HTTP servlet request.
     * @return The file associated with the given HTTP servlet request.
     * @throws IllegalArgumentException When the request is mangled in such way that it's not recognizable as a valid
     * file request. The servlet will then return a HTTP 400 error.
     */
    protected abstract File getFile(HttpServletRequest request) throws IllegalArgumentException, SignOnException;

    /**
     * Handles the case when the file is not found.
     * <p>
     * The default implementation sends a HTTP 404 error.
     * @param request The involved HTTP servlet request.
     * @param response The involved HTTP servlet response.
     * @throws IOException When something fails at I/O level.
     * @since 2.3
     */
    protected void handleFileNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Returns how long the resource may be cached by the client before it expires, in seconds.
     * <p>
     * The default implementation returns 30 days in seconds.
     * @param request The involved HTTP servlet request.
     * @param file The involved file.
     * @return The client cache expire time in seconds (not milliseconds!).
     */
    protected long getExpireTime(HttpServletRequest request, File file) {
        return DEFAULT_EXPIRE_TIME_IN_SECONDS;
    }

    /**
     * Returns the content type associated with the given HTTP servlet request and file.
     * <p>
     * The default implementation delegates {@link File#getName()} to {@link ServletContext#getMimeType(String)} with a
     * fallback default value of <code>application/octet-stream</code>.
     * @param request The involved HTTP servlet request.
     * @param file The involved file.
     * @return The content type associated with the given HTTP servlet request and file.
     */
    protected String getContentType(HttpServletRequest request, File file) {
        return coalesce(request.getServletContext().getMimeType(file.getName()), "application/octet-stream");
    }

    /**
     * Returns the first non-<code>null</code> object of the argument list, or <code>null</code> if there is no such
     * element.
     * @param <T> The generic object type.
     * @param objects The argument list of objects to be tested for non-<code>null</code>.
     * @return The first non-<code>null</code> object of the argument list, or <code>null</code> if there is no such
     * element.
     */
    @SafeVarargs
    public static <T> T coalesce(T... objects) {
        for (T object : objects) {
            if (object != null) {
                return object;
            }
        }

        return null;
    }

    /**
     * Returns <code>true</code> if we must force a "Save As" dialog based on the given HTTP servlet request and content
     * type as obtained from {@link #getContentType(HttpServletRequest, File)}.
     * <p>
     * The default implementation will return <code>true</code> if the content type does <strong>not</strong> start with
     * <code>text</code> or <code>image</code>, and the <code>Accept</code> request header is either <code>null</code>
     * or does not match the given content type.
     * @param request The involved HTTP servlet request.
     * @param contentType The content type of the involved file.
     * @return <code>true</code> if we must force a "Save As" dialog based on the given HTTP servlet request and content
     * type.
     */
    protected boolean isAttachment(HttpServletRequest request, String contentType) {
        String accept = request.getHeader("Accept");
        return !startsWithOneOf(contentType, "text", "image") && (accept == null || !accepts(accept, contentType));
    }

    /**
     * Returns <code>true</code> if the given string starts with one of the given prefixes.
     * @param string The object to be checked if it starts with one of the given prefixes.
     * @param prefixes The argument list of prefixes to be checked
     * @return <code>true</code> if the given string starts with one of the given prefixes.
     * @since 1.4
     */
    public static boolean startsWithOneOf(String string, String... prefixes) {
        for (String prefix : prefixes) {
            if (string.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the file name to be used in <code>Content-Disposition</code> header.
     * This does not need to be URL-encoded as this will be taken care of.
     * <p>
     * The default implementation returns {@link File#getName()}.
     * @param request The involved HTTP servlet request.
     * @param file The involved file.
     * @return The file name to be used in <code>Content-Disposition</code> header.
     * @since 2.3
     */
    protected String getAttachmentName(HttpServletRequest request, File file) {
        return file.getName();
    }

    // Sub-actions ----------------------------------------------------------------------------------------------------

    /**
     * Returns true if it's a conditional request which must return 412.
     */
    private boolean preconditionFailed(HttpServletRequest request, Resource resource) {
        String match = request.getHeader("If-Match");
        long unmodified = request.getDateHeader("If-Unmodified-Since");
        return (match != null) ? !matches(match, resource.eTag) : (unmodified != -1 && modified(unmodified, resource.lastModified));
    }

    /**
     * Set cache headers.
     */
    private void setCacheHeaders(HttpServletResponse response, Resource resource, long expires) {
        setCacheHeaders(response, expires);
        response.setHeader("ETag", resource.eTag);
        response.setDateHeader("Last-Modified", resource.lastModified);
    }

    /**
     * <p>Set the cache headers. If the <code>expires</code> argument is larger than 0 seconds, then the following headers
     * will be set:
     * <ul>
     * <li><code>Cache-Control: public,no-cache,max-age=[expiration time in seconds],must-revalidate</code></li>
     * <li><code>Expires: [expiration date of now plus expiration time in seconds]</code></li>
     * </ul>
     * <p>Else the method will delegate to {@link #setNoCacheHeaders(HttpServletResponse)}.
     * @param response The HTTP servlet response to set the headers on.
     * @param expires The expire time in seconds (not milliseconds!).
     * @since 2.2
     */
    public static void setCacheHeaders(HttpServletResponse response, long expires) {
        if (expires > 0) {
            response.setHeader("Cache-Control", "public,no-cache,max-age=" + expires + ",must-revalidate");
            response.setDateHeader("Expires", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expires));
            response.setHeader("Pragma", ""); // Explicitly set pragma to prevent container from overriding it.
        }
        else {
            setNoCacheHeaders(response);
        }
    }

    /**
     * <p>Set the no-cache headers. The following headers will be set:
     * <ul>
     * <li><code>Cache-Control: no-cache,no-store,must-revalidate</code></li>
     * <li><code>Expires: [expiration date of 0]</code></li>
     * <li><code>Pragma: no-cache</code></li>
     * </ul>
     * Set the no-cache headers.
     * @param response The HTTP servlet response to set the headers on.
     * @since 2.2
     */
    public static void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache"); // Backwards compatibility for HTTP 1.0.
    }

    /**
     * Returns true if it's a conditional request which must return 304.
     */
    private boolean notModified(HttpServletRequest request, Resource resource) {
        String noMatch = request.getHeader("If-None-Match");
        long modified = request.getDateHeader("If-Modified-Since");
        return (noMatch != null) ? matches(noMatch, resource.eTag) : (modified != -1 && !modified(modified, resource.lastModified));
    }

    /**
     * Get requested ranges. If this is null, then we must return 416. If this is empty, then we must return full file.
     */
    private List<Range> getRanges(HttpServletRequest request, Resource resource) {
        List<Range> ranges = new ArrayList<>(1);
        String rangeHeader = request.getHeader("Range");

        if (rangeHeader == null) {
            return ranges;
        }
        else if (!RANGE_PATTERN.matcher(rangeHeader).matches()) {
            return null; // Syntax error.
        }

        String ifRange = request.getHeader("If-Range");

        if (ifRange != null && !ifRange.equals(resource.eTag)) {
            try {
                long ifRangeTime = request.getDateHeader("If-Range");

                if (ifRangeTime != -1 && modified(ifRangeTime, resource.lastModified)) {
                    return ranges;
                }
            }
            catch (IllegalArgumentException ifRangeHeaderIsInvalid) {
                LOGGER.info("If-Range header is invalid. Let's just return full file then.", ifRangeHeaderIsInvalid);
                return ranges;
            }
        }

        for (String rangeHeaderPart : rangeHeader.split("=")[1].split(",")) {
            Range range = parseRange(rangeHeaderPart, resource.length);

            if (range == null) {
                return null; // Logic error.
            }

            ranges.add(range);
        }

        return ranges;
    }

    /**
     * Parse range header part. Returns null if there's a logic error (i.e. start after end).
     */
    private Range parseRange(String range, long length) {
        long start = sublong(range, 0, range.indexOf('-'));
        long end = sublong(range, range.indexOf('-') + 1, range.length());

        if (start == -1) {
            start = length - end;
            end = length - 1;
        }
        else if (end == -1 || end > length - 1) {
            end = length - 1;
        }

        if (start > end) {
            return null; // Logic error.
        }

        return new Range(start, end);
    }

    /**
     * Set content headers.
     */
    private String setContentHeaders(HttpServletRequest request, HttpServletResponse response, Resource resource, List<Range> ranges) {
        String contentType = getContentType(request, resource.file);
        if (contentType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }
        String filename = getAttachmentName(request, resource.file);
        boolean attachment = isAttachment(request, contentType);
        response.setHeader("Content-Disposition", formatContentDispositionHeader(filename, attachment));
        response.setHeader("Accept-Ranges", "bytes");

        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            response.setContentType(contentType);
            response.setHeader("Content-Length", String.valueOf(range.length));

            if (response.getStatus() == HttpServletResponse.SC_PARTIAL_CONTENT) {
                response.setHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + resource.length);
            }
        }
        else {
            response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
        }

        return contentType;
    }

    /**
     * <p>Format an UTF-8 compatible content disposition header for the given filename and whether it's an attachment.
     * @param filename The filename to appear in "Save As" dialogue.
     * @param attachment Whether the content should be provided as an attachment or inline.
     * @return An UTF-8 compatible content disposition header.
     * @since 2.6
     */
    public static String formatContentDispositionHeader(String filename, boolean attachment) {
        return format(CONTENT_DISPOSITION_HEADER, (attachment ? "attachment" : "inline"), encodeURI(filename));
    }

    /**
     * URI-encode the given string using UTF-8. URIs (paths and filenames) have different encoding rules as compared to
     * URL query string parameters. {@link URLEncoder} is actually only for www (HTML) form based query string parameter
     * values (as used when a webbrowser submits a HTML form). URI encoding has a lot in common with URL encoding, but
     * the space has to be %20 and some chars doesn't necessarily need to be encoded.
     * @param string The string to be URI-encoded using UTF-8.
     * @return The given string, URI-encoded using UTF-8, or <code>null</code> if <code>null</code> was given.
     * @throws UnsupportedOperationException When this platform does not support UTF-8.
     * @since 2.4
     */
    public static String encodeURI(String string) {
        if (string == null) {
            return null;
        }

        return encodeURL(string)
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%7E", "~");
    }

    /**
     * URL-encode the given string using UTF-8.
     * @param string The string to be URL-encoded using UTF-8.
     * @return The given string, URL-encoded using UTF-8, or <code>null</code> if <code>null</code> was given.
     * @throws UnsupportedOperationException When this platform does not support UTF-8.
     * @since 1.4
     */
    public static String encodeURL(String string) {
        if (string == null) {
            return null;
        }

        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(ERROR_UNSUPPORTED_ENCODING, e);
        }
    }

    /**
     * Write given file to response with given content type and ranges.
     */
    private void writeContent(HttpServletResponse response, Resource resource, List<Range> ranges, String contentType) throws IOException {
        ServletOutputStream output = response.getOutputStream();

        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            stream(resource.file, output, range.start, range.length);
        }
        else {
            for (Range range : ranges) {
                output.println();
                output.println("--" + MULTIPART_BOUNDARY);
                output.println("Content-Type: " + contentType);
                output.println("Content-Range: bytes " + range.start + "-" + range.end + "/" + resource.length);
                stream(resource.file, output, range.start, range.length);
            }

            output.println();
            output.println("--" + MULTIPART_BOUNDARY + "--");
        }
    }

    /**
     * Stream a specified range of the given file to the given output via NIO {@link Channels} and a directly allocated
     * NIO {@link ByteBuffer}. The output stream will only implicitly be closed after streaming when the specified range
     * represents the whole file, regardless of whether an exception is been thrown or not.
     * @param file The file.
     * @param output The output stream.
     * @param start The start position (offset).
     * @param length The (intented) length of written bytes.
     * @return The (actual) length of the written bytes. This may be smaller when the given length is too large.
     * @throws IOException When an I/O error occurs.
     * @since 2.2
     */
    public static long stream(File file, OutputStream output, long start, long length) throws IOException {
        if (start == 0 && length >= file.length()) {
            return stream(new FileInputStream(file), output);
        }

        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(file.toPath(), StandardOpenOption.READ)) {
            WritableByteChannel outputChannel = Channels.newChannel(output);
            ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
            long size = 0;

            while (fileChannel.read(buffer, start + size) != -1) {
                buffer.flip();

                if (size + buffer.limit() > length) {
                    buffer.limit((int) (length - size));
                }

                size += outputChannel.write(buffer);

                if (size >= length) {
                    break;
                }

                buffer.clear();
            }

            return size;
        }
    }

    /**
     * Stream the given input to the given output via NIO {@link Channels} and a directly allocated NIO
     * {@link ByteBuffer}. Both the input and output streams will implicitly be closed after streaming,
     * regardless of whether an exception is been thrown or not.
     * @param input The input stream.
     * @param output The output stream.
     * @return The length of the written bytes.
     * @throws IOException When an I/O error occurs.
     */
    public static long stream(InputStream input, OutputStream output) throws IOException {
        try (ReadableByteChannel inputChannel = Channels.newChannel(input);
             WritableByteChannel outputChannel = Channels.newChannel(output))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
            long size = 0;

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                size += outputChannel.write(buffer);
                buffer.clear();
            }

            return size;
        }
    }

    // Helpers --------------------------------------------------------------------------------------------------------

    /**
     * Returns true if the given match header matches the given ETag value.
     */
    private static boolean matches(String matchHeader, String eTag) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, eTag) > -1
                || Arrays.binarySearch(matchValues, "*") > -1;
    }

    /**
     * Returns true if the given modified header is older than the given last modified value.
     */
    private static boolean modified(long modifiedHeader, long lastModified) {
        return (modifiedHeader + ONE_SECOND_IN_MILLIS <= lastModified); // That second is because the header is in seconds, not millis.
    }

    /**
     * Returns a substring of the given string value from the given begin index to the given end index as a long.
     * If the substring is empty, then -1 will be returned.
     */
    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return substring.isEmpty() ? -1 : Long.parseLong(substring);
    }

    /**
     * Returns true if the given accept header accepts the given value.
     */
    private static boolean accepts(String acceptHeader, String toAccept) {
        String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues, toAccept) > -1
                || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }

    // Nested classes -------------------------------------------------------------------------------------------------

    /**
     * Convenience class for a file resource.
     */
    private static class Resource {
        private final File file;
        private final long length;
        private final long lastModified;
        private final String eTag;

        public Resource(File file) {
            if (file != null && file.isFile()) {
                this.file = file;
                length = file.length();
                lastModified = file.lastModified();
                eTag = format(ETAG, encodeURL(file.getName()), lastModified);
            }
            else {
                this.file = null;
                length = 0;
                lastModified = 0;
                eTag = null;
            }
        }

    }

    /**
     * Convenience class for a byte range.
     */
    private static class Range {
        private final long start;
        private final long end;
        private final long length;

        public Range(long start, long end) {
            this.start = start;
            this.end = end;
            length = end - start + 1;
        }

    }

    // EEA Specific

    /**
     * Check if user has permissions to see log file.
     * @param request HTTP servlet request
     * @param urlPath request URL
     * @return null if user has permissions, otherwise error message
     */
    private String checkPermissions(HttpServletRequest request, String urlPath) {

        String securityMessage = null;
        try {
            if (urlPath.startsWith("/tmp/")) {
                AppUser aclUser = SecurityUtil.getUser(request, Constants.USER_ATT);
                if (aclUser == null || !SecurityUtil.hasPerm(aclUser.getUserName(), "/" + Constants.ACL_WQ_PATH, "v")) {
                    securityMessage = "You don't have permissions to view result file: " + urlPath;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to check permissions: " + urlPath, e);
            securityMessage = "Unable to check permissions: " + urlPath;
        }
        return securityMessage;
    }

    /**
     * Handles HTTP 401 error.
     * @param message error message to display for a user.
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @throws IOException If an error occurs.
     * @throws ServletException If an error occurs.
     */
    private void handleNotAuthorised(String message, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info(message);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

}
