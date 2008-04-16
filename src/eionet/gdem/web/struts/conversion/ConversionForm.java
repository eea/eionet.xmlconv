/*
 * Created on 09.04.2008
 */
package eionet.gdem.web.struts.conversion;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.dto.Schema;

/**
 * @author Enriko Käsper, TietoEnator Estonia AS ConversionForm
 */

public class ConversionForm extends ActionForm {

	private static final String DEFAULT_ERROR_FORWARD="error"; 
	
	private String schemaUrl;
	private String url;
	private String insertedUrl;
	private String conversionId;
	private Schema schema;
	private List<Schema> schemas;
	private boolean showSchemaSelection=false;
	private boolean converted=true;
	private String searchAction;
	private String convertAction;
	private String action;
	private String errorForward=DEFAULT_ERROR_FORWARD;

	public ActionErrors validate(ActionMapping actionMapping,
			HttpServletRequest httpServletRequest) {
		
		return null;
	}

	public void resetAll(ActionMapping actionMapping,
			HttpServletRequest httpServletRequest) {
		super.reset(actionMapping, httpServletRequest);
		schemaUrl = null;
		url = null;
		insertedUrl=null;
		conversionId = null;
		schema=null;
		showSchemaSelection=false;
		schemas=null;
		searchAction=null;
		convertAction=null;
		converted=false;
		action=null;
		errorForward=DEFAULT_ERROR_FORWARD;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}

	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getConversionId() {
		return conversionId;
	}

	public void setConversionId(String conversionId) {
		this.conversionId = conversionId;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public List<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	public boolean isShowSchemaSelection() {
		return showSchemaSelection;
	}

	public void setShowSchemaSelection(boolean showSchemaSelection) {
		this.showSchemaSelection = showSchemaSelection;
	}

	public String getSearchAction() {
		return searchAction;
	}

	public void setSearchAction(String searchAction) {
		this.searchAction = searchAction;
	}

	public String getConvertAction() {
		return convertAction;
	}

	public void setConvertAction(String convertAction) {
		this.convertAction = convertAction;
	}

	public boolean isConverted() {
		return converted;
	}

	public void setConverted(boolean converted) {
		this.converted = converted;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getInsertedUrl() {
		return insertedUrl;
	}

	public void setInsertedUrl(String insertedUrl) {
		this.insertedUrl = insertedUrl;
	}

	public String getErrorForward() {
		return errorForward;
	}

	public void setErrorForward(String errorForward) {
		this.errorForward = errorForward;
	}

}
