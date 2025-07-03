package us.tx.state.dfps.service.approval.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyReq;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyRes;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusResourceHistyDao;

@Repository
public class ApprovalStatusResourceHistyDaoImpl implements ApprovalStatusResourceHistyDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalStatusResourceHistyImpl.fetchResourceHisty}")
	private String fetchResourceHistysql;

	public static final String V1 = "SP";
	public static final String V2 = "PV";

	public static final Logger log = Logger.getLogger(ApprovalStatusResourceHistyDaoImpl.class);

	/**
	 * Method Name: fetchResourceHisty Method Description: This dao call will
	 * retrieve a row from the RESOURCE HISTORY table where the effective Date
	 * is the most recent date and FA home status is <> 'PV' or 'SP' Dam Name:
	 * CSEC38D, Service Name:CCMN35S
	 * 
	 * @param ApprovalStatusResourceHistyReq
	 * @return List of ApprovalStatusResourceHistyRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalStatusResourceHistyRes> fetchResourceHisty(
			ApprovalStatusResourceHistyReq approvalStatusResourceHistyReq) {
		log.debug("Entering method fetchResourceHisty in ApprovalStatusResourceHistyImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchResourceHistysql)
				.addScalar("idResourceHistory", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("lastUpdate", StandardBasicTypes.STRING)
				.addScalar("dtRshsEffective", StandardBasicTypes.DOUBLE)
				.addScalar("dtRshsClose", StandardBasicTypes.DATE).addScalar("dtRshsCert", StandardBasicTypes.DATE)
				.addScalar("dtRshsMarriage", StandardBasicTypes.DATE).addScalar("dtRshsEnd", StandardBasicTypes.DATE)
				.addScalar("addrRshsStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRshsStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRshsCity", StandardBasicTypes.STRING)
				.addScalar("cdRshsState", StandardBasicTypes.STRING).addScalar("addrRshsZip", StandardBasicTypes.STRING)
				.addScalar("addrRshsAttn", StandardBasicTypes.STRING).addScalar("cdRshsCnty", StandardBasicTypes.STRING)
				.addScalar("cdRshsEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRshsFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cRshsFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRshsFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRshsInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRshsLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRshsMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRshsMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRshsOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRshsOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRshsPayment", StandardBasicTypes.STRING)
				.addScalar("cdRshsRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRshsRegion", StandardBasicTypes.STRING)
				.addScalar("cdRshsReligion", StandardBasicTypes.STRING)
				.addScalar("cdRshsRespite", StandardBasicTypes.STRING)
				.addScalar("cdRshsSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRshsSetting", StandardBasicTypes.STRING)
				.addScalar("cdRshsSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRshsStatus", StandardBasicTypes.STRING).addScalar("cdRshsType", StandardBasicTypes.STRING)
				.addScalar("cdRshsCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRshsCategory", StandardBasicTypes.STRING)
				.addScalar("cdRshsCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRshsClosureRsn", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("indRshsCareProv", StandardBasicTypes.STRING)
				.addScalar("indRshsEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRshsInactive", StandardBasicTypes.STRING)
				.addScalar("indRshsIndivStudy", StandardBasicTypes.STRING)
				.addScalar("indRshsNonPrs", StandardBasicTypes.STRING)
				.addScalar("indRshsTransport", StandardBasicTypes.STRING)
				.addScalar("nmRshsContact", StandardBasicTypes.STRING)
				.addScalar("nmRshsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmRshsResource", StandardBasicTypes.STRING)
				.addScalar("nbrRshsCampusNbr", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRshsFacilCapacity", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsFMAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsFMAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsIntChildren", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsIntFeAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsIntFeAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsIntMaAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsIntMaAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsMaAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsMaAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRshsPhn", StandardBasicTypes.STRING)
				.addScalar("nbrRshsPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrRshsVid", StandardBasicTypes.STRING)
				.addScalar("nbrRshsAnnualIncome", StandardBasicTypes.DOUBLE)
				.addScalar("txtRshsAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRshsComments", StandardBasicTypes.STRING)
				.setLong("idRsrcFaHomeEvent", approvalStatusResourceHistyReq.getIdRsrcFaHomeEvent())
				.setString("hI_V1", V1).setString("hI_V2", V2)
				.setResultTransformer(Transformers.aliasToBean(ApprovalStatusResourceHistyRes.class)));

		return (List<ApprovalStatusResourceHistyRes>) sQLQuery1.list();
	}
}
