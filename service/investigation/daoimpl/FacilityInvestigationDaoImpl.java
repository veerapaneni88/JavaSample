package us.tx.state.dfps.service.investigation.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.investigation.dao.FacilityInvestigationDao;
import us.tx.state.dfps.xmlstructs.inputstructs.FacilityInvestigationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityInvestigationDaoImpl Sep 9, 2017- 10:39:01 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class FacilityInvestigationDaoImpl implements FacilityInvestigationDao {

	@Value("${Cinv17dDaoImpl.getFacilityInvestigationDetail}")
	private transient String getFacilityInvestigationDetailSql;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Name: getFacilityInvestigationDetail Method Description: fetches
	 * Facility Investigation Details.
	 * 
	 * @param facilityInvestigationDto
	 * @return FacilInvstInfoDto
	 */
	@Override
	public FacilInvstInfoDto getFacilityInvestigationDetail(FacilityInvestigationDto facilityInvestigationDto) {
		FacilInvstInfoDto facilInvstInfoDto = new FacilInvstInfoDto();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFacilityInvestigationDetailSql)
				.addScalar("szCdMhmrCompCode", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffAttn", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffCity", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffCnty", StandardBasicTypes.STRING)
				.addScalar("addrFacilInvstAffSt", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffStr1", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffStr2", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstStr1", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstStr2", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAffZip", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstAttn", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstCity", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstCnty", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstState", StandardBasicTypes.STRING)
				.addScalar("szAddrFacilInvstZip", StandardBasicTypes.STRING)
				.addScalar("dtDtFacilInvstBegun", StandardBasicTypes.DATE)
				.addScalar("dtDtFacilInvstComplt", StandardBasicTypes.DATE)
				.addScalar("dtDtFacilInvstIncident", StandardBasicTypes.DATE)
				.addScalar("dtDtFacilInvstIntake", StandardBasicTypes.DATE)
				.addScalar("ulIdAffilResource", StandardBasicTypes.LONG).addScalar("ulIdEvent", StandardBasicTypes.LONG)
				.addScalar("ulIdFacilResource", StandardBasicTypes.LONG).addScalar("ulIdStage", StandardBasicTypes.LONG)
				.addScalar("szNbrFacilInvstAffilExt", StandardBasicTypes.STRING)
				.addScalar("lNbrFacilInvstAffilPhn", StandardBasicTypes.STRING)
				.addScalar("szNbrFacilInvstExtension", StandardBasicTypes.STRING)
				.addScalar("lNbrFacilInvstPhone", StandardBasicTypes.STRING)
				.addScalar("szNmFacilInvstAff", StandardBasicTypes.STRING)
				.addScalar("szNmFacilInvstFacility", StandardBasicTypes.STRING)
				.addScalar("szTxtFacilInvstAffilCmnt", StandardBasicTypes.STRING)
				.addScalar("szTxtFacilInvstComments", StandardBasicTypes.STRING)
				.addScalar("szCdFacilInvstOvrallDis", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cIndFacilSuperintNotif", StandardBasicTypes.STRING)
				.addScalar("cIndFacilStreamlined", StandardBasicTypes.STRING)
				.addScalar("szCdLocationOfIncident", StandardBasicTypes.STRING)
				.addScalar("szCdPriorCaseHistRev", StandardBasicTypes.STRING)
				.addScalar("idPrgrmAdminPerson", StandardBasicTypes.LONG)

				.setResultTransformer(Transformers.aliasToBean(FacilInvstInfoDto.class));
		query.setParameter("ulIdStage", facilityInvestigationDto.getUlIdStage());

		facilInvstInfoDto = (FacilInvstInfoDto) query.uniqueResult();
		return facilInvstInfoDto;
	}

}
