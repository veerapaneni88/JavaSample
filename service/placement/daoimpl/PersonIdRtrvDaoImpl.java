package us.tx.state.dfps.service.placement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * Class Name:PersonIdRtrvDaoImpl Class Description: This method retrieves the
 * details of Primary Child PersonIdRtrvDaoImpl Oct 25, 2017- 3:18:05 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PersonIdRtrvDaoImpl implements PersonIdDtlsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonIdRtrvDaoImpl.getStagePersonLinkDtls}")
	private transient String getStagePersonLinkDtls;

	@Value("${PersonIdRtrvDaoImpl.getLegalStatusDtls}")
	private transient String getLegalStatusDtls;

	@Value("${PersonIdRtrvDaoImpl.getPlacementRecord}")
	private String getPlacementRecord;
	
	@Value("${PersonIdRtrvDaoImpl.getPersonIdDetails}")
	private String getPersonIdDetailsSql;

	private static final Logger log = Logger.getLogger(PersonIdRtrvDaoImpl.class);

	/**
	 * Method Description: This method retrieves information about the Primary
	 * Child in a case. DAM Name:CSECD3D
	 * 
	 * @param stagePersDto
	 * @return PersonDto @
	 */

	@Override
	public PersonDto getPersonIdRecords(Long idCase, Long idStage, Long idPerson) {
		log.debug("Entering method getPersonIdRecords in PersonIdRtrvDaoImpl");
		PersonDto personDto = null;
		Query querySplDtls = sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkDtls)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		personDto = (PersonDto) querySplDtls.uniqueResult();
		return personDto;
	}

	/**
	 * Method Description: Retrieves the legal status information related to a
	 * Primary Child. DAM Name:CSECD5D
	 * 
	 * @param pInputDataRec
	 * @return List<LegalStatusRtrvOutDto> @
	 */

	@Override
	public LegalStatusOutDto getLegalStatusRecords(Long idPerson, Long idCase) {

		LegalStatusOutDto legalStatusOut = null;
		Query queryLegalDtls = sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusDtls)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("valueString", StandardBasicTypes.STRING)
				.addScalar("prefixString", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(LegalStatusOutDto.class));

		legalStatusOut = (LegalStatusOutDto) queryLegalDtls.uniqueResult();
		return legalStatusOut;
	}

	/**
	 * Method Name: getPlacementRecord Method Description: This DAM retrieves a
	 * full row from the Placement table where Person id is HOST and
	 * PlacementEndDate is greatest and IndPlacementActPlanned is true DAM Name:
	 * CSES34D
	 * 
	 * @param pInputDataRec
	 * @return PlacementActPlannedOutDto @
	 */

	@Override
	public PlacementActPlannedOutDto getPlacementRecord(Long idPerson) {
		log.debug("Entering method getPlacementRecord");
		PlacementActPlannedOutDto placementActPlannedOutDto = null;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementRecord)

				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.DATE)
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
				.addScalar("cdPlcmtService", StandardBasicTypes.STRING)
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
				.addScalar("nmPlcmtAgency", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtContact", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("plcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("plcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("plcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("plcmtRemovalRsn", StandardBasicTypes.STRING).setParameter("idPlcmtChild", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PlacementActPlannedOutDto.class)));
		placementActPlannedOutDto = (PlacementActPlannedOutDto) sQLQuery1.uniqueResult();
		return placementActPlannedOutDto;
	}
	
	/**
	 * Method Description: This method retrieves information about the Primary
	 * Child in a case as per ADS Change. DAM Name:CSECD3D
	 * 
	 * @param stagePersDto
	 * @return PersonDto @
	 */

	@Override
	public PersonDto getPersonIdRecordDtls(Long idCase, Long idStage, Long idPerson) {
		log.debug("Entering method getPersonIdRecords in PersonIdRtrvDaoImpl");
		PersonDto personDto = null;
		Query querySplDtls = sessionFactory.getCurrentSession().createSQLQuery(getPersonIdDetailsSql)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		personDto = (PersonDto) querySplDtls.uniqueResult();
		return personDto;
	}
}
