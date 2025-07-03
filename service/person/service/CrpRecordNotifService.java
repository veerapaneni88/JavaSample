package us.tx.state.dfps.service.person.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import us.tx.state.dfps.service.common.request.CrpRecordNotifReq;
import us.tx.state.dfps.service.common.request.FormsReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.person.dto.CentralRegistryCheckDto;

/**
 * service-business- IMPACT PHASE 2 Class Description: service
 *  * for Central Registry Portal, to populate CRP Record Notif Forms Jan 25, 2024-
 *  * 08:26:00 AM © 2024 Texas Department of Family and Protective Services
 *
 * ********Change History**********
 * 04/22/2022 thompswa Initial.
 */
public interface CrpRecordNotifService {

	/**
	 * Method Description: This method is used to retrieve
	 * CRP Record Notif Forms. This form constitutes the body of an email,
	 * that provides notification of results of the run of background checks
	 * as per the Child Care Development Block Grant (CCDBG) in 2014
	 * passing IdCrpRecordNotif as input request
	 *
	 * @param crpRecordNotifReq
	 * @return PreFillDataServiceDto @
	 */
	public CommonFormRes getCrpRecordNotif(CrpRecordNotifReq crpRecordNotifReq);

	/**
	 * Method Description: This Service is used to retrieve the notification
	 * form for the crp record check detail screen.
	 *
	 * @param crpRecordNotifReq
	 * @return CommonFormRes
	 */
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetails(@RequestBody CrpRecordNotifReq crpRecordNotifReq);

	/**
	 * Returns the centralRegistryCheckDto using idRequest
	 *
	 * @param idRequest
	 * @return centralRegistryCheckDto
	 */
	public CentralRegistryCheckDto getCentralRegistryCheckDto(Long idRequest);

	/**
	 * Returns the crpRecordNotifAndDetailsDto using idRequest
	 *
	 * @param crpRecordNotifReq
	 * @return crpRecordNotifAndDetailsDto
	 */
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetailsDto(CrpRecordNotifReq crpRecordNotifReq);
}
