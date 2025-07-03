package us.tx.state.dfps.service.notiftolawenforce.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDateDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 14, 2018- 5:16:35 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class NotifToLawEnforcementDaoImpl implements NotifToLawEnforcementDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NotifToLawEnforcementDaoImpl.getFacilityInvDtlbyId}")
	private String getFacilityInvDtlbyIdSql;

	@Value("${NotifToLawEnforcementDaoImpl.getPriorStagebyId}")
	private String getPriorStagebyIdSql;

	@Value("${NotifToLawEnforcementDaoImpl.getMultiAddress}")
	private String getMultiAddressSql;

	@Value("${NotifToLawEnforcementDaoImpl.getMinContactDate}")
	private String getMinContactDatesql;

	/**
	 * This DAO performs a Query of the Facility Inv Dtl table. Service Name -
	 * CINV80S, DAM Name - CSES39D
	 * 
	 * @param idStage
	 * @return FacilInvDtlDto
	 */
	@Override
	public FacilInvDtlDto getFacilityInvDtlbyId(Long idStage) {
		FacilInvDtlDto facilInvDtlDto = new FacilInvDtlDto();
		facilInvDtlDto = (FacilInvDtlDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFacilityInvDtlbyIdSql).setParameter("idStage", idStage))
						.addScalar("addrFacilInvstAffAttn", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffCity", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffCnty", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffSt", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffStr1", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffStr2", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstStr1", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstStr2", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAffZip", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstAttn", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstCity", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstCnty", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstState", StandardBasicTypes.STRING)
						.addScalar("addrFacilInvstZip", StandardBasicTypes.STRING)
						.addScalar("dtFacilInvstBegun", StandardBasicTypes.DATE)
						.addScalar("dtFacilInvstComplt", StandardBasicTypes.DATE)
						.addScalar("dtFacilInvstIncident", StandardBasicTypes.DATE)
						.addScalar("dtFacilInvstIntake", StandardBasicTypes.DATE)
						.addScalar("idAffilResource", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("idFacilResource", StandardBasicTypes.LONG)
						.addScalar("idFacilInvstStage", StandardBasicTypes.LONG)
						.addScalar("nbrFacilinvstAffilExt", StandardBasicTypes.STRING)
						.addScalar("nbrFacilinvstAffilPhn", StandardBasicTypes.STRING)
						.addScalar("nbrFacilinvstExtension", StandardBasicTypes.STRING)
						.addScalar("nbrFacilinvstPhone", StandardBasicTypes.STRING)
						.addScalar("nmFacilinvstAff", StandardBasicTypes.STRING)
						.addScalar("nmFacilinvstFacility", StandardBasicTypes.STRING)
						.addScalar("txtFacilInvstAffilCmnt", StandardBasicTypes.STRING)
						.addScalar("txtFacilInvstComments", StandardBasicTypes.STRING)
						.addScalar("cdFacilInvstOvrallDis", StandardBasicTypes.STRING)
						.addScalar("DtLastUpdate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FacilInvDtlDto.class)).uniqueResult();
		return facilInvDtlDto;
	}

	/**
	 * This DAO gets prior stage info, stage starts date by idStage. Service
	 * Name - CINV80S, DAM Name - CINV86D
	 * 
	 * @param idStage
	 * @return PriorStageDto
	 * 
	 */
	@Override
	public PriorStageDto getPriorStagebyId(Long idStage) {
		PriorStageDto priorStageDto = new PriorStageDto();
		priorStageDto = (PriorStageDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPriorStagebyIdSql).setParameter("idStage", idStage))
						.addScalar("idPriorStage", StandardBasicTypes.LONG)
						.addScalar("dtStageStart", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PriorStageDto.class)).uniqueResult();
		return priorStageDto;
	}

	/**
	 * method description: This DAO is to fetch multiple MHMR facility
	 * addresses. Service Name - CINV80S, DAM Name - CLSCGCD
	 * 
	 * @param idStage,
	 *            idCase
	 * @return List<MultiAddressDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MultiAddressDto> getMultiAddress(Long idStage, Long idCase) {
		List<MultiAddressDto> multiAddressDtoList = new ArrayList<MultiAddressDto>();
		multiAddressDtoList = (ArrayList<MultiAddressDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getMultiAddressSql).setParameter("idCase", idCase).setParameter("idStage", idStage))
						.addScalar("aCpNmResource", StandardBasicTypes.STRING)
						.addScalar("aFilCdMhmrCode", StandardBasicTypes.STRING)
						.addScalar("aAddrRsrcAddrStLn1", StandardBasicTypes.STRING)
						.addScalar("aAddrRsrcAddrStLn2", StandardBasicTypes.STRING)
						.addScalar("aAddrRsrcAddrCity", StandardBasicTypes.STRING)
						.addScalar("aCdRsrcAddrState", StandardBasicTypes.STRING)
						.addScalar("aAddrRsrcAddrZip", StandardBasicTypes.STRING)
						.addScalar("aCdRsrcAddrCounty", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(MultiAddressDto.class)).list();
		return multiAddressDtoList;
	}

	/**
	 * Method Name: getMinContactDate Method Description: This Dao is to fetch
	 * earliest contact date based on id stage.
	 * 
	 * @param idStage
	 * @return ContactDateDto
	 */
	@Override
	public ContactDateDto getMinContactDate(Long idStage) {
		ContactDateDto contactDateDto = new ContactDateDto();
		contactDateDto = (ContactDateDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getMinContactDatesql).setParameter("idStage", idStage))
						.addScalar("dtDTContactOccurred", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(ContactDateDto.class)).uniqueResult();
		return contactDateDto;
	}
}
