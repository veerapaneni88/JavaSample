package us.tx.state.dfps.service.conservatorship.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ConservtorshipRmlReq;
import us.tx.state.dfps.service.common.response.ConservtorshipRmlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.service.ConservtorshipRmlService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: method
 * declared in the ConservtorshipRmlBean Sep 8, 2017- 12:12:36 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/conservtorshiprml")
public class ConservtorshipRmlController {

	@Autowired
	ConservtorshipRmlService conservtorshipRmlService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: updateDenyDate Method Description: update the deny date
	 * 
	 * @param conservtorshipRmlReq
	 * @return ConservtorshipRmlRes
	 */
	@RequestMapping(value = "/updateDenyDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ConservtorshipRmlRes updateDenyDate(@RequestBody ConservtorshipRmlReq conservtorshipRmlReq) {

		if (TypeConvUtil.isNullOrEmpty(conservtorshipRmlReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("conservtorshipRml.ulIdPerson.mandatory", null, Locale.US));
		}

		return conservtorshipRmlService.updateDenyDate(conservtorshipRmlReq.getIdPerson());

	}

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:get the boolean
	 * flag for rlnqushQuestionAnsweredY
	 * 
	 * @param conservtorshipRmlReq
	 * @return ConservtorshipRmlRes
	 */
	@RequestMapping(value = "/rlnqushQuestionAnsweredY", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ConservtorshipRmlRes rlnqushQuestionAnsweredY(
			@RequestBody ConservtorshipRmlReq conservtorshipRmlReq) {
		if (TypeConvUtil.isNullOrEmpty(conservtorshipRmlReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("conservtorshipRml.ulIdStage.mandatory", null, Locale.US));
		}
		ConservtorshipRmlRes conservtorshipRmlRes = new ConservtorshipRmlRes();
		conservtorshipRmlRes.setRlnqushQuestionAnsweredY(conservtorshipRmlService
				.rlnqushQuestionAnsweredY(conservtorshipRmlReq.getIdStage(), conservtorshipRmlReq.getIdVictim()));
		return conservtorshipRmlRes;
	}

	/**
	 * Method Name: prsnCharSelected Method Description:get the prsnCharSelected
	 * dtl
	 * 
	 * @param conservtorshipRmlReq
	 * @return ConservtorshipRmlRes
	 */
	@RequestMapping(value = "/prsnCharSelected", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ConservtorshipRmlRes prsnCharSelected(@RequestBody ConservtorshipRmlReq conservtorshipRmlReq) {
		if (TypeConvUtil.isNullOrEmpty(conservtorshipRmlReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("conservtorshipRml.ulIdVictim.mandatory", null, Locale.US));
		}
		ConservtorshipRmlRes conservtorshipRmlRes = new ConservtorshipRmlRes();
		conservtorshipRmlRes.setPrsnCharSelected(conservtorshipRmlService
				.prsnCharSelected(conservtorshipRmlReq.getIdStage(), conservtorshipRmlReq.getIdVictim()));

		return conservtorshipRmlRes;
	}

	/**
	 * Method Name: getCnsrvtrRmvlPersonId Method Description:fetch the
	 * CnsrvtrRmvlPersonId
	 * 
	 * @param conservtorshipRmlReq
	 * 
	 * @return ConservtorshipRmlRes
	 */
	@RequestMapping(value = "/getCnsrvtrRmvlPersonId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ConservtorshipRmlRes getCnsrvtrRmvlPersonId(
			@RequestBody ConservtorshipRmlReq conservtorshipRmlReq) {
		if (TypeConvUtil.isNullOrEmpty(conservtorshipRmlReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("conservtorshipRml.ulIdRemovalEvent.mandatory", null, Locale.US));
		}
		ConservtorshipRmlRes conservtorshipRmlRes = new ConservtorshipRmlRes();
		conservtorshipRmlRes.setPersonValueDto(conservtorshipRmlService
				.getCnsrvtrRmvlPersonId(conservtorshipRmlReq.getIdCase(), conservtorshipRmlReq.getIdRemovalEvent()));
		return conservtorshipRmlRes;
	}

}
