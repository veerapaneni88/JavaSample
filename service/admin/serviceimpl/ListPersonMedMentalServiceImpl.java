package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaliDto;
import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaloDto;
import us.tx.state.dfps.service.admin.service.ListPersonMedMentalService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:class for
 * fetching person_address,person phone, adress_person_link details Aug 18,
 * 2017- 12:24:42 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ListPersonMedMentalServiceImpl implements ListPersonMedMentalService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	AddrPersonLinkPhoneDao addrPersonLinkPhoneDao;

	private static final Logger log = Logger.getLogger(ListPersonMedMentalServiceImpl.class);

	/**
	 * callListPersonMedMentalService -This service method used to call dam for
	 * retrieve Professional Address and Phone info.
	 * 
	 * @param pInputMsg
	 * @return finalResultList @
	 */

	public static final String MAX_DATE_STRING = "4712-12-31";

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ListPersonMedMentaloDto> getListPersonMedMentalDetail(ListPersonMedMentaliDto pInputMsg) {
		log.debug("Entering method callListPersonMedMentalService in ListPersonMedMentalServiceImpl");
		AddrPersonLinkPhoneInDto pCINV46DInputRec = new AddrPersonLinkPhoneInDto();
		List<ListPersonMedMentaloDto> finalResultList = new ArrayList<ListPersonMedMentaloDto>();
		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getIdPerson()) || pInputMsg.getIdPerson() != 0l) {
			pCINV46DInputRec.setUlIdPerson(pInputMsg.getIdPerson());
		}

		pCINV46DInputRec.setDtMaxDate(MAX_DATE_STRING);

		List<AddrPersonLinkPhoneOutDto> addrPersonLinkPhoneOutDtoList = addrPersonLinkPhoneDao
				.cinv46dQUERYdam(pCINV46DInputRec);

		if (!TypeConvUtil.isNullOrEmpty(addrPersonLinkPhoneOutDtoList)
				&& !CollectionUtils.isEmpty(addrPersonLinkPhoneOutDtoList)) {
			for (AddrPersonLinkPhoneOutDto output : addrPersonLinkPhoneOutDtoList) {
				ListPersonMedMentaloDto listPersonMedMentaloDto = new ListPersonMedMentaloDto();
				if (!TypeConvUtil.isNullOrEmpty(output.getPhone())) {
					listPersonMedMentaloDto.setPhone(output.getPhone());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getPhoneExtension())) {
					listPersonMedMentaloDto.setPhoneExtension(output.getPhoneExtension());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getAddrCity())) {
					listPersonMedMentaloDto.setAddrProfAssmtCity(output.getAddrCity());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getCdAddrState())) {
					listPersonMedMentaloDto.setAddrProfAssmtState(output.getCdAddrState());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getAddrPersAddrStLn1())) {
					listPersonMedMentaloDto.setAddrProfAssmtStLn1(output.getAddrPersAddrStLn1());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getAddrPersAddrStLn2())) {
					listPersonMedMentaloDto.setAddrProfAssmtStLn2(output.getAddrPersAddrStLn2());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getAddrZip())) {
					listPersonMedMentaloDto.setAddrProfAssmtZip(output.getAddrZip());
				}
				if (!TypeConvUtil.isNullOrEmpty(output.getCdAddrCounty())) {
					listPersonMedMentaloDto.setCdProfAssmtCounty(output.getCdAddrCounty());
				}
				finalResultList.add(listPersonMedMentaloDto);
			}
		}
		log.debug("Exiting method callListPersonMedMentalService in ListPersonMedMentalServiceImpl");
		return finalResultList;
	}
}
