package us.tx.state.dfps.service.interstatecompact.daoimpl;

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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.arfamilynotification.daoimpl.ARCnclnFamilyNotifDaoImpl;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcInfoDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcTransmittalDto;
import us.tx.state.dfps.service.interstatecompact.dao.ICPformsDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao
 * Implementation for all ICP forms> May 1, 2018- 10:18:29 AM © 2017 Texas
 * Department of Family and Protective Services
 */

@Repository
public class ICPformsDaoImpl implements ICPformsDao {

	@Value("${ICPformsDaoImpl.getIcpcChildDetails}")
	private transient String getIcpcChildDetailssql;

	@Value("${ICPformsDaoImpl.getIcpcReqAndTitle}")
	private transient String getIcpcReqAndTitlesql;

	@Value("${ICPformsDaoImpl.getAddressandName}")
	private transient String getAddressandNamesql;

	@Value("${ICPformsDaoImpl.getPersonDetailsByIdReqest}")
	private transient String getPersonDetailsByIdReqestsql;

	@Value("${ICPformsDaoImpl.getIdEventStage}")
	private transient String getIdEventStagesql;

	@Value("${ICPformsDaoImpl.getICPCinfo}")
	private transient String getICPCinfosql;

	@Value("${ICPformsDaoImpl.getNmResource}")
	private transient String getNmResourcesql;

	@Value("${ICPformsDaoImpl.getPersonInfo}")
	private transient String getPersonInfosql;

	@Value("${ICPformsDaoImpl.getDetailedPersonInfo}")
	private transient String getDetailedPersonInfosql;

	@Value("${ICPformsDaoImpl.getDetailedPersonInfoWithNmResource}")
	private transient String getDetailedPersonInfoWithNmResourcesql;

	@Value("${ICPformsDaoImpl.getPersonNmType}")
	private transient String getPersonNmTypesql;

	@Value("${ICPformsDaoImpl.getPersonAddrInfo}")
	private transient String getPersonAddrInfosql;

	@Value("${ICPformsDaoImpl.getPersonResource}")
	private transient String getPersonResourcesql;

	@Value("${ICPformsDaoImpl.getPersonTypeAndRelation}")
	private transient String getPersonTypeAndRelationsql;

	@Value("${ICPformsDaoImpl.getTransmittalInfo}")
	private transient String getTransmittalInfosql;

	@Value("${ICPformsDaoImpl.getTitleIVEind}")
	private transient String getTitleIVEindsql;

	@Value("${ICPformsDaoImpl.getTransmittalIndicator}")
	private transient String getTransmittalIndicatorsql;

	@Value("${ICPformsDaoImpl.getCountyAddress}")
	private transient String getCountyAddresssql;

	@Value("${ICPformsDaoImpl.getCountyAddressForAgency}")
	private transient String getCountyAddressForAgencysql;

	@Value("${ICPformsDaoImpl.getChildrenFullName}")
	private transient String getChildrenFullNamesql;

	@Value("${ICPformsDaoImpl.getNumberOtherCase}")
	private transient String getNumberOtherCasesql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ARCnclnFamilyNotifDaoImpl.class);

	public ICPformsDaoImpl() {

	}

	/**
	 * Dam Name: csec0cd Method Name: getIcpcChildDetails Method Description:
	 * Retrieves name, ssn, id_person from event_person_link, person, person_id.
	 * One row.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */

	@Override
	public IcpcChildDetailsDto getIcpcChildDetails(Long idEvent) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIcpcChildDetailssql).setParameter("idEvent", idEvent)
				.setParameter("yes", ServiceConstants.Y).setParameter("no", ServiceConstants.N)
				.setParameter("ssn", ServiceConstants.PERSON_ID_TYPE_SSN)
				.setParameter("maxDate", ServiceConstants.MAX_DATE)).addScalar("idIcpcRequest", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPersonType", StandardBasicTypes.STRING)
						.addScalar("nmNameFirst", StandardBasicTypes.STRING)
						.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
						.addScalar("nmNameLast", StandardBasicTypes.STRING)
						.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExt", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSEC1AD Method Name: getIcpcReqAndTitle Method Description:
	 * This DAM retrieves Title IVE and ID ICPC Request.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	@Override
	public IcpcChildDetailsDto getIcpcReqAndTitle(Long idEvent) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIcpcReqAndTitlesql).setParameter("idEvent", idEvent))
						.addScalar("indTitle", StandardBasicTypes.STRING)
						.addScalar("idIcpcRequest", StandardBasicTypes.LONG)
						.addScalar("cdCareType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSEC1CD Method Name: getAddressandName Method Description: This
	 * DAM retrieves addresses and name
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	@Override
	public IcpcChildDetailsDto getAddressandName(Long idIcpcRequest) {
		List<IcpcChildDetailsDto> icpsRsrcList = new ArrayList<IcpcChildDetailsDto>();
		icpsRsrcList = (List<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAddressandNamesql).setParameter("idIcpcRequest", idIcpcRequest))
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return (!ObjectUtils.isEmpty(icpsRsrcList)) ? icpsRsrcList.get(0) : null;
	}

	/**
	 * Dam Name: CLSC25D Method Name: getPersonDetailsByIdReqest Method
	 * Description: retrieves the Names, address, phones for a IdIcpcRequest.
	 * 
	 * @param idIcpcRequest
	 * @return List<IcpcChildDetailsDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getPersonDetailsByIdReqest(Long idIcpcRequest) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonDetailsByIdReqestsql).setParameter("idIcpcRequest", idIcpcRequest)
				.setParameter("yes", ServiceConstants.Y).setParameter("no", ServiceConstants.N)
				.setParameter("ssn", ServiceConstants.PERSON_ID_TYPE_SSN)
				.setParameter("maxDate", ServiceConstants.MAX_DATE)).addScalar("idIcpcRequest", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPersonType", StandardBasicTypes.STRING)
						.addScalar("nmNameFirst", StandardBasicTypes.STRING)
						.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
						.addScalar("nmNameLast", StandardBasicTypes.STRING)
						.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExt", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSECF9D Method Name: getPersonTypeAndRelation Method
	 * Description: This DAM retrieves idPerson cdType, code relation.
	 * 
	 * @param cdPersonType,
	 *            idPerson, idEvent
	 * @return IcpcChildDetailsDto
	 */

	@Override
	public IcpcChildDetailsDto getPersonTypeAndRelation(String cdPersonType, Long idPerson, Long idEvent) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonTypeAndRelationsql).setParameter("idPerson", idPerson)
				.setParameter("cdPersonType", cdPersonType).setParameter("idEvent", idEvent))
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdType", StandardBasicTypes.STRING)
						.addScalar("cdRelation", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSES18D Method Name: getIdEventStage Method Description: This
	 * DAM retrieves stage id that is linked to an event.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	@Override
	public IcpcChildDetailsDto getIdEventStage(Long idEvent) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIdEventStagesql)).addScalar("idEventStage", StandardBasicTypes.LONG)
						.setParameter("idEvent", idEvent)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSECF2D Method Name: getICPCinfo Method Description: This DAM
	 * retrieves codes, placement date, reason text from ICPC related tables.
	 * 
	 * @param idEvent
	 * @return IcpcInfoDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcInfoDto> getICPCinfo(Long idEvent) {
		List<IcpcInfoDto> icpcInfoDto = new ArrayList<IcpcInfoDto>();
		icpcInfoDto = (ArrayList<IcpcInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getICPCinfosql)).addScalar("cdReceivingState", StandardBasicTypes.STRING)
						.addScalar("cdSendingState", StandardBasicTypes.STRING)
						.addScalar("cdPlacementStatus", StandardBasicTypes.STRING)
						.addScalar("dtPlacement", StandardBasicTypes.DATE)
						.addScalar("dtPlacementWithDrawn", StandardBasicTypes.DATE)
						.addScalar("cdCompactTermRsn", StandardBasicTypes.STRING)
						.addScalar("txtReasonOther", StandardBasicTypes.STRING)
						.addScalar("dtPlacementTerm", StandardBasicTypes.DATE)
						.addScalar("txtTerminationNotes", StandardBasicTypes.STRING)
						.addScalar("cdCareType", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
						.setResultTransformer(Transformers.aliasToBean(IcpcInfoDto.class)).list();
		return icpcInfoDto;
	}

	/**
	 * Dam Name: CSECF3D Method Name: getNmResource Method Description: This DAM
	 * retrieves nmResource.
	 * 
	 * @param idEvent
	 * @return List<IcpcInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcInfoDto> getNmResource(Long idEvent) {
		List<IcpcInfoDto> icpcInfoDto = new ArrayList<IcpcInfoDto>();
		icpcInfoDto = (ArrayList<IcpcInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getNmResourcesql)).addScalar("nmResource", StandardBasicTypes.STRING)
						.setParameter("idEvent", idEvent)
						.setResultTransformer(Transformers.aliasToBean(IcpcInfoDto.class)).list();
		return icpcInfoDto;
	}

	/**
	 * Dam Name: CSECF4D Method Name: getPersonInfo Method Description: This DAM
	 * retrieves person information.
	 * 
	 * @param idEvent
	 * @return List<IcpcInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getPersonInfo(Long idEvent) {
		List<IcpcChildDetailsDto> icpcInfoDto = new ArrayList<IcpcChildDetailsDto>();
		icpcInfoDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonInfosql)).addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("cdPersonSex", StandardBasicTypes.STRING)
						.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcInfoDto;
	}

	/**
	 * Dam Name: CSECF7D Method Name: getDetailedPersonInfo Method Description:
	 * This DAM retrieves detailed person information.
	 * 
	 * @param idPerson
	 * @return IcpcChildDetailsDto
	 */
	@Override
	public IcpcChildDetailsDto getDetailedPersonInfo(Long idPerson) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDetailedPersonInfosql).setParameter("idPerson", idPerson))
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("idIcpcStatus", StandardBasicTypes.LONG)
						.addScalar("dtPersAddr", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSECF8D Method Name: getDetailedPersonInfoWithNmResource Method
	 * Description: This DAM retrieves detailed person information and
	 * NM_RESOURCE from CAPS_RESOURCE Table.
	 * 
	 * @param idResource
	 * @return IcpcChildDetailsDto
	 */

	@Override
	public IcpcChildDetailsDto getDetailedPersonInfoWithNmResource(Long idResource) {
		IcpcChildDetailsDto icpcChildDetailsDto = new IcpcChildDetailsDto();
		icpcChildDetailsDto = (IcpcChildDetailsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDetailedPersonInfoWithNmResourcesql).setParameter("idResource", idResource))
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("idIcpcStatus", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).uniqueResult();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CLSCH2D Method Name: getDetailedPersonInfoWithNmResource Method
	 * Description: This DAM retrieves detailed person information such as
	 * person full name, sex, type and birth.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getPersonNmType(Long idEvent) {
		List<IcpcChildDetailsDto> icpcInfoDto = new ArrayList<IcpcChildDetailsDto>();
		icpcInfoDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonNmTypesql)).addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("cdPersonSex", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("cdPersonType", StandardBasicTypes.STRING)
						.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcInfoDto;
	}

	/**
	 * Dam Name: CLSCH3D Method Name: getPersonAddrInfo Method Description: This
	 * DAM retrieves detailed person information such as person full name,
	 * address, id status
	 * 
	 * @param idPerson
	 * @return List<IcpcChildDetailsDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getPersonAddrInfo(Long idPerson) {
		List<IcpcChildDetailsDto> icpcPersonDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcPersonDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonAddrInfosql).setParameter("idPerson", idPerson))
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("idIcpcStatus", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcPersonDetailsDto;
	}

	/**
	 * Dam Name: CLSCH4D Method Name: getPersonResource Method Description: This
	 * DAM retrieves detailed person information such as nm resource, sex, type
	 * and birth.
	 * 
	 * @param idResource
	 * @return List<IcpcChildDetailsDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getPersonResource(Long idResource) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonResourcesql).setParameter("idResource", idResource))
						.addScalar("nmResource", StandardBasicTypes.STRING)
						.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrCounty", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("idIcpcStatus", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSEC1BD Method Name: getTransmittalInfo Method Description:
	 * This DAM retrieves transmittal information.
	 * 
	 * @param idTransmittal
	 * @return IcpcChildDetailsDto
	 */
	@Override
	public IcpcTransmittalDto getTransmittalInfo(Long idTransmittal) {
		IcpcTransmittalDto icpcTransmittalDto = new IcpcTransmittalDto();
		icpcTransmittalDto = (IcpcTransmittalDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getTransmittalInfosql).setParameter("idTransmittal", idTransmittal))
						.addScalar("dtSent", StandardBasicTypes.DATE)
						.addScalar("cdTransmittalType", StandardBasicTypes.STRING)
						.addScalar("idIcpcTransmittal", StandardBasicTypes.LONG)
						.addScalar("nmAttn", StandardBasicTypes.STRING)
						.addScalar("cdReceivingState", StandardBasicTypes.STRING)
						.addScalar("cdSendingState", StandardBasicTypes.STRING)
						.addScalar("c1Decode", StandardBasicTypes.STRING)
						.addScalar("c2Decode", StandardBasicTypes.STRING)
						.addScalar("cdRequestType", StandardBasicTypes.STRING)
						.addScalar("idIcpcRequest", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(IcpcTransmittalDto.class)).uniqueResult();
		return icpcTransmittalDto;
	}

	/**
	 * Dam Name: CSEC1DD Method Name: getTitleIVEind Method Description: This
	 * Dao retrieves Care Type Code from ICPC-100A Placement Request given
	 * ICPC-100B Placement Status information
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getTitleIVEind(Long idEvent) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getTitleIVEindsql).setParameter("idEvent", idEvent))
						.addScalar("cdCareType", StandardBasicTypes.STRING)
						.addScalar("idIcpcRequest", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CLSS2AD Method Name: getTransmittalInfo Method Description:
	 * This DAO retrieves transmittal indicator information.
	 * 
	 * @param idTransmittal
	 * @return List<IcpcTransmittalDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcTransmittalDto> getTransmittalIndicator(Long idTransmittal) {
		List<IcpcTransmittalDto> icpcChildDetailsDtoList = new ArrayList<IcpcTransmittalDto>();
		icpcChildDetailsDtoList = (ArrayList<IcpcTransmittalDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getTransmittalIndicatorsql).setParameter("idTransmittal", idTransmittal))
						.addScalar("indTransmittal", StandardBasicTypes.STRING)
						.addScalar("cdTransmittalType", StandardBasicTypes.STRING)
						.addScalar("concatTypeInd", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcTransmittalDto.class)).list();
		return icpcChildDetailsDtoList;
	}

	/**
	 * Dam Name: CLSCH1D Method Name: getCountyAddress Method Description: This
	 * DAO retrieve county address from 100A
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getCountyAddress(Long idEvent) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCountyAddresssql).setParameter("idEvent", idEvent))
						.addScalar("nmEntity", StandardBasicTypes.STRING)
						.addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("cdType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSEC1ED Method Name: getCountyAddressForAgency Method
	 * Description: This DAO Retrieving address information and phone number of
	 * an agency depending on code type of that agency.
	 * 
	 * @param cdAgencyType,
	 *            inputStateDecode
	 * @return List<IcpcChildDetailsDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getCountyAddressForAgency(String cdAgencyType, String inputStateDecode) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCountyAddressForAgencysql).setParameter("cdAgencyType", cdAgencyType)
				.setParameter("inputStateDecode", inputStateDecode)).addScalar("addrStLn1", StandardBasicTypes.STRING)
						.addScalar("addrStLn2", StandardBasicTypes.STRING)
						.addScalar("addrCity", StandardBasicTypes.STRING)
						.addScalar("addrZip", StandardBasicTypes.STRING)
						.addScalar("addrState", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
						.addScalar("nbrPersonPhoneExt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CLSS2BD Method Name: getChildrenFullName Method Description:
	 * This DAO retrieve Children full names giving the id_icpc_transmittal
	 * 
	 * @param idEvent
	 * @return List<IcpcChildDetailsDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcChildDetailsDto> getChildrenFullName(Long idTransmittal) {
		List<IcpcChildDetailsDto> icpcChildDetailsDto = new ArrayList<IcpcChildDetailsDto>();
		icpcChildDetailsDto = (ArrayList<IcpcChildDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getChildrenFullNamesql).setParameter("idTransmittal", idTransmittal))
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcChildDetailsDto.class)).list();
		return icpcChildDetailsDto;
	}

	/**
	 * Dam Name: CSEC1FD Method Name: getNumberOtherCase Method Description:
	 * This Dao retrieves Other Case Number from ICPC Submission table.
	 * 
	 * @param idTransmittal
	 * @return List<IcpcTransmittalDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IcpcTransmittalDto> getNumberOtherCase(Long idTransmittal) {
		List<IcpcTransmittalDto> icpcChildDetailsDtoList = new ArrayList<IcpcTransmittalDto>();
		icpcChildDetailsDtoList = (ArrayList<IcpcTransmittalDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getNumberOtherCasesql).setParameter("idTransmittal", idTransmittal))
						.addScalar("nbrCaseOtherState", StandardBasicTypes.STRING)
						.addScalar("cdAddtnlInfo", StandardBasicTypes.STRING)
						.addScalar("txtOther", StandardBasicTypes.STRING)
						.addScalar("txtComment", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(IcpcTransmittalDto.class)).list();
		return icpcChildDetailsDtoList;
	}

}
