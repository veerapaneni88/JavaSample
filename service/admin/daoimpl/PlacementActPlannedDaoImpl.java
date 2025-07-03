package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * retreives a full row from the Placement table where ID PERSON = Host and DT
 * PLCMT END is greatest and IND PLCMT ACT PLANNED = TRUE Aug 10, 2017- 11:12:48
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PlacementActPlannedDaoImpl implements PlacementActPlannedDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PlacementActPlannedDaoImpl.getPlacementRecord}")
	private String getPlacementRecord;

	private static final Logger log = Logger.getLogger(PlacementActPlannedDaoImpl.class);

	public PlacementActPlannedDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPlacementRecord Method Description: Get placement record
	 * for given person. Dam Name: CSES34D For CCMM35S Service.
	 * 
	 * @param idPerson
	 * @return PlacementActPlannedOutDto
	 * @throws DataNotFoundException
	 */
	@Override
	public PlacementActPlannedOutDto getPlacementRecord(Long idPerson) throws DataNotFoundException {
		log.debug("Entering method getPlacementRecord in PlacementActPlannedDaoImpl");
		PlacementActPlannedOutDto placementActPlannedOutDto = null;
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementRecord)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
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
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSxVctmztnHistoryDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtStart", StandardBasicTypes.TIMESTAMP)
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
				.addScalar("plcmtRemovalRsn", StandardBasicTypes.STRING).setParameter("hI_ulIdPlcmtChild", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PlacementActPlannedOutDto.class)));
		List<PlacementActPlannedOutDto> placementActPlannedOutDtoList = (List<PlacementActPlannedOutDto>) sqlQuery
				.list();
		if (!CollectionUtils.isEmpty(placementActPlannedOutDtoList)) {
			placementActPlannedOutDto = placementActPlannedOutDtoList.get(0);
		}
		log.debug("Exiting method getPlacementRecord in PlacementActPlannedDaoImpl");
		return placementActPlannedOutDto;
	}

	@Override
	public List<PlacementActPlannedOutDto> getPlacementRecord(PlacementActPlannedInDto pCSES34DInputRec) {
		log.debug("Entering method PlacementActPlannedQUERYdam in PlacementActPlannedDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPlacementRecord)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
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
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSxVctmztnHistoryDiscuss", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPlcmtStart", StandardBasicTypes.TIMESTAMP)
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
				.addScalar("plcmtRemovalRsn", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdPlcmtChild", pCSES34DInputRec.getIdPlcmtChild())
				.setResultTransformer(Transformers.aliasToBean(PlacementActPlannedOutDto.class)));
		List<PlacementActPlannedOutDto> liCses34doDto = (List<PlacementActPlannedOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCses34doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses34dDaoImpl.no.placement.record.for.person", null, Locale.US));
		}
		log.debug("Exiting method PlacementActPlannedQUERYdam in PlacementActPlannedDaoImpl");
		return liCses34doDto;
	}
}
