/*
 * Created on 25.02.2008
 */
package eionet.gdem.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Vector;

import eionet.gdem.GDEMException;
import eionet.gdem.dcm.remote.HttpMethodResponseWrapper;
import eionet.gdem.dcm.remote.RemoteServiceMethod;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.InputFile;
import eionet.gdem.utils.Streams;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS
 * ConvertDDXMLMethod
 */

public class ConvertDDXMLMethod extends RemoteServiceMethod {

	private static LoggerIF _logger = GDEMServices.getLogger();

	/**
	 * Converts DataDictionary MS Excel file to XML
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML(String sourceURL)
			throws GDEMException {
		OutputStream result = null;
		String cnvFileName = null;
		ByteArrayInputStream in_stream_tmp = null;
		InputFile src = null;
		String str_result = null;
		ByteArrayOutputStream out_stream_tmp = new ByteArrayOutputStream();
		Vector v_result = new Vector();

		String outFileName = getTmpFolder() + "gdem_" + System.currentTimeMillis()
				+ ".xml";
		String error_mess = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(getTicket());
			src.setTrustedMode(isTrustedMode());
			cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ?
					DEFAULT_FILE_NAME:src.getFileNameNoExtension();

			if (isHttpRequest()) {
				try {
					HttpMethodResponseWrapper httpResponse = getHttpResponse();
					httpResponse.setContentType("text/xml");
					httpResponse.setContentDisposition(cnvFileName + ".xml");
					result = httpResponse.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			if (result == null)
				result = new FileOutputStream(outFileName);

			// Read inputstream into Bytearrayoutputstream
			Streams.drain(src.getSrcInputStream(), out_stream_tmp);
			// Detect the file format
			DDXMLConverter converter = DDXMLConverter
					.getConverter(out_stream_tmp);

			if (converter == null) {
				_logger
						.error(
								"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.",
								null);
				throw new GDEMException(
						"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.");
			}
			// create new inputstrema from tmp Bytearrayoutputstream
			in_stream_tmp = new ByteArrayInputStream(out_stream_tmp
					.toByteArray());

			str_result = converter.convertDD_XML(in_stream_tmp, result);

		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL ", mfe);
			if (isHttpRequest()) {
				throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
			} else {
				error_mess = "Bad URL : " + mfe.toString();
			}
		} catch (IOException ioe) {
			_logger.error("Error opening URL ", ioe);
			if (isHttpRequest()) {
				throw new GDEMException("Error opening URL " + ioe.toString(),
						ioe);
			} else {
				error_mess = "Error opening URL " + ioe.toString();
			}
		} catch (Exception e) {
			_logger.error("", e);
			if (isHttpRequest()) {
				throw new GDEMException(e.toString(), e);
			} else {
				error_mess = e.toString();
			}
		} finally {
			try {
				if (src != null)
					src.close();
				// if (result!=null) result.close();
			} catch (Exception e) {
				_logger.error("", e);
			}
			try {
				if (in_stream_tmp != null)
					in_stream_tmp.close();
			} catch (Exception e) {
			}
			try {
				if (out_stream_tmp != null)
					out_stream_tmp.close();
			} catch (Exception e) {
			}

		}

		if (isHttpRequest()) {
			return v_result;
		}
		// Creates response Vector
		int result_code = 1;
		if (!Utils.isNullStr(str_result)) {
			if (str_result.equals("OK"))
				result_code = 0;
		}
		byte[] file = Utils.fileToBytes(outFileName);

		v_result.add(String.valueOf(result_code));
		if (result_code == 0){
			v_result.add(file);
			v_result.add(cnvFileName + ".xml");
		}
		else
			v_result.add(error_mess);

		try {
			Utils.deleteFile(outFileName);
		} catch (Exception e) {
			_logger.error("Couldn't delete the result file", e);
		}

		return v_result;
	}
	/**
	 * Converts DataDictionary MS Excel sheets to
	 * different XML files, where one xml file is dataset table.
	 *
	 * @param String
	 *            url: URL of the srouce Excel file
	 * @return Vector result: error_code, xml_url, error_message
	 */
	public Vector convertDD_XML_split(String sourceURL, String sheet_param) throws GDEMException {
		OutputStream result = null;
		ByteArrayOutputStream out_stream_tmp = new ByteArrayOutputStream();
		ByteArrayInputStream in_stream_tmp = null;

		InputFile src = null;
		String error_mess = null;
		String cnvFileName = null;
		Vector v_result = null;

		try {

			src = new InputFile(sourceURL);
			src.setAuthentication(getTicket());
			src.setTrustedMode(isTrustedMode());

			cnvFileName = Utils.isNullStr(src.getFileNameNoExtension()) ?
					DEFAULT_FILE_NAME:src.getFileNameNoExtension();
			if (isHttpRequest()) {
				try {
					HttpMethodResponseWrapper httpResponse = getHttpResponse();
					httpResponse.setContentType("text/xml");
					httpResponse.setContentDisposition(cnvFileName + ".xml");
					result = httpResponse.getOutputStream();
				} catch (IOException e) {
					_logger.error("Error getting response outputstream ", e);
					throw new GDEMException(
							"Error getting response outputstream "
									+ e.toString(), e);
				}
			}
			// Read inputstream into Bytearrayoutputstream
			Streams.drain(src.getSrcInputStream(), out_stream_tmp);
			// Detect the file format
			DDXMLConverter converter = DDXMLConverter
					.getConverter(out_stream_tmp);

			if (converter == null) {
				_logger
						.error(
								"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.",
								null);
				throw new GDEMException(
						"Could not detect the format of source file. Converter waits MS Excel or OpenDocument Spreadsheet file.");
			}
			// create new inputstrema from tmp Bytearrayoutputstream
			in_stream_tmp = new ByteArrayInputStream(out_stream_tmp
					.toByteArray());

			v_result = converter.convertDD_XML_split(in_stream_tmp, result,
					sheet_param);

		} catch (MalformedURLException mfe) {
			_logger.error("Bad URL ", mfe);
			if (isHttpRequest()) {
				throw new GDEMException("Bad URL : " + mfe.toString(), mfe);
			} else {
				error_mess = "Bad URL : " + mfe.toString();
			}
		} catch (IOException ioe) {
			_logger.error("Error opening URL ", ioe);
			if (isHttpRequest()) {
				throw new GDEMException("Error opening URL " + ioe.toString(),
						ioe);
			} else {
				error_mess = "Error opening URL " + ioe.toString();
			}
		} catch (Exception e) {
			_logger.error("", e);
			if (isHttpRequest()) {
				throw new GDEMException(e.toString(), e);
			} else {
				error_mess = e.toString();
			}
		} finally {
			try {
				if (src != null)
					src.close();
			} catch (Exception e) {
			}
			try {
				if (in_stream_tmp != null)
					in_stream_tmp.close();
			} catch (Exception e) {
			}
			try {
				if (out_stream_tmp != null)
					out_stream_tmp.close();
			} catch (Exception e) {
			}
		}
		if (isHttpRequest()) {
			return v_result;
		}
		// Creates response Vector

		if (Utils.isNullVector(v_result) && !Utils.isNullStr(error_mess)) {
			v_result.add("1");
			v_result.add(error_mess);
		}

		return v_result;
	}
}
