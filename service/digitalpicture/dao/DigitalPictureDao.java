/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:12:49 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.digitalpicture.dao;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.PictureDetail;
import us.tx.state.dfps.service.common.request.DigitalPictureReq;
import us.tx.state.dfps.service.workload.dto.ExternalDocumentDetailDto;
import us.tx.state.dfps.web.casemanagement.bean.PictureDetailBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 2, 2017- 5:12:49 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public interface DigitalPictureDao {

	/**
	 * This method will insert the picture detail in Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return PictureDetail
	 * @, ParseException
	 */
	public PictureDetail addDigitalPicture(DigitalPictureReq digitalPictureReq) throws ParseException;

	/**
	 * This method will fetch the picture details in Picture_details table
	 * 
	 * @param idPictureDetail
	 * @param idUser
	 * @return PictureDetailBean @
	 */
	public PictureDetailBean getDigitalPictureDetails(int idPictureDetail, String idUser);

	/**
	 * This method will updates the picture detail in Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return void @
	 */
	public PictureDetail updateDigitalPicture(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will check for the existing documentation and updates the
	 * needed references in ext_documantation table
	 * 
	 * @param digitalPictureReq
	 * @return boolean @
	 */
	public boolean checkAndUpdateExternalDocumentation(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will fetch the documentation details from ext_documantation
	 * table
	 * 
	 * @param digitalPictureReq
	 * @return ExternalDocumentDetailDto @
	 */
	public ExternalDocumentDetailDto getExtDocDetail(int idExtDocDetail);

	/**
	 * This method will fetch the picture detail for the provided id from the
	 * picture _detail table
	 * 
	 * @param int
	 *            idPictureDetail
	 * @return PictureDetail @
	 */
	public PictureDetail getPictureDetailById(int idPictureDetail);

	/**
	 * This method will updates the picture detail in Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public List<PictureDetailBean> getDigitalPictureList(int extDocmnttnID);
}
