package us.tx.state.dfps.service.conservatorship.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.response.ConservtorshipRmlRes;
import us.tx.state.dfps.service.conservatorship.dao.ConservtorshipRmlDao;
import us.tx.state.dfps.service.conservatorship.service.ConservtorshipRmlService;
import us.tx.state.dfps.service.cvs.dto.ConservtorshipRmlDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service
 * implementation for ConservtorshipRmlServiceImpl Sep 8, 2017- 12:18:03 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ConservtorshipRmlServiceImpl implements ConservtorshipRmlService {

	@Autowired
	ConservtorshipRmlDao conservtorshipRmlDao;

	/**
	 * Deletes all rows on EVENT_PERSON_LINK table and the EVENT row for the
	 * given event id. Deletes this data in a separate transaction to ensure the
	 * delete actually happens.
	 * 
	 * @param idPerson
	 * @return ConservtorshipRmlRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ConservtorshipRmlRes updateDenyDate(long idPerson) {
		ConservtorshipRmlRes conservtorshipRmlRes = new ConservtorshipRmlRes();
		conservtorshipRmlRes.setTotalRecCount(conservtorshipRmlDao.updateDenyDate(idPerson));

		return conservtorshipRmlRes;
	}

	/**
	 * This method returns true if atleast one of the relinquish allegation
	 * cstdy is answered yes.
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return boolean @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean rlnqushQuestionAnsweredY(long idStage, long idVictim) {
		long result = conservtorshipRmlDao.rlnqushQuestionAnsweredY(idStage, idVictim);
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method returns true if atleast one of the SB44 person char are
	 * selected.
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return boolean @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean prsnCharSelected(long idStage, long idVictim) {
		long result = conservtorshipRmlDao.prsnCharSelected(idStage, idVictim);
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method gets selcted personId data.
	 * 
	 * @param idCase
	 * @param idRemovalEvent
	 * @return PersonValueDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PersonValueDto getCnsrvtrRmvlPersonId(long idCase, long idRemovalEvent) {
		PersonValueDto personValueDto = new PersonValueDto();
		List<ConservtorshipRmlDto> conservtorshipRmlDtoList = conservtorshipRmlDao.getCnsrvtrRmvlPersonId(idCase,
				idRemovalEvent);
		if (conservtorshipRmlDtoList != null) {
			for (ConservtorshipRmlDto conservtorshipRmlDtos : conservtorshipRmlDtoList) {
				personValueDto.setPersonId(conservtorshipRmlDtos.getPersonId());
				if (conservtorshipRmlDtos.getDateOfDeath() != null) {
					personValueDto.setDateOfDeath(conservtorshipRmlDtos.getDateOfDeath());
				}
			}
		}
		return personValueDto;
	}
}
