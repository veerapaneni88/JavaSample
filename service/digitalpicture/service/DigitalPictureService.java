/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:10:47 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.digitalpicture.service;

import org.springframework.stereotype.Service;

import us.tx.state.dfps.common.domain.PictureDetail;
import us.tx.state.dfps.service.common.request.DigitalPictureReq;
import us.tx.state.dfps.service.common.response.DigitalPictureRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 2, 2017- 5:10:47 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public interface DigitalPictureService {

	/**
	 * This method will call the dao to insert the picture detail in
	 * Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes
	 * @, ParseException
	 */
	public DigitalPictureRes addDigitalPicture(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will call the dao to fetch the picture details in
	 * Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public DigitalPictureRes getDigitalPictureDetails(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will updates the picture detail in Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public DigitalPictureRes getDigitalPictureList(int extDocmnttnID);

	/**
	 * This method will call the dao to updates the picture detail in
	 * Picture_details table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public DigitalPictureRes updateDigitalPicture(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will call the dao to fetch the documentation details from
	 * ext_documantation table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public DigitalPictureRes getExtDocDetail(int idExtDocDetail);

	/**
	 * This method will call the dao to check for the existing documentation and
	 * updates the needed references in ext_documantation table
	 * 
	 * @param digitalPictureReq
	 * @return digitalPictureRes @
	 */
	public DigitalPictureRes checkAndUpdateExternalDocumentation(DigitalPictureReq digitalPictureReq);

	/**
	 * This method will call the dao to fetch the picture detail for the
	 * provided id from the picture _detail table
	 * 
	 * @param int
	 *            idPictureDetail
	 * @return pictureDetail @
	 */
	public PictureDetail getPictureDetailById(int idPictureDetail);

}
