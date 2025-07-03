package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.person.dao.RetrieveCapsResourceDao;
import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageInDto;
import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:RetrieveCapsResourceDaoImpl May 8, 2018- 6:39:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class RetrieveCapsResourceDaoImpl implements RetrieveCapsResourceDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RetrieveCapsResourceDaoImpl.retrieveCapsResource}")
	String retrieveCapsResource;
	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: retrieveCapsResource Method Description: retrieve
	 * CapsResource details CSES41D
	 * 
	 * @param rtrvRsrcByStageInDto
	 * @param rtrvRsrcByStageOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RtrvRsrcByStageOutDto> retrieveCapsResource(RtrvRsrcByStageInDto rtrvRsrcByStageInDto,
			RtrvRsrcByStageOutDto rtrvRsrcByStageOutDto) {
		log.debug("Entering method retrieveCapsResource in RetrieveCapsResourceDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveCapsResource)
				.addScalar("ulIdResource", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("szAddrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("szAddrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("szAddrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcState", StandardBasicTypes.STRING)
				.addScalar("lAddrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("szAddrRsrcAttn", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cCdRsrcFaHomeType8", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcStatus", StandardBasicTypes.STRING)
				.addScalar("szCdRsrcType", StandardBasicTypes.STRING)
				.addScalar("dtDtRsrcMarriage", StandardBasicTypes.DATE)
				.addScalar("dtDtRsrcCert", StandardBasicTypes.DATE).addScalar("dtDtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("ulIdRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("ulIdRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("cIndRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("cIndRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("cIndRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("bIndRsrcIndivStudy", StandardBasicTypes.STRING)
				.addScalar("bIndRsrcNonPrs", StandardBasicTypes.STRING)
				.addScalar("szCdCertifyEntity", StandardBasicTypes.STRING)
				.addScalar("bIndRsrcNonPrsPCA", StandardBasicTypes.STRING)
				.addScalar("cIndRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("cIndRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("szNmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szNmResource", StandardBasicTypes.STRING)
				.addScalar("szNmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("dNbrRsrcAnnualIncome", StandardBasicTypes.DOUBLE)
				.addScalar("lNbrSchCampusNbr", StandardBasicTypes.LONG)
				.addScalar("lNbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("uNbrRsrcFacilCapacity", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcFMAgeMax", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcFMAgeMin", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcMlAgeMax", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcMlAgeMin", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcIntChildren", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcIntFeAgeMax", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcIntFeAgeMin", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcIntMaAgeMax", StandardBasicTypes.SHORT)
				.addScalar("uNbrRsrcIntMaAgeMin", StandardBasicTypes.SHORT)
				.addScalar("sNbrRsrcOpenSlots", StandardBasicTypes.SHORT)
				.addScalar("szNbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("lNbrFacilPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("szNbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("szTxtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("szTxtRsrcComments", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdRsrcFaHomeStage", rtrvRsrcByStageInDto.getIdRsrcFaHomeStage())
				.setResultTransformer(Transformers.aliasToBean(RtrvRsrcByStageOutDto.class)));

		List<RtrvRsrcByStageOutDto> liCses41doDto = new ArrayList<>();
		liCses41doDto = (List<RtrvRsrcByStageOutDto>) sQLQuery1.list();

		log.debug("Exiting method retrieveCapsResource in RetrieveCapsResourceDaoImpl");
		return liCses41doDto;
	}

}
