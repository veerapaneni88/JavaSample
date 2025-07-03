package us.tx.state.dfps.service.cshattachmenta.daoimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.criterion.Order;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildSxVctmztn;
import us.tx.state.dfps.common.domain.ChildSxVctmztnIncdnt;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.cshattachmenta.dao.AttachmentADao;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;


/** service-business-FCL Class 
 * Name: AttachmentADaoImpl
 * Description:This is the Sexual Victimization history Page.
 * © 2019 Texas Department of Family and Protective Services
 * Artifact ID: artf128756
 * **/
@Repository
public class AttachmentADaoImpl implements AttachmentADao {

	@Autowired
	private SessionFactory sessionFactory;

	

	/**
	 * Name: fetchSexualVictimHistory
	 * Description: This is the Dao implementation to get the Child Sexual aggression,Sexual Victimization history and 
	 * Trafficking details for Attachment A form 
	 * @param idPerson
	 * @return sexualVictimHistoryDto
	 *  © 2019 Texas Department of Family and Protective Services
	 * Artifact ID: artf128756
	 */
	@Override
	public SexualVictimHistoryDto fetchSexualVictimHistory(Long idPerson) {
		SexualVictimHistoryDto sexualVictimHistoryDto  = new SexualVictimHistoryDto();
		List<SexualVictimIncidentDto> cshIncidentList = new ArrayList<SexualVictimIncidentDto>();
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztnIncdnt.class);
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		criteria.addOrder(Order.desc("dtIncident"));
		
		@SuppressWarnings("unchecked")
		List<ChildSxVctmztnIncdnt> incidentList = criteria.list();
		if (CollectionUtils.isNotEmpty(incidentList)) {
			
			incidentList.forEach(incident -> {
				SexualVictimIncidentDto cshIncidentDto = new SexualVictimIncidentDto();
				BeanUtils.copyProperties(incident, cshIncidentDto);
				
				cshIncidentDto.setDtIncident(incident.getDtIncident());
				cshIncidentDto.setResponsibleComments(incident.getTxtResponsibleComments());
				cshIncidentDto.setVictimComments(incident.getTxtVictimizationComments());
				cshIncidentDto.setIdIncident(incident.getIdChildSxVctmztnIncdnt());	
				cshIncidentList.add(cshIncidentDto);				
			});		
			sexualVictimHistoryDto.setSvhIncidents(cshIncidentList);
		
		}
		
		//to fetch Supervision Contact description		
		Criteria criteriaS = sessionFactory.getCurrentSession().createCriteria(ChildSxVctmztn.class);
		criteriaS.add(Restrictions.eq("person.idPerson", idPerson));
		ChildSxVctmztn childSxVctmztn = (ChildSxVctmztn)criteriaS.uniqueResult();
		if(!ObjectUtils.isEmpty(childSxVctmztn)){
			sexualVictimHistoryDto.setSupervisionContactDesc(childSxVctmztn.getTxtSupervisionContactDesc());
			sexualVictimHistoryDto.setIndChildHasSxVictimHistory(childSxVctmztn.getIndChildSxVctmztnHist());
            sexualVictimHistoryDto.setIndUnconfirmedVictimHistory(childSxVctmztn.getIndUnconfirmedVicHist());
            sexualVictimHistoryDto.setIndSexualBehaviorProblem(childSxVctmztn.getIndSxBehaviorProb());
            sexualVictimHistoryDto.setTxtBehaviorProblems(childSxVctmztn.getTxtSxBehaviorProb());
			sexualVictimHistoryDto.setIdUpdPrsUnconfVicHistUser(childSxVctmztn.getIdUpdPrsUnconfVicHistUser());
		}
			
		return sexualVictimHistoryDto;
	}


}