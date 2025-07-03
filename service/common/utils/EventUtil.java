package us.tx.state.dfps.service.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dto.AdminEventInputDto;
import us.tx.state.dfps.service.admin.service.AdminEventService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.workload.dto.EventDto;

@Repository
public class EventUtil {

	private static final int _80 = 80;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private AdminEventService ccmn01uService;

	@Autowired
	EventDao eventDao;

	@Value("${EventUtil.attachEventToDo}")
	private String attachEventToDoSql;

	@Value("${EventUtilImpl.updtLstUpdateTodo}")
	private String updtLstUpdateTodo;

	public static final String NEW_EVENT = "NEW";
	public static final String APPROVED_EVENT = "APRV";
	public static final String COMPLETE_EVENT = "COMP";
	public static final String PENDING_EVENT = "PEND";
	public static final String PROCESS_EVENT = "PROC";

	private static final Logger log = Logger.getLogger(EventUtil.class);

	public void attachEventToTodo(Long idEvent, Long idStage, String cdTask) {

		int result = sessionFactory.getCurrentSession().createSQLQuery(attachEventToDoSql)
				.setParameter("idToDoEvent", idEvent).setParameter("cdToDoTask", cdTask)
				.setParameter("idTodoStage", idStage).executeUpdate();
		if (result >= 1) {
			log.info("attachEventToDo updated successfully");
		}

	}

	public void changeEventStatus(long idEvent, String eventStatusWas, String eventStatusWillBe) {

		HashSet<String> hashSet = new HashSet<>();
		hashSet.add(eventStatusWas);
		FceUtil.verifyNonZero("idEvent", idEvent);
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		String description = getDescription(event.getCdTask(), eventStatusWillBe);
		event.setTxtEventDescr(description);
		changeEventStatus(event, hashSet, eventStatusWillBe);

	}

	public void changeEventStatus(long idEvent, Set<String> eventStatusWas, String eventStatusWillBe,
			String description) {

		FceUtil.verifyNonZero("idEvent", idEvent);
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		if (description == null) {
			event.setTxtEventDescr(getDescription(event.getCdTask(), eventStatusWillBe));
		}
		changeEventStatus(event, eventStatusWas, eventStatusWillBe);
	}

	private void changeEventStatus(Event eventValBean, Set<String> eventStatusWas, String eventStatusWillBe) {

		String currentEventStatus = eventValBean.getCdEventStatus();
		if (eventStatusWas.contains(currentEventStatus) == false) {
			return;
		}
		AdminEventInputDto ccmn01ui = new AdminEventInputDto();

		ccmn01ui.setReqFunctionCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		ccmn01ui.setEventLastUpdate(eventValBean.getDtLastUpdate());
		ccmn01ui.setIdEvent(eventValBean.getIdEvent());
		ccmn01ui.setIdPerson(eventValBean.getPerson().getIdPerson());
		ccmn01ui.setCdTask(eventValBean.getCdTask());
		ccmn01ui.setCdEventType(eventValBean.getCdEventType());

		ccmn01ui.setTxtEventDescr(eventValBean.getTxtEventDescr());
		ccmn01ui.setDtDtEventOccurred(new java.util.Date());
		ccmn01ui.setEventLastUpdate(eventValBean.getDtLastUpdate());
		ccmn01ui.setIdStage(eventValBean.getStage().getIdStage());

		ccmn01ui.setCdEventStatus(eventStatusWillBe);
		ccmn01uService.postEvent(ccmn01ui);

	}

	public static String getDescription(String taskCode, String eventStatus) {
		HashMap<String, String> hashMap = new HashMap<>();

		if (ServiceConstants.FCE_APPLICATION_TASK_CODE.equals(taskCode)) {

			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_APP);
			hashMap.put(ServiceConstants.PROCESS_EVENT, ServiceConstants.PROCESS_EVENT_APP);
			hashMap.put(ServiceConstants.PENDING_EVENT, ServiceConstants.PENDING_EVENT_APP);
			hashMap.put(ServiceConstants.COMPLETE_EVENT, ServiceConstants.COMPLETE_EVENT_APP);
			hashMap.put(ServiceConstants.APPROVED_EVENT, ServiceConstants.APPROVED_EVENT_APP);

		}

		if (ServiceConstants.FCE_ELIGIBILITY_TASK_CODE.equals(taskCode)) {
			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_ELI);
		}

		if (ServiceConstants.FCE_REVIEW_TASK_CODE.equals(taskCode)) {

			hashMap.put(ServiceConstants.NEW_EVENT, ServiceConstants.NEW_EVENT_FC);
			hashMap.put(ServiceConstants.PROCESS_EVENT, ServiceConstants.PROCESS_EVENT_FC);
			hashMap.put(ServiceConstants.PENDING_EVENT, ServiceConstants.PENDING_EVENT_FC);
			hashMap.put(ServiceConstants.COMPLETE_EVENT, ServiceConstants.COMPLETE_EVENT_FC);

		}

		String description = hashMap.get(eventStatus);
		if (description == null) {
			throw new IllegalStateException(
					"Unexpected eventStatus '" + eventStatus + "' " + "for taskCode '" + taskCode + "'");
		}
		final int eventDescriptionColumnLimit = _80;
		if (description.length() > eventDescriptionColumnLimit) {
			description = description.substring(0, eventDescriptionColumnLimit);
		}
		return description;
	}

	public void completeTodosForEventId(long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updtLstUpdateTodo);
		query.setLong("hi_ulIdEvent", idEvent);
		query.executeUpdate();

	}

	public void deleteEventById(Long idEvent) {

		eventDao.deleteEventById(idEvent);
	}

	public EventDto getEventById(Long idEvent) {

		EventDto eventDto;

		eventDto = eventDao.getEventByid(idEvent);
		return eventDto;
	}
	
	/**
	 * 
	 *Method Name:	getCpNarrTables
	 *Method Description:get CP dependent NARRATIVES domain names
	 *@return
	 */
	public void getCpNarrTables(Map<String, String> cpNarrTables){
		
		cpNarrTables.put("CP_ISH_NARR","us.tx.state.dfps.common.domain.CpIshNarr");
		cpNarrTables.put("CP_SSC_NARR","us.tx.state.dfps.common.domain.CpSscNarr");
		cpNarrTables.put("CP_SEN_NARR","us.tx.state.dfps.common.domain.CpSenNarr");
		cpNarrTables.put("CP_TRM_NARR","us.tx.state.dfps.common.domain.CpTrmNarr");
		cpNarrTables.put("CP_DVL_NARR","us.tx.state.dfps.common.domain.CpDvlNarr");
		cpNarrTables.put("CP_PLS_NARR","us.tx.state.dfps.common.domain.CpPlsNarr");
		cpNarrTables.put("CP_SAE_NARR","us.tx.state.dfps.common.domain.CpSaeNarr");
		cpNarrTables.put("CP_DVP_NARR","us.tx.state.dfps.common.domain.CpDvpNarr");
		cpNarrTables.put("CP_PHP_NARR","us.tx.state.dfps.common.domain.CpPhpNarr");
		cpNarrTables.put("CP_TPL_NARR","us.tx.state.dfps.common.domain.CpTplNarr");
		cpNarrTables.put("CP_AOP_NARR","us.tx.state.dfps.common.domain.CpAopNarr");
		cpNarrTables.put("CP_PCH_NARR","us.tx.state.dfps.common.domain.CpPchNarr");
		cpNarrTables.put("CP_CHG_NARR","us.tx.state.dfps.common.domain.CpChgNarr");
		cpNarrTables.put("CP_CONCURRENT_GOAL","us.tx.state.dfps.common.domain.CpConcurrentGoal");
		cpNarrTables.put("CP_ASF_NARR","us.tx.state.dfps.common.domain.CpAsfNarr");
		cpNarrTables.put("CP_PVP_NARR","us.tx.state.dfps.common.domain.CpPvpNarr");
		cpNarrTables.put("CP_ICH_NARR","us.tx.state.dfps.common.domain.CpIchNarr");
		cpNarrTables.put("CP_EDP_NARR","us.tx.state.dfps.common.domain.CpEdpNarr");
		cpNarrTables.put("CP_MDP_NARR","us.tx.state.dfps.common.domain.CpMdpNarr");
		cpNarrTables.put("CP_WOR_NARR","us.tx.state.dfps.common.domain.CpWorNarr");
		cpNarrTables.put("CP_DVN_NARR","us.tx.state.dfps.common.domain.CpDvnNarr");
		cpNarrTables.put("CP_PSP_NARR","us.tx.state.dfps.common.domain.CpPspNarr");
		cpNarrTables.put("CP_PSY_NARR","us.tx.state.dfps.common.domain.CpPsyNarr");
		cpNarrTables.put("CP_CPL_NARR","us.tx.state.dfps.common.domain.CpCplNarr");
		cpNarrTables.put("CP_PRA_NARR","us.tx.state.dfps.common.domain.CpPraNarr");
		cpNarrTables.put("CP_EDN_NARR","us.tx.state.dfps.common.domain.CpEdnNarr");
		cpNarrTables.put("CP_REP_NARR","us.tx.state.dfps.common.domain.CpRepNarr");
		cpNarrTables.put("CP_EOC_NARR","us.tx.state.dfps.common.domain.CpEocNarr");
		cpNarrTables.put("CP_APP_NARR","us.tx.state.dfps.common.domain.CpAppNarr");
		cpNarrTables.put("CP_PLP_NARR","us.tx.state.dfps.common.domain.CpPlpNarr");
		cpNarrTables.put("CP_IBP_NARR","us.tx.state.dfps.common.domain.CpIbpNarr");
		cpNarrTables.put("CP_PAL_NARR","us.tx.state.dfps.common.domain.CpPalNarr");
		cpNarrTables.put("CP_SSF_NARR","us.tx.state.dfps.common.domain.CpSsfNarr");
		cpNarrTables.put("CP_TRV_NARR","us.tx.state.dfps.common.domain.CpTrvNarr");
		cpNarrTables.put("CP_DSC_NARR","us.tx.state.dfps.common.domain.CpDscNarr");
		cpNarrTables.put("CP_REC_NARR","us.tx.state.dfps.common.domain.CpRecNarr");
		cpNarrTables.put("CP_FMP_NARR","us.tx.state.dfps.common.domain.CpFmpNarr");
		cpNarrTables.put("CP_PER_NARR","us.tx.state.dfps.common.domain.CpPerNarr");
		cpNarrTables.put("CP_CNP_NARR","us.tx.state.dfps.common.domain.CpCnpNarr");
		cpNarrTables.put("CP_OOP_NARR","us.tx.state.dfps.common.domain.CpOopNarr");
		cpNarrTables.put("CP_VIS_NARR","us.tx.state.dfps.common.domain.CpVisNarr");
		cpNarrTables.put("CP_PFC_NARR","us.tx.state.dfps.common.domain.CpPfcNarr");
		cpNarrTables.put("CP_APA_NARR","us.tx.state.dfps.common.domain.CpApaNarr");
		cpNarrTables.put("CP_PDO_NARR","us.tx.state.dfps.common.domain.CpPdoNarr");
		cpNarrTables.put("CP_SUP_NARR","us.tx.state.dfps.common.domain.CpSupNarr");
		cpNarrTables.put("CP_FAN_NARR","us.tx.state.dfps.common.domain.CpFanNarr");
		cpNarrTables.put("CP_APR_NARR","us.tx.state.dfps.common.domain.CpAprNarr");
		cpNarrTables.put("CP_CTP_NARR","us.tx.state.dfps.common.domain.CpCtpNarr");
		cpNarrTables.put("CP_IGH_NARR","us.tx.state.dfps.common.domain.CpIghNarr");
		cpNarrTables.put("CP_SEP_NARR","us.tx.state.dfps.common.domain.CpSepNarr");
		cpNarrTables.put("CP_PHY_NARR","us.tx.state.dfps.common.domain.CpPhyNarr");
		cpNarrTables.put("CP_MDN_NARR","us.tx.state.dfps.common.domain.CpMdnNarr");
		cpNarrTables.put("CP_FAM_NARR","us.tx.state.dfps.common.domain.CpFamNarr");
		cpNarrTables.put("CP_DPL_NARR","us.tx.state.dfps.common.domain.CpDplNarr");
		cpNarrTables.put("CP_HRI_NARR","us.tx.state.dfps.common.domain.CpHriNarr");
		cpNarrTables.put("CP_HRB_NARR","us.tx.state.dfps.common.domain.CpHrbNarr");
		cpNarrTables.put("CP_TOP_NARR","us.tx.state.dfps.common.domain.CpTopNarr");			
		cpNarrTables.put("CP_RIP_NARR","us.tx.state.dfps.common.domain.CpRipNarr");			
		cpNarrTables.put("CP_DIP_NARR","us.tx.state.dfps.common.domain.CpDipNarr");		
		cpNarrTables.put("CP_SAP_NARR","us.tx.state.dfps.common.domain.CpSapNarr");		
		cpNarrTables.put("CP_REI_NARR","us.tx.state.dfps.common.domain.CpReiNarr");		
		cpNarrTables.put("CP_TSM_NARR","us.tx.state.dfps.common.domain.CpTsmNarr");		
		cpNarrTables.put("CP_FNP_NARR","us.tx.state.dfps.common.domain.CpFnpNarr");		
		cpNarrTables.put("CP_TMP_NARR","us.tx.state.dfps.common.domain.CpTmpNarr");		
		cpNarrTables.put("CP_PAP_NARR","us.tx.state.dfps.common.domain.CpPapNarr");		
		cpNarrTables.put("CP_DAP_NARR","us.tx.state.dfps.common.domain.CpDapNarr");		
		cpNarrTables.put("CP_TSW_NARR","us.tx.state.dfps.common.domain.CpTswNarr");
		
	}

}
