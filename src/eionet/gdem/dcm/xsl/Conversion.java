package eionet.gdem.dcm.xsl;

import org.w3c.dom.*;

import java.io.File;
import javax.xml.parsers.*;

import java.util.*;
import eionet.gdem.Properties;
import eionet.gdem.dto.ConversionDto;

public class Conversion {

	private static List conversions = null;
	static {
		try {
			File file = new File(Properties.convFile);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);

			// Find the tags of interest

			NodeList nodes = doc.getElementsByTagName("conversion");
			conversions = new ArrayList();
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				ConversionDto resObject = new ConversionDto();

				// Process the conversion tag
				NodeList id = element.getElementsByTagName("id");
				Element eId = (Element) id.item(0);
				resObject.setConvId(eId.getFirstChild().getNodeValue());

				NodeList desc = element.getElementsByTagName("description");
				Element eDesc = (Element) desc.item(0);
				resObject.setDescription(eDesc.getFirstChild().getNodeValue());

				NodeList type = element.getElementsByTagName("result_type");
				Element eType = (Element) type.item(0);
				resObject.setResultType(eType.getFirstChild().getNodeValue());

				NodeList ss = element.getElementsByTagName("stylesheet");
				Element eSs = (Element) ss.item(0);
				resObject.setStylesheet(eSs.getFirstChild().getNodeValue());

				conversions.add(resObject);

			}

		} catch (Exception ex) {
			//System.out.println(ex);
			ex.printStackTrace();
		}

	}


	public static void main(String[] args) {

		System.out.print(getConversionById("5"));
	}


	public static List getConversions() {
		return conversions;
	}


	public static ConversionDto getConversionById(String convId) {
		for (int i = 0; i < conversions.size(); i++) {
			if (((ConversionDto) conversions.get(i)).getConvId().compareTo(convId) == 0) {
				return (ConversionDto) conversions.get(i);
			}
		}
		return null;
	}

}
