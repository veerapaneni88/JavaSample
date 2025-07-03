package us.tx.state.dfps.service.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaliDto;
import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaloDto;
import us.tx.state.dfps.service.admin.service.ListPersonMedMentalService;
import us.tx.state.dfps.service.common.response.ListPersonMedMentalRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:controller
 * for fetching person_address,person phone, adress_person_link details Aug 18,
 * 2017- 12:22:04 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/professionalassesmentaddress")
public class ListPersonMedMentalController {

	@Autowired
	ListPersonMedMentalService listPersonMedMentalService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ListPersonMedMentalController.class);

	/**
	 * method name: fetchPersonDetails method description: used to handle/map
	 * request to get the medical mental assessment professional assessment
	 * detail
	 * 
	 * @param listPersonMedMentaliDto
	 * @return response
	 */
	@RequestMapping(value = "/getaddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ListPersonMedMentalRes fetchPersonDetails(
			@RequestBody ListPersonMedMentaliDto listPersonMedMentaliDto) {
		log.debug("Entering method fetchPersonDetails in ListPersonMedMentalController");

		List<ListPersonMedMentaloDto> listPersonMedMentaloDto = new ArrayList<ListPersonMedMentaloDto>();

		ListPersonMedMentalRes response = new ListPersonMedMentalRes();
		// check the person id is present in the request
		if (TypeConvUtil.isNullOrEmpty(listPersonMedMentaliDto.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ListPersonMedMentalController.IdPerson.mandatory", null, Locale.US));
		}

		listPersonMedMentaloDto = listPersonMedMentalService.getListPersonMedMentalDetail(listPersonMedMentaliDto);

		response.setResponse(listPersonMedMentaloDto);

		log.debug("Exiting method fetchPersonDetails in ListPersonMedMentalController");
		return response;
	}

}
