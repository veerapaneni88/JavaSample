package us.tx.state.dfps.service.common.utils;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.person.daoimpl.Base64;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.common.ServiceConstants;

public class FacilityAbuseInvUtil {
		
	private static final Logger logger = Logger.getLogger(FacilityAbuseInvUtil.class);

	//Start Added the code for  SD 56377: R2 Sev 5 Defect 10107

	public static String getFieldValue(byte[] xmlData, String tagName) {
		
		logger.info("Start getFieldValue method");

		String fieldEncodeValue = null; 
        try {
			org.jsoup.nodes.Document blobDocument = Jsoup.parse(new String(xmlData), "", Parser.xmlParser());
			 Elements blobElements = blobDocument.select(ServiceConstants.USER_EDIT_TAG);
            if (!ObjectUtils.isEmpty(blobElements)) {
            	
            	for (Element blobelement : blobElements) {
            		String fieldName = blobelement.getElementsByTag(ServiceConstants.FIELD_NAME_TAG).text(); 
            		if (!tagName.equals(fieldName)) {
            			continue;
            		}

            		Elements fieldValueElement = blobelement.getElementsByTag(ServiceConstants.FIELD_VALUE_TAG);
            		if(!ObjectUtils.isEmpty(fieldValueElement) && !ObjectUtils.isEmpty(fieldValueElement.text())) {
                    	fieldEncodeValue = fieldValueElement.text();
                    }
            		break;
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

        logger.info("End getFieldValue method");
        return fieldEncodeValue;
	}
	
	
	public static Document updateFieldValue(byte[] xmlData, String searchfieldName, String updatefieldValue ) {
	
		logger.info("Start updateFieldValue");
		org.jsoup.nodes.Document blobDocument =  null;
		
        try {
			blobDocument = Jsoup.parse(new String(xmlData), "", Parser.xmlParser());
            Elements blobElements = blobDocument.select(ServiceConstants.USER_EDIT_TAG);
            
            if (!ObjectUtils.isEmpty(blobElements)) {
            	int index = 0;
            	for (org.jsoup.nodes.Element blobelement : blobElements) {
                      String fieldName = "";
                      String fieldValue = "";
                    fieldName = blobelement.getElementsByTag(ServiceConstants.FIELD_NAME_TAG).text();   
                    fieldValue = blobelement.getElementsByTag(ServiceConstants.FIELD_VALUE_TAG).text();
                    if(searchfieldName.equals(fieldName) && (!ObjectUtils.isEmpty(fieldValue))) {
                    	Elements blobElement = blobDocument.select(ServiceConstants.FIELD_VALUE_TAG);
                    	blobElement.get(index).text(updatefieldValue);
                    	break;
                    }
                    index++;
            	}
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
        logger.info("End updateFieldValue");
        return blobDocument;
	}
	
	public static byte[] getByteArray(String xmlData, int count) {
		logger.info("Start getByteArray");
		byte[] byteArray = null;
		try {

			byteArray = xmlData.getBytes(ServiceConstants.CHARACTER_ENCODING);
			String checkString = new String(byteArray);
			if (checkString.indexOf('\0') != -1) {
				if (count < 5) {
					byteArray = getByteArray(xmlData, ++count);
				} else {
				}
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
		logger.info("End getByteArray");
		return byteArray;
	}

//	End	 Added the code for  SD 56377: R2 Sev 5 Defect 10107

}
