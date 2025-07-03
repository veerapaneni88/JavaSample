package us.tx.state.dfps.service.approval.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscRes;
import us.tx.state.dfps.service.approval.dao.GetCapsResourcesDao;

@Repository
public class GetCapsResourcesDaoImpl implements GetCapsResourcesDao {

	@Value("${GetCapsResourcesDaoImpl.getCapsRscsql}")
	private String getCapsRscsql;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("GetCapsResourcesDaoImpl.Class");

	/**
	 * Method Name: getCapsRsc Method Description: This method will return the
	 * FA Home Status for a given Event Id. DAM NAME: CSES43D Service Name:
	 * CCMN35S
	 *
	 * @param SaveApprovalStatusGetCapsRscReq
	 * @return List<SaveApprovalStatusGetCapsRscRes>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SaveApprovalStatusGetCapsRscRes> getCapsRsc(
			SaveApprovalStatusGetCapsRscReq saveApprovalStatusGetCapsRscReq) {
		log.debug("Entering method getCapsRsc in GetCapsResourcesDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCapsRscsql)
				.addScalar("idResource").addScalar("lastUpdate").addScalar("addrRsrcStLn1").addScalar("addrRsrcStLn2")
				.addScalar("addrRsrcCity").addScalar("cdRsrcState").addScalar("addrRsrcZip").addScalar("addrRsrcAttn")
				.addScalar("cdRsrcCnty").addScalar("cdRsrcInvolClosure").addScalar("cdRsrcClosureRsn")
				.addScalar("cdRsrcCampusType").addScalar("cdRsrcCategory").addScalar("cdRsrcCertBy")
				.addScalar("cdRsrcEthnicity").addScalar("cdRsrcFaHomeStatus").addScalar("cdRsrcFaHomeType1")
				.addScalar("cCdRsrcFaHomeType2").addScalar("cdRsrcFaHomeType3").addScalar("cdRsrcFaHomeType4")
				.addScalar("cdRsrcFaHomeType5").addScalar("cdRsrcFaHomeType6").addScalar("cdRsrcFaHomeType7")
				.addScalar("cdRsrcFacilType").addScalar("cdRsrcLanguage").addScalar("cdRsrcMaintainer")
				.addScalar("cdRsrcMaritalStatus").addScalar("cdRsrcOperBy").addScalar("cdRsrcOwnership")
				.addScalar("cdRsrcPayment").addScalar("cdRsrcRecmndReopen").addScalar("cdRsrcRegion")
				.addScalar("cdRsrcReligion").addScalar("cdRsrcRespite").addScalar("cdRsrcSchDist")
				.addScalar("cdRsrcSetting").addScalar("cdRsrcSourceInquiry").addScalar("cdRsrcStatus")
				.addScalar("cdRsrcType").addScalar("dtRsrcMarriage").addScalar("dtRsrcCert").addScalar("dtRsrcClose")
				.addScalar("idRsrcFaHomeEvent").addScalar("idRsrcFaHomeStage").addScalar("indRsrcCareProv")
				.addScalar("indRsrcEmergPlace").addScalar("indRsrcInactive").addScalar("indRsrcIndivStudy")
				.addScalar("indRsrcNonPrs").addScalar("indRsrcTransport").addScalar("indRsrcWriteHist")
				.addScalar("nmRsrcLastUpdate").addScalar("nmResource").addScalar("nmRsrcContact")
				.addScalar("nbrRsrcAnnualIncome").addScalar("nbrSchCampusNbr").addScalar("nbrRsrcFacilAcclaim")
				.addScalar("nbrRsrcFacilCapacity").addScalar("nbrRsrcFMAgeMax").addScalar("nbrRsrcFMAgeMin")
				.addScalar("nbrRsrcMlAgeMax").addScalar("nbrRsrcMlAgeMin").addScalar("nbrRsrcIntChildren")
				.addScalar("nbrRsrcIntFeAgeMax").addScalar("nbrRsrcIntFeAgeMin").addScalar("nbrRsrcIntMaAgeMax")
				.addScalar("nbrRsrcIntMaAgeMin").addScalar("nbrRsrcOpenSlots").addScalar("nbrRsrcPhn")
				.addScalar("nbrFacilPhoneExtension").addScalar("nbrRsrcVid").addScalar("txtRsrcAddrCmnts")
				.addScalar("txtRsrcComments")
				.setLong("idRsrcFaHomeEvent", saveApprovalStatusGetCapsRscReq.getIdRsrcFaHomeEvent())
				.setResultTransformer(Transformers.aliasToBean(SaveApprovalStatusGetCapsRscRes.class)));

		List<SaveApprovalStatusGetCapsRscRes> saveApprovalStatusGetCapsRscRes = new ArrayList<>();
		saveApprovalStatusGetCapsRscRes = (List<SaveApprovalStatusGetCapsRscRes>) sQLQuery1.list();

		log.debug("Exiting method getCapsRsc in GetCapsResourcesDaoImpl");
		return saveApprovalStatusGetCapsRscRes;
	}
}
