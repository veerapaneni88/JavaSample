package us.tx.state.dfps.service.person.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.service.common.request.EducationHistoryReq;
import us.tx.state.dfps.service.common.response.EducationHistoryRes;
import us.tx.state.dfps.service.person.dao.EducationHistoryDao;
import us.tx.state.dfps.service.person.dao.EducationalNeedListDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationNeededListDto;
import us.tx.state.dfps.service.person.dto.PersonEducationNeedDto;
import us.tx.state.dfps.service.person.service.EducationHistoryService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION class name:
 * EducationHistoryServiceImp Class description : CCFC17S tux service conversion
 * to get the Educational History Detail tables March, 2018- © 2017 Texas
 * Department of Family and Protective Services
 *
 */
@Service
@Transactional
public class EducationHistoryServiceImp implements EducationHistoryService {

	@Autowired
	EducationHistoryDao educationHistoryDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	EducationalNeedListDao csesc1dDao;

	@Autowired
	PersonDao personDao;

	private static final Logger log = Logger.getLogger(EducationHistoryServiceImp.class);

	public EducationHistoryServiceImp() {
	}

	/**
	 * 
	 * Method Description: This Method will retrieve all rows from the education
	 * History Table for a person id. Service Name: CCFC17S
	 * 
	 * @param EducationHistoryReq
	 * @return EducationHistoryRes
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public EducationHistoryRes getPersonEducationHistoryList(EducationHistoryReq educationHistoryReq) {
		EducationHistoryRes educationHistoryRes = new EducationHistoryRes();
		List<EducationHistoryDto> educationHistoryList = educationHistoryDao
				.getPersonEducationHistoryList(educationHistoryReq);

		PersonDto personDto;
		// set child name
		if (!ObjectUtils.isEmpty(educationHistoryList) && educationHistoryList.size() > 0) {
			personDto = personDao.getPersonById(educationHistoryList.get(0).getIdPerson());
			if (!ObjectUtils.isEmpty(personDto)) {
				for (EducationHistoryDto educationHistoryDto : educationHistoryList) {
					educationHistoryDto.setPersonName(personDto.getNmPersonFull());
				}
			}
		}
		CapsResource capsResource;

		for (EducationHistoryDto educationId : educationHistoryList) {
			if (educationId.getIndEdHistTeaSchool().equalsIgnoreCase("I") && (null != educationId.getIdResource())) {

				capsResource = capsResourceDao.getCapsResourceById(educationId.getIdResource());
				educationId.setAddrEdHistCnty(capsResource.getCdRsrcCnty());
				educationId.setAddrEdHistCity(capsResource.getAddrRsrcCity());
				educationId.setAddrEdHistState(capsResource.getCdRsrcState());
				educationId.setAddrEdHistStreetLn1(capsResource.getAddrRsrcStLn1());
				educationId.setAddrEdHistStreetLn2(capsResource.getAddrRsrcStLn2());
				educationId.setAddrEdHistZip(capsResource.getAddrRsrcZip());
				educationId.setEdHistPhone(capsResource.getNbrRsrcPhn());
				educationId.setEdHistPhoneExt(capsResource.getNbrRsrcPhoneExt());

			}

			if (null != CallCreateEduStr(educationId.getIdEdHist(), educationId.getIdPerson()))
				educationId
						.setCdEducationalNeed((CallCreateEduStr(educationId.getIdEdHist(), educationId.getIdPerson())));

		}

		educationHistoryRes.setEducationHistoryDtoList(educationHistoryList);
		log.info("TransactionId :" + educationHistoryReq.getTransactionId());
		return educationHistoryRes;
	}

	/**
	 *
	 * @param idEdHist
	 * @param idPerson
	 * @param iIndex
	 * @param pOutputMsg
	 * @return
	 * 
	 */
	private String CallCreateEduStr(Long idEdHist, Long idPerson) {
		log.debug("Entering method CallCreateEduStr in EducListDtlRtrvServiceImpl");

		EducationHistoryReq pCSESC1DInputRec = new EducationHistoryReq();
		EducationNeededListDto csesc1doDto = new EducationNeededListDto();
		StringBuilder tempEduNeed = new StringBuilder();

		pCSESC1DInputRec.setIdEdhist(idEdHist);
		pCSESC1DInputRec.setIdPerson(idPerson);

		csesc1doDto = csesc1dDao.getEducationalNeedList(pCSESC1DInputRec);
		if (csesc1doDto != null) {

			for (PersonEducationNeedDto ed : csesc1doDto.getPersonEducationNeed()) {
				if (null != ed.getCdEducationalNeed())
					tempEduNeed = tempEduNeed.append(ed.getCdEducationalNeed());
			}

		}
		return tempEduNeed.toString();
	}
}