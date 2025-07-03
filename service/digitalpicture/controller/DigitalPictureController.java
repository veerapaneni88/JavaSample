/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:06:12 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.digitalpicture.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.DigitalPictureReq;
import us.tx.state.dfps.service.common.response.DigitalPictureRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 2, 2017- 5:06:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/digitalPicture")
public class DigitalPictureController {

	@Autowired
	DigitalPictureService digitalPictureService;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Description:This method adds a new digital picture in the picture
	 * detail table EJB Name: DigitalPictDetailBean
	 * 
	 * @param DigitalPictureReq
	 * @return DigitalPictureRes
	 * 
	 */
	@RequestMapping(value = "/addPicture", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes addDigitalPicture(@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean())) {
			throw new InvalidRequestException(messageSource.getMessage("common.inputcheck.data", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdExtDocumentation())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.idExtDoc.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getSubject())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.txtsubject.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getCdPictureType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.picturetypecd.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getDescription())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.txtdescription.mandatory", null, Locale.US));
		}
		/*
		 * if
		 * (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().
		 * getIdPerson())) { throw new InvalidRequestException(
		 * messageSource.getMessage("digitalPicture.personid.mandatory", null,
		 * Locale.US)); }
		 */
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getNmImage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.nmimage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getNmThumbnail())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.nmthumbnail.mandatory", null, Locale.US));
		}

		return digitalPictureService.addDigitalPicture(digitalPictureReq);
	}

	/**
	 * 
	 * Method Description:This method updates the digital picture details in the
	 * picture detail table EJB Name: DigitalPictDetailBean
	 * 
	 * @param DigitalPictureReq
	 * @return DigitalPictureRes
	 */
	@RequestMapping(value = "/updatePicture", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes updateDigitalPicture(@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean())) {
			throw new InvalidRequestException(messageSource.getMessage("common.inputcheck.data", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdPictureDetail())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.pictureid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getSubject())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.txtsubject.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getCdPictureType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.picturetypecd.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getDescription())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.txtdescription.mandatory", null, Locale.US));
		}

		return digitalPictureService.updateDigitalPicture(digitalPictureReq);
	}

	/**
	 * 
	 * Method Description:This method gets the external documentation details
	 * from the Ext document table EJB Name: DigitalPictDetailBean
	 * 
	 * @param DigitalPictureReq
	 * @return DigitalPictureRes
	 */
	@RequestMapping(value = "/getExtDocDetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes getExtDocDetail(@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getIdExtDoc())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.idExtDoc.mandatory", null, Locale.US));
		}

		return digitalPictureService.getExtDocDetail(digitalPictureReq.getIdExtDoc());

	}

	/**
	 * 
	 * Method Description:This method checks and updates/deletes the Ext
	 * document table based on other EJB Name: DigitalPictDetailBean
	 * 
	 * @param DigitalPictureReq
	 * @return DigitalPictureRes
	 */
	@RequestMapping(value = "/getCheckAndUpdateExternalDocumentation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes checkAndUpdateExternalDocumentation(
			@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean())) {
			throw new InvalidRequestException(messageSource.getMessage("common.inputcheck.data", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdPictureDetail())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.pictureid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdExtDocumentation())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.idExtDoc.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.caseid.mandatory", null, Locale.US));
		}

		return digitalPictureService.checkAndUpdateExternalDocumentation(digitalPictureReq);
	}

	/**
	 * Method Name: getDigitalPictureList Method Description:This method used to
	 * get Digital Picture List Information based on the given External
	 * Documentation ID
	 * 
	 * @param DigitalPictureReq
	 * @return DigitalPictureRes
	 */
	@RequestMapping(value = "/getDigitalPictureList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes getDigitalPictureList(@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getIdExtDoc())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.idExtDoc.mandatory", null, Locale.US));
		}
		return digitalPictureService.getDigitalPictureList(digitalPictureReq.getIdExtDoc());
	}

	/**
	 * Method Name: getDigitalPictureDetails Method Description:
	 * 
	 * @param digitalPictureReq
	 * @return
	 */
	@RequestMapping(value = "/getDigitalPictureDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DigitalPictureRes getDigitalPictureDetails(@RequestBody DigitalPictureReq digitalPictureReq){

		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getPictureDetailBean().getIdPictureDetail())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.pictureid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(digitalPictureReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("digitalPicture.userId.mandatory", null, Locale.US));
		}
		return digitalPictureService.getDigitalPictureDetails(digitalPictureReq);
	}
}
