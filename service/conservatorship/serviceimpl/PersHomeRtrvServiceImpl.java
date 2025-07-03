package us.tx.state.dfps.service.conservatorship.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersHomeRtrvReq;
import us.tx.state.dfps.service.conservatorship.dao.NameStagePersonLinkPersonDao;
import us.tx.state.dfps.service.conservatorship.dao.PersonHomeRemovalSelRemDetlsDao;
import us.tx.state.dfps.service.conservatorship.service.PersHomeRtrvService;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonInDto;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonOutDto;
import us.tx.state.dfps.service.cvs.dto.PersHomeRtrvoDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Call the
 * serive class for PersonHomeRtrv Aug 2, 2017- 7:53:46 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PersHomeRtrvServiceImpl implements PersHomeRtrvService {

	// Clsc10d
	@Autowired
	NameStagePersonLinkPersonDao nameStagePersonLinkPersonDao;

	// Clss08d
	@Autowired
	PersonHomeRemovalSelRemDetlsDao personHomeRemovalSelRemDetlsDao;

	private static final Logger log = Logger.getLogger(PersHomeRtrvServiceImpl.class);

	/**
	 * 
	 * Method Name: callPersHomeRtrvService Method Description:This service
	 * retrieves all persons associated with an event from STAGE_PERSON_LINK and
	 * a subset of all persons at home during removal from PERSON_HOME_RMVL
	 * table. If the person is found in both tables, an attribute is set to
	 * true, so that the person will be 'checked' when displayed to the window.
	 * 
	 * @param persHomeRtrvReq
	 * @return List<PersHomeRtrvoDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PersHomeRtrvoDto> personHomeRtrv(PersHomeRtrvReq persHomeRtrvReq) {
		log.debug("Entering method callPersHomeRtrvService in PersHomeRtrvServiceImpl");
		NameStagePersonLinkPersonInDto nameStagePersonLinkPersonInDto = new NameStagePersonLinkPersonInDto();
		PersonHomeRemovalSelRemDetlsInDto personHomeRemovalSelRemDetlsInDto = new PersonHomeRemovalSelRemDetlsInDto();
		List<PersHomeRtrvoDto> liPersHomeRtrvoDto = null;
		nameStagePersonLinkPersonInDto.setIdStage(persHomeRtrvReq.getIdStage());
		nameStagePersonLinkPersonInDto.setCdStagePersType(ServiceConstants.PRINCIPAL);
		nameStagePersonLinkPersonInDto.setCdStagePersType(ServiceConstants.PERSON_TYPE);
		List<NameStagePersonLinkPersonOutDto> liClsc10dDao = nameStagePersonLinkPersonDao
				.getStagePersonLinkDetails(nameStagePersonLinkPersonInDto);
		liPersHomeRtrvoDto = new ArrayList<PersHomeRtrvoDto>();
		for (NameStagePersonLinkPersonOutDto objClsc10doDto : liClsc10dDao) {
			PersHomeRtrvoDto objPersHomeRtrvoDto = new PersHomeRtrvoDto();
			objPersHomeRtrvoDto.setIdPerson(objClsc10doDto.getIdPerson());
			objPersHomeRtrvoDto.setNmPersonFull(!StringUtils.isEmpty(objClsc10doDto.getCdNameSuffix())
					? objClsc10doDto.getNmPersonFull() + " " + objClsc10doDto.getCdNameSuffix()
					: objClsc10doDto.getNmPersonFull());
			objPersHomeRtrvoDto.setCdPersonRelationship(objClsc10doDto.getCdStagePersRelInt());
			objPersHomeRtrvoDto.setCCdPersonSex(objClsc10doDto.getCCdPersonSex());
			objPersHomeRtrvoDto.setDtPersonBirth(objClsc10doDto.getDtPersonBirth());
			liPersHomeRtrvoDto.add(objPersHomeRtrvoDto);
		}
		personHomeRemovalSelRemDetlsInDto.setIdEvent(persHomeRtrvReq.getIdEvent());
		List<PersonHomeRemovalSelRemDetlsOutDto> liClss08doDto = personHomeRemovalSelRemDetlsDao
				.getPersonHomeRemovalDetails(personHomeRemovalSelRemDetlsInDto);
		if (liClss08doDto != null && liClss08doDto.size() > 0) {
			for (PersHomeRtrvoDto objPersHomeRtrvoDto : liPersHomeRtrvoDto) {
				for (PersonHomeRemovalSelRemDetlsOutDto objClss08doDto : liClss08doDto) {
					if (objClss08doDto.getIdPersHmRemoval() == objPersHomeRtrvoDto.getIdPerson()) {
						objPersHomeRtrvoDto.setTsLastUpdate(objClss08doDto.getTsLastUpdate());
						objPersHomeRtrvoDto.setCScrIndRefChildMatch(ServiceConstants.STRING_IND_Y);
					}
				}
			}
		}
		log.debug("Exiting method callPersHomeRtrvService in PersHomeRtrvServiceImpl");
		return liPersHomeRtrvoDto;
	}
}
