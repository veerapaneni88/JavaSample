/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Implementation Class for Child Sexual Aggression page.
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.cshattachmenta.serviceimpl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.cshattachmenta.dao.AttachmentADao;
import us.tx.state.dfps.service.cshattachmenta.service.AttachmentAService;
import us.tx.state.dfps.service.forms.dto.AttachmentADto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CshAttachmentAPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * service-business-FCL
 * Class Name: AttachmentAServiceImpl 
 * Description:This is the Service Victimization history Page. 
 * © 2019 Texas Department of Family and Protective Services 
 * FCL Artifact ID: artf128756
 **/
@Service
@Transactional
public class AttachmentAServiceImpl implements AttachmentAService {

	@Autowired
	CshAttachmentAPrefillData attachmentAPrefillData;

	@Autowired
	AttachmentADao attachmentADao;

	@Autowired
	CSADao csaDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	TraffickingDao trfckngDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getCshDetailsByIDPerson(Long idPerson) {
		AttachmentADto attachmentADto = new AttachmentADto();

		PersonDto personDto = personDao.getPersonById(idPerson);
		attachmentADto.setIdPerson(personDto.getIdPerson());
		attachmentADto.setNmPerson(personDto.getNmPersonFull());

		SexualVictimHistoryDto sexualVictimHistoryDto = attachmentADao.fetchSexualVictimHistory(idPerson);
		//List<CSAEpisodeDto> csaDtoResponseList = csaDao.fetchCSAList(idPerson,true);
		List<CsaEpisodesIncdntDto> csaDtoResponseList = csaDao.fetchCSAIncidentList(idPerson,true);

		TraffickingDto traffickingDto = new TraffickingDto();
		traffickingDto.setIdPerson(BigDecimal.valueOf(idPerson));

		List<TraffickingDto> traffickingRespList = trfckngDao.getTraffickingList(traffickingDto);
		
		attachmentADto.setCsaEpisodesIncdntDtoList(csaDtoResponseList);
		attachmentADto.setSxIcdntDtoList(sexualVictimHistoryDto.getSvhIncidents());
		attachmentADto.setSxVictimHistoryDtoSpvsrCmts(sexualVictimHistoryDto.getSupervisionContactDesc());
		attachmentADto.setIndChildSxVctmztnHist(sexualVictimHistoryDto.getIndChildHasSxVictimHistory());
		attachmentADto.setTrfckngDtoList(traffickingRespList);
        attachmentADto.setIndUnconfirmedVictim(sexualVictimHistoryDto.getIndUnconfirmedVictimHistory());
        attachmentADto.setIndSbp(sexualVictimHistoryDto.getIndSexualBehaviorProblem());
        attachmentADto.setTxtSbp(sexualVictimHistoryDto.getTxtBehaviorProblems());
		attachmentADto.setIdUpdPrsUnconfVicHistUser(sexualVictimHistoryDto.getIdUpdPrsUnconfVicHistUser());

		return attachmentAPrefillData.returnPrefillData(attachmentADto);
	}

}