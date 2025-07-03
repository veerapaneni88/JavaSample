package us.tx.state.dfps.service.admin.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.PersonAddressDetailsDao;
import us.tx.state.dfps.service.admin.dao.PersonPhoneDetailsDao;
import us.tx.state.dfps.service.admin.dto.AddrPhoneDto;
import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsDto;
import us.tx.state.dfps.service.admin.dto.PersonAddressDetailsReq;
import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailReq;
import us.tx.state.dfps.service.admin.dto.PersonPhoneDetailsDto;
import us.tx.state.dfps.service.admin.service.AddrPhoneRtrvService;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * AddrPhoneRtrvServiceImpl Jul 6, 2018- 11:48:38 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class AddrPhoneRtrvServiceImpl implements AddrPhoneRtrvService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonAddressDetailsDao addressPhoneDetailsDao;

	@Autowired
	PersonPhoneDetailsDao personPhoneDetailsDao;

	private static final Logger log = Logger.getLogger(AddrPhoneRtrvServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.service.AddrPhoneRtrvService#
	 * callAddrPhoneRtrvService(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AddrPhoneDto callAddrPhoneRtrvService(Long idPerson) {
		List<AddrPhoneDto> addrPhoneDtoList = new ArrayList<AddrPhoneDto>();
		PersonAddressDetailsReq personAddressDetailsReq = new PersonAddressDetailsReq();
		PersonPhoneDetailReq personPhoneDetailReq = new PersonPhoneDetailReq();
		DateFormat df = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_YY_MM_DD);
		Date today = Calendar.getInstance().getTime();
		String sysTsQuery = df.format(today);
		if (!ObjectUtils.isEmpty(idPerson)) {
			personAddressDetailsReq.setIdPerson(idPerson);
		}
		personAddressDetailsReq.setIndIntake(ServiceConstants.STRING_IND_Y);
		personAddressDetailsReq.setTsSysTsQuery(sysTsQuery);
		List<PersonAddressDetailsDto> personAdrsList = addressPhoneDetailsDao
				.getPersonAddressDtls(personAddressDetailsReq);

		for (PersonAddressDetailsDto personAddressDetailsDto : personAdrsList) {
			AddrPhoneDto addrPhoneDto = new AddrPhoneDto();
			boolean isPrimaryExists = !ObjectUtils.isEmpty(personAddressDetailsDto.getIndPersAddrLinkPrimary());

			if (isPrimaryExists
					&& ServiceConstants.STRING_IND_Y
							.equalsIgnoreCase(personAddressDetailsDto.getIndPersAddrLinkPrimary())
					&& ServiceConstants.MAX_DATE.equals(personAddressDetailsDto.getDtPersAddrLinkEnd())) {
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getAddrPersAddrStLn1())) {
					addrPhoneDto.setAddrPersAddrStLn1(personAddressDetailsDto.getAddrPersAddrStLn1());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getAddrPersAddrStLn2())) {
					addrPhoneDto.setAddrPersAddrStLn2(personAddressDetailsDto.getAddrPersAddrStLn2());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getTxtPersAddrCmnts())) {
					addrPhoneDto.setTxtPersAddrCmnts(personAddressDetailsDto.getTxtPersAddrCmnts());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getAddrZip())) {
					addrPhoneDto.setAddrZip(personAddressDetailsDto.getAddrZip());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getCdAddrCounty())) {
					addrPhoneDto.setCdAddrCounty(personAddressDetailsDto.getCdAddrCounty());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getAddrCity())) {
					addrPhoneDto.setAddrCity(personAddressDetailsDto.getAddrCity());
				}
				if (!ObjectUtils.isEmpty(personAddressDetailsDto.getCdAddrState())) {
					addrPhoneDto.setCdAddrState(personAddressDetailsDto.getCdAddrState());
				}
				addrPhoneDtoList.add(addrPhoneDto);
			}
		}

		if (!ObjectUtils.isEmpty(addrPhoneDtoList)) {
			if (!ObjectUtils.isEmpty(idPerson)) {
				personPhoneDetailReq.setIdPerson(idPerson);
			}
			personPhoneDetailReq.setSysIndIntake(ServiceConstants.STRING_IND_Y);
			personPhoneDetailReq.setTsSysTsQuery(sysTsQuery);
			List<PersonPhoneDetailsDto> personDetailsList = personPhoneDetailsDao
					.getPersonPhoneDetails(personPhoneDetailReq);
			if (!ObjectUtils.isEmpty(personDetailsList)) {
				for (PersonPhoneDetailsDto personPhoneDetailsDto : personDetailsList) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(personPhoneDetailsDto.getDtDtPersonPhoneEnd());
					boolean isPrimaryPhoneExists = !ObjectUtils
							.isEmpty(personPhoneDetailsDto.getIndPersonPhonePrimary());
					if ((isPrimaryPhoneExists
							&& personPhoneDetailsDto.getIndPersonPhonePrimary().equals(ServiceConstants.STRING_IND_Y))
							&& ((calendar.get(Calendar.DAY_OF_MONTH) == ServiceConstants.ARC_MAX_DAY)
									&& (calendar.get(Calendar.MONTH) + 1 == ServiceConstants.ARC_MAX_MONTH)
									&& (calendar.get(Calendar.YEAR) == ServiceConstants.ARC_MAX_YEAR))) {
						for (AddrPhoneDto addrPhoneDto : addrPhoneDtoList) {
							if (!ObjectUtils.isEmpty(personPhoneDetailsDto.getNbrPhone())) {
								addrPhoneDto.setNbrPhone(personPhoneDetailsDto.getNbrPhone());
							}
							if (!ObjectUtils.isEmpty(personPhoneDetailsDto.getNbrPhoneExtension())) {
								addrPhoneDto.setNbrPhoneExtension(personPhoneDetailsDto.getNbrPhoneExtension());
							}
						}
					}
				}
			}
		}
		log.debug("Exiting method callAddrPhoneRtrvService in AddrPhoneRtrvServiceImpl");
		return !ObjectUtils.isEmpty(addrPhoneDtoList) ? addrPhoneDtoList.get(0) : null;
	}
}
