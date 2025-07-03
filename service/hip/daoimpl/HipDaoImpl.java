package us.tx.state.dfps.service.hip.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.HipAddress;
import us.tx.state.dfps.common.domain.HipFile;
import us.tx.state.dfps.common.domain.HipFileDetail;
import us.tx.state.dfps.common.domain.HipGroup;
import us.tx.state.dfps.common.domain.HipPerson;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.HipGroupDtlReq;
import us.tx.state.dfps.service.common.request.UpdtRecordsReq;
import us.tx.state.dfps.service.common.response.UpdtRecordsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.hip.dao.HipDao;
import us.tx.state.dfps.service.hip.dto.HipAddressDto;
import us.tx.state.dfps.service.hip.dto.HipFileDtlDto;
import us.tx.state.dfps.service.hip.dto.HipGroupDto;
import us.tx.state.dfps.service.hip.dto.HipPersonDto;
import us.tx.state.dfps.service.hip.dto.UpdtRecordDto;

@Repository
public class HipDaoImpl implements HipDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${HipDaoImpl.getHipGroups}")
	private String getHipGroupsSql;

	@Value("${HipDaoImpl.getPrsnLstForGrpDtl}")
	private String getPrsnLstForGrpDtlSql;

	@Value("${HipDaoImpl.getHipFindrsFileDtls}")
	private String getHipFindrsFileDtls;

	@Value("${HipDaoImpl.chkFileComp}")
	private String chkFileComp;

	@Value("${HipDaoImpl.getHipPersonRecord}")
	private String getHipPersonRecord;

	public HipDaoImpl() {

	}

	/**
	 * This service is to get all records for state wide intake
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HipGroupDto> getHipGroups(ServiceReqHeaderDto serviceReqHeaderDto) {

		List<HipGroupDto> hipGroups = null;

		hipGroups = sessionFactory.getCurrentSession().createSQLQuery(getHipGroupsSql)
				.addScalar("idHipGroup", StandardBasicTypes.LONG).addScalar("cdGroupBy")
				.addScalar("idGrpHead", StandardBasicTypes.LONG).addScalar("nmGroup")
				.addScalar("dtPrsnBirth", StandardBasicTypes.TIMESTAMP).addScalar("cdPrsnSex")
				.addScalar("addrResdncStLn1").addScalar("addrResdncStLn2").addScalar("addrResdncCity")
				.addScalar("cdResdncAddrCnty").addScalar("prsnMatchSsnDfps")
				.addScalar("resState", StandardBasicTypes.INTEGER).addScalar("addrResdncZip")
				.setResultTransformer(Transformers.aliasToBean(HipGroupDto.class)).list();
		if (TypeConvUtil.isNullOrEmpty(hipGroups)) {
			throw new DataNotFoundException("No Groups found");
		}

		return hipGroups;
	}

	/**
	 * This service is to get the group details for state wide intake
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HipPersonDto> getHipGroupDetail(HipGroupDtlReq hipGroupDtlReq) {

		List<HipPersonDto> personList = new ArrayList<>();
		personList = (List<HipPersonDto>) sessionFactory.getCurrentSession().createSQLQuery(getPrsnLstForGrpDtlSql)
				.addScalar("idHipPerson", StandardBasicTypes.LONG).addScalar("nmPrsnLglFrst").addScalar("nmPrsnMid")
				.addScalar("prsnMatchStat").addScalar("nmPrnsMdnLast").addScalar("nmPrsnMatchFrstDfps")
				.addScalar("idPrsnDshs", StandardBasicTypes.LONG).addScalar("nmPrsnLglMid")
				.addScalar("nbrPrsnBirthState", StandardBasicTypes.INTEGER).addScalar("cdPrsnTyp")
				.addScalar("nmPrsnMatchMidDfps").addScalar("prsnPlrlty", StandardBasicTypes.INTEGER)
				.addScalar("nmPrsnMatchFrstDshs").addScalar("nmPrsnFrst").addScalar("dtPrsnMatchDobDfps")
				.addScalar("prsnMatchSsnDfps").addScalar("dtPrsnBirth")
				.addScalar("prsnMatchClass", StandardBasicTypes.INTEGER)
				.addScalar("prsnRcrdTyp", StandardBasicTypes.INTEGER).addScalar("nmPrsnSufx")
				.addScalar("cdStateFileNbr").addScalar("nmPrsnLglLst").addScalar("nmPrsnMatchMidDshs")
				.addScalar("cdPrsnSex").addScalar("nmPrsnLst").addScalar("nmPrsnMatchLstDshs")
				.addScalar("dtPrsnMatchDobDshs").addScalar("nmPrsnMatchLstDfps")
				.addScalar("idPrsnImpact", StandardBasicTypes.LONG)
				.addScalar("prsnMatchScore", StandardBasicTypes.INTEGER)
				.setParameter("HI_GROUP_ID", hipGroupDtlReq.getIdHipGroup())
				.setResultTransformer(Transformers.aliasToBean(HipPersonDto.class)).list();

		if (TypeConvUtil.isNullOrEmpty(personList)) {
			throw new DataNotFoundException("No Groups found");
		}
		personList.forEach(o -> o.setPersonAddress(populateAddrDto(o.getIdHipPerson())));
		return personList;
	}

	/**
	 * This method is to update the HIP group sent to SWI, as we should not send
	 * the same record again
	 */
	@Override
	public void updtHipGroup(HipGroupDtlReq hipGroupDtlReq) {
		sessionFactory.getCurrentSession().update(createHipGrp(hipGroupDtlReq));
	}

	/*
	 * This service is to get all HIP records for FINDRS to match and non match
	 * on the screen, this service is for IMPACT
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HipFileDtlDto> getHipFindrsRecords() {
		List<HipFileDtlDto> fileDtlList = new ArrayList<>();
		fileDtlList = (List<HipFileDtlDto>) sessionFactory.getCurrentSession().createSQLQuery(getHipFindrsFileDtls)
				.addScalar("idHipFile", StandardBasicTypes.LONG).addScalar("idHipFileDetail", StandardBasicTypes.LONG)
				.addScalar("indRmtch").addScalar("cdMtchd").addScalar("dtBirthRcrdEntrd").addScalar("cdFindrsDtrmntn")
				.setResultTransformer(Transformers.aliasToBean(HipFileDtlDto.class)).list();

		if (TypeConvUtil.isNullOrEmpty(fileDtlList)) {
			throw new DataNotFoundException("No Groups found");
		}
		fileDtlList.forEach(o -> o.setPersonList(populatePersonListDto(o.getIdHipFileDetail())));
		return fileDtlList;
	}

	/**
	 * This method is to create transient HIP_GROUP entity
	 */
	private HipGroup createHipGrp(HipGroupDtlReq hipGroupDtlReq) {
		HipGroup hipGroup = (HipGroup) sessionFactory.getCurrentSession().load(HipGroup.class,
				hipGroupDtlReq.getIdHipGroup());
		hipGroup.setIndSentToSwi(ServiceConstants.STRING_IND_Y);
		hipGroup.setDtLastUpdate(new Date());
		hipGroup.setIdLastUpdatePerson(Long.valueOf(hipGroupDtlReq.getUserId()));
		return hipGroup;
	}

	/**
	 * This method is to update match and non match in HIP tables , when FINDRS
	 * team does their update on the screen.
	 */
	private HipAddressDto populateAddrDto(Long idPerson) {
		HipAddress personAddress = (HipAddress) sessionFactory.getCurrentSession().createCriteria(HipAddress.class)
				.createAlias("idHipPerson", "a").add(Restrictions.eq("a.idHipPerson", idPerson)).uniqueResult();
		HipAddressDto hipAddressDto = new HipAddressDto();
		BeanUtils.copyProperties(personAddress, hipAddressDto);
		return hipAddressDto;
	}

	/**
	 * This method is to populate HipPersonDto for the given idHipFileDetail
	 */
	@SuppressWarnings("unchecked")
	private List<HipPersonDto> populatePersonListDto(Long idHipFileDetail) {
		HipFileDetail hipFileDetail = (HipFileDetail) sessionFactory.getCurrentSession().load(HipFileDetail.class,
				idHipFileDetail);
		List<HipPerson> personList = (List<HipPerson>) (sessionFactory.getCurrentSession()
				.createQuery(getHipPersonRecord).setParameter("idHipFileDetail", hipFileDetail)).list();
		List<HipPersonDto> personDtoList = new ArrayList<>();
		personList.forEach(o -> {
			HipPersonDto personDto = new HipPersonDto();
			BeanUtils.copyProperties(o, personDto);
			if (!ObjectUtils.isEmpty(o.getTxtPrsnMatchStat())) {
				personDto.setPrsnMatchStat(o.getTxtPrsnMatchStat());
			}
			if (!ObjectUtils.isEmpty(o.getNbrPrsnPlrlty())) {
				personDto.setPrsnPlrlty(o.getNbrPrsnPlrlty());
			}

			if (!ObjectUtils.isEmpty(o.getTxtPrsnMatchSsnDfps())) {
				personDto.setPrsnMatchSsnDfps(o.getTxtPrsnMatchSsnDfps());
			}
			if (!ObjectUtils.isEmpty(o.getNbrPrsnMatchClass())) {
				personDto.setPrsnMatchClass(o.getNbrPrsnMatchClass());
			}

			if (!ObjectUtils.isEmpty(o.getNbrPrsnRcrdTyp())) {
				personDto.setPrsnRcrdTyp(o.getNbrPrsnRcrdTyp());
			}

			if (!ObjectUtils.isEmpty(o.getNbrPrsnMatchScore())) {
				personDto.setPrsnMatchScore(o.getNbrPrsnMatchScore());
			}

			personDto.setPersonAddress(populateAddrDto(o.getIdHipPerson()));
			personDtoList.add(personDto);
		});
		return personDtoList;
	}

	/**
	 * This method is to update match and non match in HIP tables , when FINDRS
	 * team does their update on the screen.
	 */
	@Override
	public void updtHipFindrsRecords(UpdtRecordsReq updtRecordsReq) {
		updtRecordsReq.getUpdatedRecords().forEach(o -> {
			HipFileDetail hipFileDetail = (HipFileDetail) sessionFactory.getCurrentSession().load(HipFileDetail.class,
					o.getIdHipFileDetail());
			hipFileDetail.setDtLastUpdate(new Date());
			hipFileDetail.setCdFindrsDtrmntn(o.getCdFindrsDtrmntn());
			hipFileDetail.setIdLastUpdatePerson(Long.valueOf(updtRecordsReq.getUserId()));
			sessionFactory.getCurrentSession().update(hipFileDetail);
		});
	}

	/**
	 * This method is check all the records in the file are successfully
	 * processed , if processed need to pruge the non match records
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UpdtRecordsRes chkFileComp(UpdtRecordsReq updtRecordsReq) {
		Long idFile = updtRecordsReq.getUpdatedRecords().stream().findAny().orElse(null).getIdHipFile();
		Long totalRecCount = (Long) sessionFactory.getCurrentSession().createCriteria(HipFileDetail.class)
				.createAlias("idHipFile", "a").add(Restrictions.eq("a.idHipFile", idFile))
				.setProjection(Projections.rowCount()).uniqueResult();

		List<UpdtRecordDto> list = (List<UpdtRecordDto>) sessionFactory.getCurrentSession().createSQLQuery(chkFileComp)
				.addScalar("cdMatchCount", StandardBasicTypes.INTEGER).addScalar("cdFindrsDtrmntn")
				.setParameter("HI_idFile", idFile).setResultTransformer(Transformers.aliasToBean(UpdtRecordDto.class))
				.list();

		Date fileUploadDate = (Date) sessionFactory.getCurrentSession().createCriteria(HipFile.class)
				.setProjection(Projections.projectionList().add(Projections.property("dtRcvd"), "dtRcvd"))
				.add(Restrictions.eq("idHipFile", idFile)).uniqueResult();

		UpdtRecordsRes updtRecordsRes = new UpdtRecordsRes();
		updtRecordsRes.setFileUploadDate(fileUploadDate);
		Integer tot = totalRecCount.intValue();
		updtRecordsRes.setTotalRecords(tot);

		Integer totMatch = 0;
		Integer totNonMatch = 0;
		if (list.stream().filter(o -> ServiceConstants.Match.equals(o.getCdFindrsDtrmntn())).count() > 0)
			totMatch = (int) list.stream().filter(o -> ServiceConstants.Match.equals(o.getCdFindrsDtrmntn()))
					.findFirst().orElse(null).getCdMatchCount();
		if (list.stream().filter(o -> ServiceConstants.NoMatch.equals(o.getCdFindrsDtrmntn())).count() > 0)
			totNonMatch = (int) list.stream().filter(o -> ServiceConstants.NoMatch.equals(o.getCdFindrsDtrmntn()))
					.findFirst().orElse(null).getCdMatchCount();
		updtRecordsRes.setMatchedRecords(totMatch);
		updtRecordsRes.setNonMatchedRecords(totNonMatch);
		Integer totdeterminedRecs = totMatch + totNonMatch;
		if (totdeterminedRecs.intValue() == updtRecordsRes.getTotalRecords().intValue()) {
			updtRecordsRes.setIndFileProcComp(Boolean.TRUE);
			purgeRecrods(updtRecordsReq);
		} else
			updtRecordsRes.setIndFileProcComp(Boolean.FALSE);
		return updtRecordsRes;

	}

	/**
	 * This method is to pruge the non match records
	 */
	private void purgeRecrods(UpdtRecordsReq updtRecordsReq) {

		Long idFile = updtRecordsReq.getUpdatedRecords().stream().findAny().orElse(null).getIdHipFile();

		@SuppressWarnings("unchecked")
		List<HipFileDetail> delFileDtl = (List<HipFileDetail>) sessionFactory.getCurrentSession()
				.createCriteria(HipFileDetail.class).createAlias("idHipFile", "a")
				.add(Restrictions.eq("a.idHipFile", idFile))
				.add(Restrictions.eq("cdFindrsDtrmntn", ServiceConstants.NoMatch)).list();
		delFileDtl.forEach(o -> sessionFactory.getCurrentSession().delete(o));

		HipFile hipFile = (HipFile) sessionFactory.getCurrentSession().load(HipFile.class, idFile);
		hipFile.setIdLastUpdatePerson(Long.valueOf(updtRecordsReq.getUserId()));
		hipFile.setDtLastUpdate(new Date());
		hipFile.setIndFileProcsd(ServiceConstants.STRING_IND_Y);
		sessionFactory.getCurrentSession().update(hipFile);
	}

}
