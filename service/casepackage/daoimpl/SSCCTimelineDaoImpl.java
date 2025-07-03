package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.SsccTimeline;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kin.dto.DatabaseResultDetailsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CasePackage
 * Controller Dao Implementation Oct 4, 2017- 4:29:41 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class SSCCTimelineDaoImpl implements SSCCTimelineDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SSCCTimelineDaoImpl.getSSCCTimelineList}")
	private String getSSCCTimelineList;

	@Value("${SSCCTimelineDaoImpl.sqlInsertIntoTimeline}")
	private String sqlInsertIntoTimeline;

	@Value("${SSCCTimelineDaoImpl.sqlGetFromTimeline}")
	private String sqlGetFromTimeline;

	/**
	 * Method Name: getSSCCTimelineList Method Description: Method returns a
	 * PaginationResultBean object with a list of Timeline objects related to a
	 * specific SSCC Referral Id and reference stage.
	 *
	 * @param ssccTimelineDto
	 *            the sscc timeline dto
	 * @return List<SSCCTimelineDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SSCCTimelineDto> getSSCCTimelineList(SSCCTimelineDto ssccTimelineDto) {
		Long idStage = ssccTimelineDto.getIdStage();
		Long idReference = ssccTimelineDto.getIdReference();
		String cdTimelineTableName = ssccTimelineDto.getCdTimelineTableName();
		// commented by me
		DatabaseResultDetailsDto databaseResultDetailsDto = new DatabaseResultDetailsDto();
		// DatabaseResultDetailsDto
		// databaseResultDetailsDto=ssccTimelineDto.getResultDetails();
		String SORT_BY = ServiceConstants.EMPTY_STRING;
		String SORT_DIRECTION = ServiceConstants.EMPTY_STRING;
		if (!TypeConvUtil.isNullOrEmpty(databaseResultDetailsDto)) {
			databaseResultDetailsDto.setResultsPerPage(ServiceConstants.szAddrIncmgFacilZip_IND);
			if (!TypeConvUtil.isNullOrEmpty(databaseResultDetailsDto.getOrderBy())) {
				SORT_BY = databaseResultDetailsDto.getOrderBy();
			}
			if (!TypeConvUtil.isNullOrEmpty(databaseResultDetailsDto.getOrderByDirection())) {
				SORT_DIRECTION = databaseResultDetailsDto.getOrderByDirection();
			}
		}
		StringBuilder stringBuilder = new StringBuilder(getSSCCTimelineList);
		if (!ServiceConstants.EMPTY_STRING.equals(SORT_BY) && SORT_BY.equals(ServiceConstants.ROLE)) {
			stringBuilder.append(ServiceConstants.ORDER_BY_CLAUSE_FOR_SORT_BY_AGENCY);
		} else if (!ServiceConstants.EMPTY_STRING.equals(SORT_BY) && SORT_BY.equals(ServiceConstants.SORT_BY_COUNTY)) {
			stringBuilder.append(ServiceConstants.ORDER_BY_CLAUSE_FOR_SORT_BY_ENTERED_BY);
		} else if (!ServiceConstants.EMPTY_STRING.equals(SORT_BY) && SORT_BY.equals(ServiceConstants.STATE_MED_ONLY)) {
			stringBuilder.append(ServiceConstants.ORDER_BY_CLAUSE_FOR_SORT_BY_MILESTONE);
		} else if (!ServiceConstants.EMPTY_STRING.equals(SORT_BY)
				&& SORT_BY.equals(ServiceConstants.SORT_BY_DATE_RECORDED)) {
			stringBuilder.append(ServiceConstants.ORDER_BY_CLAUSE_FOR_SORT_BY_DTRECORDED);
		} else {
			stringBuilder.append(ServiceConstants.ORDER_BY_CLAUSE_FOR_SORT_BY_DEFAULT);
		}
		if (!ServiceConstants.EMPTY_STRING.equals(SORT_DIRECTION)) {
			stringBuilder.append(SORT_DIRECTION);
		}
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString())
				.addScalar("idSsccTimeline", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG).addScalar("idReference", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdTimelineTableName", StandardBasicTypes.STRING)
				.addScalar("idRsrcSscc", StandardBasicTypes.LONG)
				.addScalar("txtTimelineDesc", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("agency", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setParameter("idReference", idReference)
				.setParameter("cdTimelineTableName", cdTimelineTableName)
				.setResultTransformer(Transformers.aliasToBean(SSCCTimelineDto.class));
		List<SSCCTimelineDto> ssccTimelineDtoList = (List<SSCCTimelineDto>) sqlQuery.list();

		return ssccTimelineDtoList;
	}

	/**
	 * Method Name: insertSSCCTimeline Method Description: Inserts record into
	 * the SSCC Timeline table
	 * 
	 * @param ssccTimelineDto
	 * @return void
	 */

	@Override
	public Long insertSSCCTimeline(SSCCTimelineDto ssccTimelineDto) {
		Long idSsccTimeline = 0L;
		SsccTimeline sSCCTimeLine = new SsccTimeline();
		if (!ObjectUtils.isEmpty(ssccTimelineDto)) {
			sSCCTimeLine.setIdSsccTimeline(ssccTimelineDto.getIdSsccTimeline());
			sSCCTimeLine.setDtCreated(new Date());
			sSCCTimeLine.setIdCreatedPerson(ssccTimelineDto.getIdCreatedPerson());
			sSCCTimeLine.setIdLastUpdatePerson(ssccTimelineDto.getIdLastUpdatePerson());
			sSCCTimeLine.setDtRecorded(new Date());

			SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().get(SsccReferral.class,
					ssccTimelineDto.getIdSsccReferral());
			sSCCTimeLine.setSsccReferral(ssccReferral);
			sSCCTimeLine.setIdRsrcSscc(ssccTimelineDto.getIdRsrcSscc());
			sSCCTimeLine.setIdReference(ssccTimelineDto.getIdReference());

			sSCCTimeLine.setCdTimelineTableName(ssccTimelineDto.getCdTimelineTableName());
			sSCCTimeLine.setTxtTimelineDesc(ssccTimelineDto.getTxtTimelineDesc());
			idSsccTimeline = (Long) sessionFactory.getCurrentSession().save(sSCCTimeLine);
		}
		return idSsccTimeline;
	}

}
