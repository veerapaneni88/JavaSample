package us.tx.state.dfps.service.SDM.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.service.SDM.SDMHouseHoldDto;
import us.tx.state.dfps.service.SDM.dao.SDMDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;

@Repository
public class SDMDaoImpl implements SDMDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SDMDaoImpl.getSDMComplHouseHold}")
	private transient String getSDMComplHouseHoldSql;

	@Value("${SDMDaoImpl.getSDMComplHouseHoldForINVClosedStages}")
	private transient String getSDMComplHouseHoldForINVClosedStages;

	@Value("${SDMDaoImpl.getSDMComplHouseHoldForARClosedStages}")
	private transient String getSDMComplHouseHoldForARClosedStages;

	@Value("${SDMDaoImpl.getSDMComplHouseHoldWithIdStage}")
	private transient String getSDMComplHouseHoldWithIdStage;	

	@Value("${SDMDaoImpl.getSDMHouseHold}")
	private transient String getSDMHouseHoldSql;

	private static final Logger log = Logger.getLogger(SDMDaoImpl.class);

	public SDMDaoImpl() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SDMHouseHoldDto> getSDMComplHouseHold(CommonCaseIdReq caseIdReq,Boolean isStageClosed) {
		log.info("TransactionId :" + caseIdReq.getTransactionId());
		List<SDMHouseHoldDto> houseHoldList = new ArrayList<>();

		List<String> cdTaskList = new ArrayList<>();
		cdTaskList.add(ServiceConstants.CD_TASK_SA);
		cdTaskList.add(ServiceConstants.CD_TASK_SA_AR);
		//Defect 15437- Added idStage to filter out the non-current stages while displaying inv conclusion.
		//Defect 16430 - Added idStage logic to filter House Holds by safety assessments completed in stage assosicated with Risk Assessment event. used by risk assessment page.
	//[artf171225] changed logic for INV and A-R stages to determine correct household used at the time of closing the stage
		if(caseIdReq.getStageCD().equalsIgnoreCase(WebConstants.STAGE_CODE_AR)){
			houseHoldList = getSdmHouseHoldDtosForAR(caseIdReq, isStageClosed, cdTaskList);
		}else{
			houseHoldList = getSdmHouseHoldDtosForINV(caseIdReq, isStageClosed, cdTaskList);
		}
		return houseHoldList;
	}

	private List<SDMHouseHoldDto> getSdmHouseHoldDtosForINV(CommonCaseIdReq caseIdReq, Boolean isStageClosed, List<String> cdTaskList) {
		List<SDMHouseHoldDto> houseHoldList;
		String qry;
		qry =getSDMComplHouseHoldSql;
		//[artf171225] Added new Query by Joining  table to determine the correct household when the stage is closed.
		//[artf171225] Added new Query by Joining CPS_INVST_DETAIL table to determine the correct household for INV stages when the stage is closed.
		if(isStageClosed){
			qry = getSDMComplHouseHoldForINVClosedStages ;
		}

		houseHoldList = (List<SDMHouseHoldDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(qry).setParameter("uidCase", caseIdReq.getUlIdCase())
				.setParameter("eventStatus", ServiceConstants.EVENTSTATUS_PROCESS)
				.setParameterList("cdTaskSDMSafety", cdTaskList)).addScalar("idAsmntCpsSa", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSafetyDecision").addScalar("nmPerson").addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SDMHouseHoldDto.class)).list();
		return houseHoldList;
	}

	private List<SDMHouseHoldDto> getSdmHouseHoldDtosForAR(CommonCaseIdReq caseIdReq, Boolean isStageClosed, List<String> cdTaskList) {
		String qry;
		List<SDMHouseHoldDto> houseHoldList;
		qry =getSDMComplHouseHoldWithIdStage;

		//[artf171225] Added new Query by Joining CPS_AR_CNCLSN_DETAIL table to determine the correct household for A-R stages when the stage is closed.
		if(isStageClosed){
			qry = getSDMComplHouseHoldForARClosedStages;
		}

		houseHoldList = (List<SDMHouseHoldDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(qry).setParameter("uidCase", caseIdReq.getUlIdCase())
				.setParameter("idStage", caseIdReq.getStageId())
				.setParameter("eventStatus", ServiceConstants.EVENTSTATUS_PROCESS)
				.setParameterList("cdTaskSDMSafety", cdTaskList)).addScalar("idAsmntCpsSa", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSafetyDecision").addScalar("nmPerson").addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SDMHouseHoldDto.class)).list();


		return houseHoldList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SDMHouseHoldDto> getSDMHouseHold(CommonCaseIdReq caseIdReq) {
		log.info("TransactionId :" + caseIdReq.getTransactionId());
		List<SDMHouseHoldDto> houseHoldList = new ArrayList<>();

		List<String> cdTaskList = new ArrayList<>();
		cdTaskList.add(ServiceConstants.CD_TASK_SA);
		cdTaskList.add(ServiceConstants.CD_TASK_SA_AR);
		cdTaskList.add(ServiceConstants.CD_TASK_SA_FSU);
		cdTaskList.add(ServiceConstants.CD_TASK_SA_FRE);
		cdTaskList.add(ServiceConstants.CD_TASK_SA_FPR);

		//ALMID : 9711 : get SDM households from current stage 
		houseHoldList = (List<SDMHouseHoldDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSDMHouseHoldSql).setParameter("stageId", caseIdReq.getStageId())
				.setParameterList("cdTaskSDMSafety", cdTaskList)).addScalar("idAsmntCpsSa", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdSafetyDecision")
						.addScalar("cdEventStatus").addScalar("nmPerson")
						.setResultTransformer(Transformers.aliasToBean(SDMHouseHoldDto.class)).list();

		return houseHoldList;
	}

}
