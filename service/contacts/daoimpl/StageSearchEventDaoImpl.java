package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.IncomingNarrative;
import us.tx.state.dfps.common.domain.InrDuplicateGrouping;
import us.tx.state.dfps.service.admin.dto.StageEventDto;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.StageSearchEventDao;
import us.tx.state.dfps.service.utility.dao.BlobUtil;
import us.tx.state.dfps.xmlstructs.inputstructs.EventSearchStageInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventSearchStageOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageSearchEventDaoImpl Nov 1, 2017- 5:25:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageSearchEventDaoImpl implements StageSearchEventDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${StageSearchEventDaoImpl.getDateCaseOpenedForStage}")
	private String getDateCaseOpenedForStageSql;

	@Value("${StageSearchEventDaoImpl.getAssessmentListByStageEvent}")
	private String getAssessmentListByStageEventSql;

	/**
	 * Method Name: getDateCaseOpenedForStage Method Description:This dam
	 * retrieves the DT INT START given the ID CASE from the STAGE Table.
	 * 
	 * @param eventSearchStageInDto
	 * @return EventSearchStageOutDto
	 */
	@Override
	public EventSearchStageOutDto getDateCaseOpenedForStage(EventSearchStageInDto eventSearchStageInDto) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDateCaseOpenedForStageSql)
				.addScalar("dtCaseOpened", StandardBasicTypes.DATE)
				.setParameter("idStage", eventSearchStageInDto.getUlIdStage())
				.setResultTransformer(Transformers.aliasToBean(EventSearchStageOutDto.class));
		List<EventSearchStageOutDto> searchStageOutDtoList = new ArrayList<>();
		searchStageOutDtoList = (List<EventSearchStageOutDto>) sQLQuery1.list();
		EventSearchStageOutDto eventSearchStageOutDto = new EventSearchStageOutDto();

		if (0 < searchStageOutDtoList.size()) {
			eventSearchStageOutDto.setDtCaseOpened(searchStageOutDtoList.get(ServiceConstants.Zero).getDtCaseOpened());
		}
		return eventSearchStageOutDto;
	}

	/**
	 * Method Name: getAssesmentListByStageEvent Method Description:This dam
	 * retrieves Assessment list from stage, event and task table
	 * 
	 * @param stageEventDto
	 * @return assesmentListByStageEvent
	 */
	@Override
	public List<StageEventDto> getAssessmentListByStageEvent(StageEventDto stageEventDto) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAssessmentListByStageEventSql).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("txtTaskDecode", StandardBasicTypes.STRING).setParameter("idCase", stageEventDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(StageEventDto.class));
		List<StageEventDto> assesmentListByStageEvent = new ArrayList<>();
		assesmentListByStageEvent = (List<StageEventDto>) sQLQuery1.list();

		return assesmentListByStageEvent;
	}

	public List<ContactNarrativeDto> getContactDetailIntakeReports(List<Long> idStage) {
		List<ContactNarrativeDto> retVal = new LinkedList<>();
		List<IncomingNarrative> rawResults = null;
		if (!ObjectUtils.isEmpty(idStage)) {
			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(IncomingNarrative.class);
			criteria1.add(Restrictions.in("idStage", idStage));
			rawResults = criteria1.list();
		}

		if (!ObjectUtils.isEmpty(rawResults)) {
			for (IncomingNarrative currResult : rawResults) {
				String narrativeData = BlobUtil.unwrapBlobWithoutDecode(currResult.getNarrIncoming());
				Hashtable<String, String> narrativeHash = BlobUtil.readDOMXML(narrativeData.getBytes(), BlobUtil.CHARACTER_ENCODING);
				StringBuilder buffer = new StringBuilder();
				buffer.append(narrativeHash.get("txtGeneralInfo"));
				buffer.append(narrativeHash.get("txtConclusions"));
				ContactNarrativeDto tempResult = new ContactNarrativeDto();
				tempResult.setIdContactStage(currResult.getIdStage());
				tempResult.setStrNarrative(buffer.toString());
				retVal.add(tempResult);
			}

		}

		return retVal;
	}

	@Override
	public ContactNarrativeDto getIntakeReportAlternatives(Long groupNum) {
		ContactNarrativeDto retVal = new ContactNarrativeDto();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrDuplicateGrouping.class);
		criteria.add(Restrictions.eq("idInrDuplicateGrouping", groupNum));
		InrDuplicateGrouping dbEntity = (InrDuplicateGrouping)criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(dbEntity)) {
			retVal.setStrNarrative(dbEntity.getTxtNarrativeRpt());
			retVal.setCdContactOthers(dbEntity.getCdInrProviderRegType());
		}
		return retVal;
	}
}
