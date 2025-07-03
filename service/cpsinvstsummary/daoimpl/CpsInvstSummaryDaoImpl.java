package us.tx.state.dfps.service.cpsinvstsummary.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.cpsinvstsummary.dto.RiskAssessmentInfoDto;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * database calls for service Mar 28, 2018- 4:10:06 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class CpsInvstSummaryDaoImpl implements CpsInvstSummaryDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CpsInvstDetailDao cpsInvstDetailDao;

	@Autowired
	private IncomingDetailDao incomingDetailDao;

	@Value("${CpsInvstSummaryDaoImpl.getRiskAssessmentInfo}")
	private transient String getRiskAssessmentInfoSql;

	/**
	 * Method Name: getRiskAssessmentInfo Method Description: Retrieves risk
	 * assessment info (CSECFAD)
	 * 
	 * @param idStage
	 * @return List<RiskAssessmentInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskAssessmentInfoDto> getRiskAssessmentInfo(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRiskAssessmentInfoSql)
				.addScalar("idCpsRa", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdFinalRiskLevel", StandardBasicTypes.STRING)
				.addScalar("idCpsRaAssmtLookup", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtAssmtCompleted",StandardBasicTypes.DATE)
				.setParameter("idStage", idStage)
				.setParameter("cdEventStatusComp", ServiceConstants.EVENTSTATUS_COMPLETE)
				.setParameter("cdEventStatusPend", ServiceConstants.EVENTSTATUS_PENDING)
				.setParameter("cdEventStatusAprv", ServiceConstants.EVENTSTATUS_APPROVE)
				.setResultTransformer(Transformers.aliasToBean(RiskAssessmentInfoDto.class));
		return query.list();
	}

	/**
	 * Method Name: getCpsInvstDetail Method Description: Calls DAO method from
	 * CpsInvstDetailDao and transfers data from domain object to DTO (DAM:
	 * CINV95D)
	 * 
	 * @param idStage
	 * @return List<CpsInvstDetailDto>
	 */
	public List<CpsInvstDetailDto> getCpsInvstDetail(Long idStage) {
		List<CpsInvstDetail> cpsInvstDetailList = cpsInvstDetailDao.getCpsInvstDetailbyParentId(idStage);
		List<CpsInvstDetailDto> cpsInvstDetailDtoList = new ArrayList<>();
		for (CpsInvstDetail cpsInvstDetail : cpsInvstDetailList) {
			CpsInvstDetailDto dto = new CpsInvstDetailDto();
			dto.setIdEvent(cpsInvstDetail.getIdEvent());
			dto.setIdStage(cpsInvstDetail.getStage().getIdStage());
			dto.setDtLastUpdate(cpsInvstDetail.getDtLastUpdate());
			dto.setIdCase(cpsInvstDetail.getIdCase());
			dto.setDtCpsInvstDtlComplt(cpsInvstDetail.getDtCpsInvstDtlComplt());
			dto.setDtCpsInvstDtlBegun(cpsInvstDetail.getDtCpsInvstDtlBegun());
			dto.setIndCpsInvstSafetyPln(cpsInvstDetail.getIndCpsInvstSafetyPln());
			dto.setIndCpsInvstDtlRaNa(cpsInvstDetail.getIndCpsInvstDtlRaNa());
			dto.setDtCpsInvstDtlAssigned(cpsInvstDetail.getDtCpsInvstDtlAssigned());
			dto.setDtCpsInvstDtlIntake(cpsInvstDetail.getDtCpsInvstDtlIntake());
			dto.setCdCpsInvstDtlFamIncm(cpsInvstDetail.getCdCpsInvstDtlFamIncm());
			dto.setIndCpsInvstDtlEaConcl(cpsInvstDetail.getIndCpsInvstDtlEaConcl());
			dto.setIndCpsInvstDtlAbbrv(cpsInvstDetail.getIndCpsInvstDtlAbbrv());
			dto.setIndCpsLeJntCntct(cpsInvstDetail.getIndCpsLeJntCntct());
			dto.setCdReasonNoJntCntct(cpsInvstDetail.getCdReasonNoJntCntct());
			dto.setReasonNoJntCntct(cpsInvstDetail.getTxtReasonNoJntCntct());
			dto.setIndVictimTaped(cpsInvstDetail.getIndVictimTaped());
			dto.setCdVictimTaped(cpsInvstDetail.getCdVictimTaped());
			dto.setVictimTaped(cpsInvstDetail.getTxtVictimTaped());
			dto.setIndMeth(cpsInvstDetail.getIndMeth());
			dto.setIndVictimPhoto(cpsInvstDetail.getIndVictimPhoto());
			dto.setCdVictimNoPhotoRsn(cpsInvstDetail.getCdVictimNoPhotoRsn());
			dto.setVictimPhoto(cpsInvstDetail.getTxtVictimPhoto());
			dto.setIndParentGivenGuide(cpsInvstDetail.getIndParentGivenGuide());
			dto.setIndParentNotify24h(cpsInvstDetail.getIndParentNotify24h());
			dto.setIndMultPersFound(cpsInvstDetail.getIndMultPersFound());
			dto.setIndMultPersMerged(cpsInvstDetail.getIndMultPersMerged());
			dto.setIndFtmOffered(cpsInvstDetail.getIndFtmOffered());
			dto.setIndFtmOccurred(cpsInvstDetail.getIndFtmOccurred());
			dto.setIndReqOrders(cpsInvstDetail.getIndReqOrders());
			dto.setIndAbsentParent(cpsInvstDetail.getIndAbsentParent());
			dto.setAbsentParent(cpsInvstDetail.getTxtAbsentParent());
			dto.setRsnOvrllDisptn(cpsInvstDetail.getTxtRsnOvrllDisptn());
			dto.setRsnOpenServices(cpsInvstDetail.getTxtRsnOpenServices());
			dto.setRsnInvClosed(cpsInvstDetail.getTxtRsnInvClosed());
			dto.setIndChildSexTraffic(cpsInvstDetail.getIndChildSexTraffic());
			dto.setIndChildLaborTraffic(cpsInvstDetail.getIndChildLaborTraffic());
			dto.setCdCpsOverallDisptn(cpsInvstDetail.getCdCpsInvstDtlOvrllDisptn());
			//PPM 69915 - Alcohol Substance Tracker Changes
			dto.setIndSubstancePrnt(cpsInvstDetail.getIndSubstancePrnt());
			dto.setIndSubstanceChild(cpsInvstDetail.getIndSubstanceChild());
			dto.setIndVrblWrtnNotifRights(cpsInvstDetail.getIndVrblWrtnNotifRights());
			dto.setIndNotifRightsUpld(cpsInvstDetail.getIndNotifRightsUpld());
			cpsInvstDetailDtoList.add(dto);
		}
		return cpsInvstDetailDtoList;
	}

	/**
	 * Method Name: getIncomingDetail Method Description: Calls DAO method from
	 * IncomingDetailDao and transfers data from domain object to DTO (DAM:
	 * CINT07D)
	 * 
	 * @param idStage
	 * @return
	 */
	public IncomingDetailDto getIncomingDetail(Long idStage) {
		IncomingDetail incomingDetail = incomingDetailDao.getincomingDetailbyId(idStage);
		IncomingDetailDto incomingDetailDto = new IncomingDetailDto();
		incomingDetailDto.setIdStage(incomingDetail.getIdStage());
		if(!ObjectUtils.isEmpty(incomingDetail.getPerson())){
			incomingDetailDto.setIdPerson(incomingDetail.getPerson().getIdPerson());
		}
		if(!ObjectUtils.isEmpty(incomingDetail.getCapsResource())){
			incomingDetailDto.setIdResource(incomingDetail.getCapsResource().getIdResource());
		}
		incomingDetailDto.setDtLastUpdate(incomingDetail.getDtLastUpdate());
		incomingDetailDto.setIdCase(incomingDetail.getIdCase());
		incomingDetailDto.setIdEvent(incomingDetail.getIdEvent());
		incomingDetailDto.setIncmgUnit(incomingDetail.getNbrIncmgUnit());
		incomingDetailDto.setCdIncmgRegion(incomingDetail.getCdIncmgRegion());
		incomingDetailDto.setNmIncomingCallerLast(incomingDetail.getNmIncomingCallerLast());
		incomingDetailDto.setCdIncmgCallerInt(incomingDetail.getCdIncmgCallerInt());
		incomingDetailDto.setAddrIncmgWorkerCity(incomingDetail.getAddrIncmgWorkerCity());
		incomingDetailDto.setIncmgWorkerPhone(incomingDetail.getNbrIncmgWorkerPhone());
		incomingDetailDto.setIncmgWorkerExt(incomingDetail.getNbrIncmgWorkerExt());
		incomingDetailDto.setNmIncmgWorkerName(incomingDetail.getNmIncmgWorkerName());
		incomingDetailDto.setCdIncmgAllegType(incomingDetail.getCdIncmgAllegType());
		incomingDetailDto.setCdIncmgCallerAddrType(incomingDetail.getCdIncmgCallerAddrType());
		incomingDetailDto.setCdIncmgCallerPhonType(incomingDetail.getCdIncmgCallerPhonType());
		incomingDetailDto.setIncmgCallerPhonExt(incomingDetail.getNbrIncmgCallerPhonExt());
		incomingDetailDto.setCdIncomingCallerSuffix(incomingDetail.getCdIncomingCallerSuffix());
		incomingDetailDto.setCdIncmgSpecHandling(incomingDetail.getCdIncmgSpecHandling());
		incomingDetailDto.setIndIncmgSensitive(incomingDetail.getIndIncmgSensitive());
		incomingDetailDto.setIndIncmgWorkerSafety(incomingDetail.getIndIncmgWorkerSafety());
		incomingDetailDto.setIncmgSensitive(incomingDetail.getTxtIncmgSensitive());
		incomingDetailDto.setCdIncomingCallType(incomingDetail.getCdIncomingCallType());
		incomingDetailDto.setCdIncmgSex(incomingDetail.getCdIncmgSex());
		incomingDetailDto.setAddrIncmgStreetLn1(incomingDetail.getAddrIncmgStreetLn1());
		incomingDetailDto.setAddrIncmgStreetLn2(incomingDetail.getAddrIncmgStreetLn2());
		incomingDetailDto.setIndIncmgNoFactor(incomingDetail.getIndIncmgNoFactor());
		incomingDetailDto.setAddrIncmgZip(incomingDetail.getAddrIncmgZip());
		incomingDetailDto.setNmIncmgRegardingLast(incomingDetail.getNmIncmgRegardingLast());
		incomingDetailDto.setNmIncmgJurisdiction(incomingDetail.getNmIncmgJurisdiction());
		incomingDetailDto.setAddrIncomingCallerCity(incomingDetail.getAddrIncomingCallerCity());
		incomingDetailDto.setDtIncomingCallDisposed(incomingDetail.getDtIncomingCallDisposed());
		incomingDetailDto.setCdIncomingDisposition(incomingDetail.getCdIncomingDisposition());
		incomingDetailDto.setCdIncmgStatus(incomingDetail.getCdIncmgStatus());
		incomingDetailDto.setNmIncomingCallerFirst(incomingDetail.getNmIncomingCallerFirst());
		incomingDetailDto.setCdIncomingCallerState(incomingDetail.getCdIncomingCallerState());
		incomingDetailDto.setDtIncomingCall(incomingDetail.getDtIncomingCall());
		incomingDetailDto.setNmIncmgRegardingFirst(incomingDetail.getNmIncmgRegardingFirst());
		incomingDetailDto.setCdIncomingCallerCounty(incomingDetail.getCdIncomingCallerCounty());
		incomingDetailDto.setCdIncomingProgramType(incomingDetail.getCdIncomingProgramType());
		incomingDetailDto.setIncomingCallerPhone(incomingDetail.getNbrIncomingCallerPhone());
		incomingDetailDto.setNmIncomingCallerMiddle(incomingDetail.getNmIncomingCallerMiddle());
		incomingDetailDto.setIndIncmgIntInvClsReclass(incomingDetail.getIndIncmgIntInvClsReclass());
		incomingDetailDto.setIndIncmgSuspMeth(incomingDetail.getIndIncmgSuspMeth());
		incomingDetailDto.setIncmgSuspMeth(incomingDetail.getTxtIncmgSuspMeth());
		incomingDetailDto.setDtIncomingRecorded(incomingDetail.getDtIncomingRecorded());
		incomingDetailDto.setRelatedCalls(incomingDetail.getTxtRelatedCalls());
		incomingDetailDto.setIndFoundOpenCase(incomingDetail.getIndFoundOpenCase());
		incomingDetailDto.setIndCallReentry(incomingDetail.getIndCallReentry());
		incomingDetailDto.setCdReentryRsn(incomingDetail.getCdReentryRsn());
		incomingDetailDto.setIndWorkerProvided(incomingDetail.getIndWorkerProvided());
		incomingDetailDto.setCdDisaster(incomingDetail.getCdDisaster());
		incomingDetailDto.setSecCallerPhone(incomingDetail.getNbrSecCallerPhone());
		incomingDetailDto.setSecCallerPhonExt(incomingDetail.getNbrSecCallerPhonExt());
		incomingDetailDto.setCdSecCallerPhonType(incomingDetail.getCdSecCallerPhonType());
		incomingDetailDto.setReporterNotes(incomingDetail.getTxtReporterNotes());
		incomingDetailDto.setIndLenMnul(incomingDetail.getIndLenMnul());
		incomingDetailDto.setIndCaseNbrRqstd(incomingDetail.getIndCaseNbrRqstd());
		incomingDetailDto.setIdPersonRprtr(incomingDetail.getIdPersonRprtr());
		incomingDetailDto.setIndSendEmail(incomingDetail.getIndSendEmail());
		incomingDetailDto.setErprtConfrmtnNbr(incomingDetail.getTxtErprtConfrmtnNbr());
		return incomingDetailDto;
	}

}
