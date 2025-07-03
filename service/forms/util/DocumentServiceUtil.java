package us.tx.state.dfps.service.forms.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Abstract
 * Class to be extended for the prefill data for all the forms Feb 9, 2018-
 * 2:04:22 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
public abstract class DocumentServiceUtil {

	@Autowired
	LookupDao lookupDao;

	public abstract PreFillDataServiceDto returnPrefillData(Object parentDtoobj);

	/**
	 * Method Description: Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template.
	 * 
	 * @param name
	 * @param data
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmark(String name, String data) {
		BookmarkDto bookMarkDto = new BookmarkDto();
		bookMarkDto.setBookmarkName(name);
		bookMarkDto.setBookmarkData(data == null ? "" : data);
		return bookMarkDto;
	}

	/**
	 * Method Description: Generates bookmark xml for a string without
	 * StringEscapeUtils.escapeJava(data) to use processing required for text
	 * fields in repeating groups as it exists in forms code.
	 * 
	 * @param name
	 * @param data
	 * @param formDataGroupDto
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmark(String name, String data, FormDataGroupDto formDataGroupDto) {
		BookmarkDto bookMarkDto = new BookmarkDto();
		bookMarkDto.setBookmarkName(name);
		bookMarkDto.setBookmarkData(data == null ? "" : data);
		return bookMarkDto;
	}

	/**
	 * Method Description: Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template.
	 * 
	 * @param name
	 * @param data
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmark(String name, Number data) {
		BookmarkDto bookMarkDto = new BookmarkDto();
		bookMarkDto.setBookmarkName(name);
		bookMarkDto.setBookmarkData(data == null ? "" : String.valueOf(data));
		return bookMarkDto;
	}

	/**
	 * Method Description: Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template.
	 * 
	 * @param name
	 * @param data
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmark(String name, Object data) {
		BookmarkDto bookMarkDto = new BookmarkDto();
		bookMarkDto.setBookmarkName(name);
		if ((String.valueOf(data).equalsIgnoreCase(FormConstants.NULL_DATE))
				|| (String.valueOf(data)).equals(FormConstants.STR_ZERO)) {
			bookMarkDto.setBookmarkData("");
		} else {
			bookMarkDto.setBookmarkData(data == null ? "" : String.valueOf(data));
		}
		return bookMarkDto;
	}

	/**
	 * Method Description: Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template.
	 * 
	 * @param map
	 * @param field
	 * @param key
	 * @param codesTable
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmarkFromMap(Map map, String field, String key, String codesTable) {
		Object value = map.get(key);
		Object data = codesTable == null ? value : lookupDao.simpleDecodeSafe(codesTable, (String) value);
		return createBookmark(field, data);
	}

	/**
	 * Method Description: Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template. This method accepts a codes
	 * table name to simplify the lookup for code values in the database.
	 * 
	 * @param field
	 * @param value
	 * @param codesTable
	 * @return BookMarkDto
	 * 
	 */
	public BookmarkDto createBookmarkWithCodesTable(String field, Object value, String codesTable) {
		return createBookmark(field, getDecodedValue(value, codesTable));
	}
	
	/***ALM Defect# 15099 - Unable to Create Common Application. Refactored the code***/
	public Object getDecodedValue(Object value, String codesTable) {
		Object data = null;
		if (null != value) {
			data = codesTable == null ? value : lookupDao.simpleDecodeSafe(codesTable, (String) value);
		} else {
			data = value;
		}
		return data;
	}

	/**
	 * Method Description:Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template. This method generates a
	 * special bookmark xml that will pull in another narrative into the
	 * template. The narrative must be accessible via Event ID as its primary or
	 * unique key on the narrative table.
	 * 
	 * @param name
	 * @param tableName
	 * @param id
	 * @return BlobDataDto
	 * 
	 */
	public BlobDataDto createBlobData(String name, String tableName, int id) {
		BlobDataDto blobDataDto = new BlobDataDto();
		blobDataDto.setBookmarkName(name);
		blobDataDto.setBlobTableName(tableName);
		blobDataDto.setBlobId(String.valueOf(id));
		return blobDataDto;
	}

	/**
	 * Method Description:Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template. This method generates a
	 * special bookmark xml that will pull in another narrative into the
	 * template. The narrative must be accessible via Event ID as its primary or
	 * unique key on the narrative table.
	 * 
	 * @param name
	 * @param tableName
	 * @param id
	 * @return BlobDataDto
	 * 
	 */
	public BlobDataDto createBlobData(String name, String tableName, String id) {
		BlobDataDto blobDataDto = new BlobDataDto();
		blobDataDto.setBookmarkName(name);
		blobDataDto.setBlobTableName(tableName);
		blobDataDto.setBlobId(id);
		return blobDataDto;
	}
	
	/**
	 * Method Description:Generates bookmark xml which for the forms
	 * architecture to consume. The bookmark xml are essentially name/value
	 * pairs which are replaced in an html template. This method generates a
	 * special bookmark xml that will pull in another narrative into the
	 * template. The narrative must be accessible via Event ID as its primary or
	 * unique key on the narrative table.
	 *
	 * @param name the name
	 * @param blobValue the blob value
	 * @return BlobDataDto
	 */
	public BlobDataDto createBlobValueData(String name, String blobValue, Long idTemplate) {
		BlobDataDto blobDataDto = new BlobDataDto();
		blobDataDto.setBookmarkName(name);
		blobDataDto.setBlobEncodedValue(blobValue);
		blobDataDto.setBlobId(String.valueOf(idTemplate));
		return blobDataDto;
	}

	/**
	 * Method Description: Generates repeating group xml which for the forms
	 * architecture to consume. Repeating Groups are sections of html that are
	 * repeated (e.g. listing rows in a table). They may contain Bookmarks and
	 * BlobData or other Repeating Groups.
	 * 
	 * @param formDataGroupBookmarkName
	 * @param subGroupTemplateName
	 * @return FormDataGroupDto
	 * 
	 */
	public FormDataGroupDto createFormDataGroup(String formDataGroupBookmarkName, String subGroupTemplateName) {
		FormDataGroupDto formDataGroupDto = new FormDataGroupDto();
		formDataGroupDto.setFormDataGroupBookmark(formDataGroupBookmarkName);
		formDataGroupDto.setSubGroupTemplate(subGroupTemplateName);
		return formDataGroupDto;
	}

}
