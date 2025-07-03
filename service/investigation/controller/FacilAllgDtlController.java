package us.tx.state.dfps.service.investigation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.GetFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.common.response.GetFacilAllegDetailRes;
import us.tx.state.dfps.service.common.response.UpdtFacilAllegDetailRes;
import us.tx.state.dfps.service.investigation.service.FacilAllgDtlService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CINV07S,CINV08S,CINV10S Class Description: This class is to
 * retrieves,saves,updates,multi update Facility Allegation Detail page.
 */
@RestController
@RequestMapping("/facil")
public class FacilAllgDtlController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FacilAllgDtlService facilAllgDtlService;

	/**
	 * Method Description:This method is to Retrieves Facility Allegation Detail
	 * Information Uses DAMS CINV70D, CINV69D, CCMN87D, CSEC54D, CCMNB5D and
	 * CINV08D legacy service name - CINV07S
	 * 
	 * @param getFacilAllegDetailReq
	 * @return
	 */
	@RequestMapping(value = "/getFacilAllegList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  GetFacilAllegDetailRes getFacilAllegList(
			@RequestBody GetFacilAllegDetailReq getFacilAllegDetailReq) {
		return facilAllgDtlService.getallegtnlist(getFacilAllegDetailReq);

	}

	/**
	 * This method is to update facility allegation details legacy service name
	 * - CINV08S
	 * 
	 * @param updtFacilAllegDetailReq
	 * @return
	 */
	@RequestMapping(value = "/updateFacilAlleg", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  UpdtFacilAllegDetailRes updateFacilAlleg(
			@RequestBody UpdtFacilAllegDetailReq updtFacilAllegDetailReq) {

		return facilAllgDtlService.updateFacilAlleg(updtFacilAllegDetailReq);

	}

	/**
	 * This is rest service to update multiple allegations CINV10S
	 * 
	 * @param updtFacilAllegMultiDtlReq
	 * @return
	 */
	@RequestMapping(value = "/updateMultiFacilAlleg", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  UpdtFacilAllegDetailRes updateMultiFacilAlleg(
			@RequestBody UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq) {

		facilAllgDtlService.updateFacilAllegMulti(updtFacilAllegMultiDtlReq);
		UpdtFacilAllegDetailRes updtFacilAllegDetailRes = new UpdtFacilAllegDetailRes();
		updtFacilAllegDetailRes.setMessage(ServiceConstants.SUCCESS);
		return updtFacilAllegDetailRes;

	}

}
