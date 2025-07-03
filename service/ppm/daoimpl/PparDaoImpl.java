package us.tx.state.dfps.service.ppm.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.ppm.dao.PparDao;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:dao methods
 * for Permanency planning May 29, 2018- 4:29:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PparDaoImpl implements PparDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ppmdao.getpptInfo}")
	private String getpptInfoSql;

	@Value("${ppmdao.getEventPerson}")
	private String getEventPersonSql;

	@Value("${ppmdao.getEventPpt}")
	private String getEventPptSql;

	@Value("${ppmdao.getPlcmt}")
	private String getPlcmtSql;

	@Value("${ppmdao.getUnitInfo}")
	private String getUnitInfoSql;

	/**
	 * Method Name: getPPTParticipant CLSS05D Method Description: This DAM
	 * selects a full row with id_event as the input.
	 * 
	 * @param idEvent
	 * @return PRTParticipantDto
	 */
	@Override
	public List<PRTParticipantDto> getPPTParticipant(Long idEvent) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getpptInfoSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPart", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdNotifType", StandardBasicTypes.STRING).addScalar("cdPartType", StandardBasicTypes.STRING)
				.addScalar("dtPart", StandardBasicTypes.DATE).addScalar("dtPartDate", StandardBasicTypes.DATE)
				.addScalar("nmPartFull", StandardBasicTypes.STRING).addScalar("sdsPartRel", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(PRTParticipantDto.class));
		List<PRTParticipantDto> list = (List<PRTParticipantDto>) query.list();
		return list;
	}

	/**
	 * Method Name: getEventPerson Method Description: From DAM for csec22d This
	 * DAM joins the Event, Event Person Link, and Service Plan to retrieve the
	 * most recent Service plan for a given id stage and id person.
	 * 
	 * @param idPerson
	 * @return String
	 */
	@Override
	public String getEventPerson(Long idPerson) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getEventPersonSql).setParameter("idPerson",
				idPerson);
		return ObjectUtils.isEmpty(query.list()) ? null : (String) query.list().get(0);
	}

	/**
	 * Method Name: getEventPerson Method Description: From DAM for CSEC31D This
	 * dam will retrieve a PPT record previous to a given PPT record.
	 * 
	 * @param idStage
	 * @return PptDetailsOutDto
	 */
	@Override
	public PptDetailsOutDto getEventPpt(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getEventPptSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPptEvent", StandardBasicTypes.LONG)
				.addScalar("addrPptCity", StandardBasicTypes.STRING).addScalar("addrPptCnty", StandardBasicTypes.STRING)
				.addScalar("addrPptStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPptStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPptState", StandardBasicTypes.STRING).addScalar("addrPptZip", StandardBasicTypes.STRING)
				.addScalar("dtPptDate", StandardBasicTypes.DATE).addScalar("dtPptDocComp", StandardBasicTypes.DATE)
				.addScalar("nbrPptPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPptPhoneExt", StandardBasicTypes.STRING)
				.addScalar("pptAddrCmnt", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idEventStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccured", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PptDetailsOutDto.class));
		List<PptDetailsOutDto> list = (List<PptDetailsOutDto>) query.list();
		PptDetailsOutDto pptDetailsOutDto=new PptDetailsOutDto();
		if(list.size()>=2)
		{
			pptDetailsOutDto=list.get(list.size()-2);	
		}
		return ObjectUtils.isEmpty(list) ? null : pptDetailsOutDto;
	}

	/**
	 * Method Name: getUnitInfo Method Description: From DAM for CSEC19D This
	 * DAM joins the stage table and unit table given the ID STAGE.
	 * 
	 * @param idStage
	 * @return UnitDto
	 */
	@Override
	public UnitDto getUnitInfo(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getUnitInfoSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("dtStageClose", StandardBasicTypes.DATE)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.DATE).addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("txtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("nbrUnit", StandardBasicTypes.STRING).addScalar("cdUnitRegion", StandardBasicTypes.STRING)
				.addScalar("cdUnitProgram", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idUnitParent", StandardBasicTypes.LONG)
				.addScalar("cdUnitSpecialization", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(UnitDto.class));
		List<UnitDto> list = (List<UnitDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getPlcmt Method Description: From DAM for CSEC32D This DAM
	 * will retreive an Account Placement from the PLACEMENT table where ID
	 * PERSON = the host and Dt Plcmt Strt <= input date and input date =< Max
	 * and IND PLCMT ACT PLANNED = true
	 * 
	 * @param idPlcmtChild,dtLastUpdate
	 * @return PlacementAUDDto
	 */
	@Override
	public PlacementAUDDto getPlcmt(Long idPlcmtChildId, Date dtLastUpdateDate) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlcmtSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).addScalar("idPlcmtChild", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("indPlcmtContCntct", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEducLog", StandardBasicTypes.STRING)
				.addScalar("indPlcmetEmerg", StandardBasicTypes.STRING)
				.addScalar("indPlcmtNotApplic", StandardBasicTypes.STRING)
				.addScalar("indPlcmtSchoolDoc", StandardBasicTypes.STRING)
				.addScalar("indPlcmtWriteHistory", StandardBasicTypes.STRING)
				.addScalar("plcmtPhoneExt", StandardBasicTypes.STRING)
				.addScalar("plcmtTelephone", StandardBasicTypes.STRING)
				.addScalar("plcmtAgency", StandardBasicTypes.STRING)
				.addScalar("plcmtContact", StandardBasicTypes.STRING).addScalar("plcmtFacil", StandardBasicTypes.STRING)
				.addScalar("plcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.setParameter("idPlcmtChildId", idPlcmtChildId).setParameter("dtLastUpdateDate", dtLastUpdateDate)
				.setResultTransformer(Transformers.aliasToBean(PlacementAUDDto.class));
		List<PlacementAUDDto> list = (List<PlacementAUDDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

}
