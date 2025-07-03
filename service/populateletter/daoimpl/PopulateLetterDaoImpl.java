/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: dao methods for populate letter
 *Jan 11, 2018- 3:39:15 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.populateletter.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.legal.daoimpl.PersonDetailsDaoImpl;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dao methods
 * for populate letter Jan 11, 2018- 3:39:15 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PopulateLetterDaoImpl implements PopulateLetterDao {

	/**
	 * 
	 */
	public PopulateLetterDaoImpl() {

	}

	private static final Logger log = Logger.getLogger(PopulateLetterDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseInfoDaoImpl.getCaseInfoById}")
	private transient String getCaseInfoByIdSql;
	
	@Value("${CaseInfoDaoImpl.getPPMCaseInfoById}")
	private transient String getPPMCaseInfoByIdSql;

	@Value("${CaseInfoDaoImpl.getReporterInfoByIdSql}")
	private transient String getReporterInfoByIdSql;

	@Value("${CodesTablesDaoImpl.getPersonInfoByCode}")
	private transient String getPersonInfoByCodeSql;

	@Value("${PersonAddressDaoImpl.getAddressMatchById}")
	private String getAddressMatchByIdSql;

	@Value("${CaseSummaryTool.getEmailList}")
	private String getEmailList;

	@Value("${SubcareDaoImpl.getCVSMonthlyList}")
	private String getCVSMonthlyList;

	/**
	 * Method Name: getCaseInfoById Method Description: This method retrieves
	 * all info about principals of the case based upon an id stage. Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK CLSC01D
	 * 
	 * @param idStage
	 ** @param cdStagePersType
	 * @return List<CaseInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseInfoDto> getCaseInfoById(Long idStage, String cdStagePersType) {
		List<CaseInfoDto> caseInfoDtoLists = new ArrayList<CaseInfoDto>();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseInfoByIdSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("personOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
				.addScalar("idDescPerson", StandardBasicTypes.STRING)
				.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE).addScalar("idPersonAddr", StandardBasicTypes.LONG)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG)
				.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
				.addScalar("persAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAddrHash", StandardBasicTypes.INTEGER)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrAttn", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("idPersonPhone", StandardBasicTypes.LONG)
				.addScalar("txtPersonPhoneComments", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
				.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING).addScalar("dtNameStart", StandardBasicTypes.DATE)
				.addScalar("dtNameEnd", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("cdStagePersType", cdStagePersType)
				.setResultTransformer(Transformers.aliasToBean(CaseInfoDto.class));

		caseInfoDtoLists = (List<CaseInfoDto>) query.list();

		for (CaseInfoDto caseInfoDto : caseInfoDtoLists) {
			query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEmailList).setParameter("idPerson",
					caseInfoDto.getIdPerson());
			if (!ObjectUtils.isEmpty(query.list())) {
				caseInfoDto.setTxtEmail((String) query.list().get(0));
			}
		}

		return caseInfoDtoLists;
	}

	
	/**
	 * Method Name: getPPMCaseInfoById Method Description: This method retrieves
	 * all info about principals of the case based upon an id stage. Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK CLSC01D ORDER BY SPL.IND_STAGE_PERS_REPORTER
	 * 
	 * @param idStage
	 ** @param cdStagePersType
	 * @return List<CaseInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseInfoDto> getPPMCaseInfoById(Long idStage, String cdStagePersType) {
		List<CaseInfoDto> caseInfoDtoLists = new ArrayList<CaseInfoDto>();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPPMCaseInfoByIdSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("personOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
				.addScalar("idDescPerson", StandardBasicTypes.STRING)
				.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE).addScalar("idPersonAddr", StandardBasicTypes.LONG)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG)
				.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
				.addScalar("persAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAddrHash", StandardBasicTypes.INTEGER)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrAttn", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("idPersonPhone", StandardBasicTypes.LONG)
				.addScalar("txtPersonPhoneComments", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
				.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING).addScalar("dtNameStart", StandardBasicTypes.DATE)
				.addScalar("dtNameEnd", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("cdStagePersType", cdStagePersType)
				.setResultTransformer(Transformers.aliasToBean(CaseInfoDto.class));

		caseInfoDtoLists = (List<CaseInfoDto>) query.list();

		for (CaseInfoDto caseInfoDto : caseInfoDtoLists) {
			query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEmailList).setParameter("idPerson",
					caseInfoDto.getIdPerson());
			if (!ObjectUtils.isEmpty(query.list())) {
				caseInfoDto.setTxtEmail((String) query.list().get(0));
			}
		}

		return caseInfoDtoLists;
	}
	
	
	/**
	 * Method Name: getReporterInfoById Method Description:Retrieves all
	 * information about the REPORTERs in the stage. Tux method : CSEC18D Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK
	 * 
	 * @param idStage
	 * @return List<CaseInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseInfoDto> getReporterInfoById(Long idStage) {
		List<CaseInfoDto> caseInfoDtoList = new ArrayList<CaseInfoDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getReporterInfoByIdSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("personOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("idPersonId", StandardBasicTypes.LONG).addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonIdType", StandardBasicTypes.STRING)
				.addScalar("idDescPerson", StandardBasicTypes.STRING)
				.addScalar("dtPersonIdStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonIdEnd", StandardBasicTypes.DATE).addScalar("idPersonAddr", StandardBasicTypes.LONG)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG)
				.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
				.addScalar("persAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.addScalar("dtPersAddrLinkEnd", StandardBasicTypes.DATE)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAddrHash", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrAttn", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("idPersonPhone", StandardBasicTypes.LONG)
				.addScalar("txtPersonPhoneComments", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
				.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING).addScalar("dtNameStart", StandardBasicTypes.DATE)
				.addScalar("dtNameEnd", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CaseInfoDto.class));
		caseInfoDtoList = query.list();
		return caseInfoDtoList;
	}

	/**
	 * Method Name: getPersonInfoByCode CLSC03D Method Description:This DAM
	 * retrieves the name of the person to whom the letter is being sent and the
	 * name of the child participant. Table Name: CODES_TABLES
	 * 
	 * @param aCodeType,bCodeType
	 * @return List<CodesTablesDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CodesTablesDto> getPersonInfoByCode(String aCodeType, String bCodeType) {
		List<CodesTablesDto> codesTablesDtoLists = new ArrayList<CodesTablesDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonInfoByCodeSql)
				.addScalar("aCode", StandardBasicTypes.STRING).addScalar("aDecode", StandardBasicTypes.STRING)
				.addScalar("aCodeType", StandardBasicTypes.STRING).addScalar("bCode", StandardBasicTypes.STRING)
				.addScalar("bDecode", StandardBasicTypes.STRING).addScalar("bCodeType", StandardBasicTypes.STRING)
				.setParameter("aaCodeType", aCodeType).setParameter("bbCodeType", bCodeType)
				.setResultTransformer(Transformers.aliasToBean(CodesTablesDto.class));
		codesTablesDtoLists = (List<CodesTablesDto>) query.list();
		return codesTablesDtoLists;
	}

	/**
	 * Method Name: getAddressMatchById Method Description: TUX Service :CINTA1D
	 * This function returns a count of address matches for a given reporter and
	 * all PRNs in the case. Table Name: stage_person_link ,address_person_link
	 * ,person_address
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return matchNum
	 */
	@Override
	public Long getAddressMatchById(long idCase, long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAddressMatchByIdSql)
				.addScalar("count", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idPerson", idPerson);
		Long matchNum = (Long) (query.uniqueResult());
		return matchNum;
	}

}
