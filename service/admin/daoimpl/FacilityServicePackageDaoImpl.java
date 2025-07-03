package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.FacilityServicePackage;
import us.tx.state.dfps.common.domain.FacilityServicePackageDetail;
import us.tx.state.dfps.service.admin.dao.FacilityServicePackageDao;
import us.tx.state.dfps.service.admin.dto.FacilityServicePackageDetailDto;
import us.tx.state.dfps.service.admin.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;

@Repository
public class FacilityServicePackageDaoImpl implements FacilityServicePackageDao {

    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_HOLD = "HLD";

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${FacilityServiceTypeDaoImpl.getFacilitySvcPkgList}")
    private String getFacilitySvcPkgList;

    @Value("${FacilityServiceTypeDaoImpl.getFacilityServicePackageSql}")
    private String getFacilityServicePackageSql;


    @Value("${FacilityServiceTypeDaoImpl.getFacilityServicePackageDetailSql}")
    private String getFacilityServicePackageDetailSql;

    @Override
    public List<FacilityServicePackageDto> getFacilityServicePackageDtoList(long idResource) {
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilitySvcPkgList)
                .addScalar("idFcltySvcPkg", StandardBasicTypes.LONG)
                .addScalar("dtEffective", StandardBasicTypes.DATE)
                .addScalar("dtEnd", StandardBasicTypes.DATE)
                .addScalar("holdFcltySvcCodes", StandardBasicTypes.STRING)
                .addScalar("activeFcltySvcCodes", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(FacilityServicePackageDto.class)));
        sQLQuery1.setParameter("idResource", idResource);
        List<FacilityServicePackageDto> facilityServicePackageDtoList = (List<FacilityServicePackageDto>) sQLQuery1.list();
        List<FacilityServicePackageDto> finalList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(facilityServicePackageDtoList)) {
            long prevIdFcltySvcPkg = 0;
            FacilityServicePackageDto prevFcltySvcPkgDto = null;
            for (FacilityServicePackageDto fcltySvcPkg : facilityServicePackageDtoList) {
                if (prevIdFcltySvcPkg != fcltySvcPkg.getIdFcltySvcPkg()) {
                    if (prevFcltySvcPkgDto != null) {
                        finalList.add(prevFcltySvcPkgDto);
                    }
                    prevFcltySvcPkgDto = fcltySvcPkg;
                    prevIdFcltySvcPkg = fcltySvcPkg.getIdFcltySvcPkg();
                } else {
                    if (fcltySvcPkg.getHoldFcltySvcCodes() != null) {
                        prevFcltySvcPkgDto.setHoldFcltySvcCodes(fcltySvcPkg.getHoldFcltySvcCodes());
                    } else if (fcltySvcPkg.getActiveFcltySvcCodes() != null) {
                        prevFcltySvcPkgDto.setActiveFcltySvcCodes(fcltySvcPkg.getActiveFcltySvcCodes());
                    }
                }

            }
            finalList.add(prevFcltySvcPkgDto);
        }
        return finalList;
    }

    /**
     * This method will check if the selected Service Package Cd exists in FCLTY_SVC_PKG and FCLTY_SVC_PKG_DETAIL
     * tables, and inserts record into them if it doesn't exist.
     * @param resourceId
     * @param svcPkgDtlDto
     * @param dtPlacementStart
     * @param userId
     */
    public void addFcltySvcPkgCredentails(Long resourceId, ServicePackageDtlDto svcPkgDtlDto, Date dtPlacementStart, Long userId) {
        CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class, resourceId);
        HashMap<Long, List<FacilityServicePackageDetailDto>> fcltySvcPkgDtlHashMap = new HashMap<Long, List<FacilityServicePackageDetailDto>>();
        //Fetch all FacilitySvcPkg records sorted by Effective date Asc.
        List<FacilityServicePackageDto> allFcltySvcPkgDtoList = fetchAllFacilitySvcPackages(resourceId);

        if (CollectionUtils.isEmpty(allFcltySvcPkgDtoList)) {
            //Insert into FSP, FSPDetail
            long idFsp = insertIntoFsp(capsResource, dtPlacementStart, null, userId);
            insertIntoFspDetail(idFsp, svcPkgDtlDto, userId);
        } else {
            FacilityServicePackageDto firstFsp = allFcltySvcPkgDtoList.get(0);
            FacilityServicePackageDto lasstFsp = allFcltySvcPkgDtoList.get(allFcltySvcPkgDtoList.size() - 1);

            if (DateUtils.isBefore(dtPlacementStart, firstFsp.getDtEffective()) ||
                    (lasstFsp.getDtEnd() != null && DateUtils.isAfter(dtPlacementStart, lasstFsp.getDtEnd()))) {
                //Insert into FSP, FSPDetail.
                Date dtEnd = DateUtils.isBefore(dtPlacementStart, firstFsp.getDtEffective()) ?
                        DateUtils.addToDate(firstFsp.getDtEffective(), 0, 0, -1) : null;
                long idFsp = insertIntoFsp(capsResource, dtPlacementStart, dtEnd, userId);
                insertIntoFspDetail(idFsp, svcPkgDtlDto, userId);
            } else {
                //Get the FacilitySvcPkgDetail records for each FacilitySvcPkg and put them in a hashmap.
                for (FacilityServicePackageDto fsp : allFcltySvcPkgDtoList) {
                    fcltySvcPkgDtlHashMap.put(fsp.getIdFcltySvcPkg(), fetchFacilitySvcPackageDetails(fsp.getIdFcltySvcPkg()));
                }
                boolean isFspMatchFound = false;
                for (FacilityServicePackageDto fsp : allFcltySvcPkgDtoList) {
                    if ((DateUtils.isAfter(dtPlacementStart, fsp.getDtEffective()) || dtPlacementStart.equals(fsp.getDtEffective()))
                            && (fsp.getDtEnd() == null ||
                            (DateUtils.isAfter(fsp.getDtEnd(), dtPlacementStart) || dtPlacementStart.equals(fsp.getDtEnd())))) {
                        isFspMatchFound = true;
                    }
                    if (isFspMatchFound) {
                        //We found a match for active FacilitySvcPkg record. We need to insert a child record into FacilitySvcPkgDetail table (if it already doesn't exist)
                        //for matching record, and for any subsequent FacilitySvcPkg records.
                        checkAndInsertIntoFspDtl(fsp, fcltySvcPkgDtlHashMap, svcPkgDtlDto, userId);
                    }
                }
            }
        }
    }

    /**
     * This method will check if record exists in FacilitySvcPkgDetail table for the selected service package code.
     * If the record doesn't exist, it will insert a new record.
     * @param fsp
     * @param fcltySvcPkgDtlHashMap
     * @param svcPkgDtlDto
     * @param userId
     */
    private void checkAndInsertIntoFspDtl(FacilityServicePackageDto fsp, HashMap<Long, List<FacilityServicePackageDetailDto>> fcltySvcPkgDtlHashMap,
                                          ServicePackageDtlDto svcPkgDtlDto, Long userId) {
        long idFsp = fsp.getIdFcltySvcPkg();
        List<FacilityServicePackageDetailDto> fspDetaildtoList = fcltySvcPkgDtlHashMap.get(idFsp);
        boolean matchFound = false;
        if (!ObjectUtils.isEmpty(fspDetaildtoList)) {
            Optional<FacilityServicePackageDetailDto> matchFspd = fspDetaildtoList.stream().filter(fspd -> fspd.getCdFcltySvc().equals(svcPkgDtlDto.getSvcPkgCd())).findFirst();
            if (matchFspd.isPresent()) {
                if(ServiceConstants.STATUS_HLD.equals(matchFspd.get().getCdStatus())) {
                    updateFspDetail(matchFspd.get().getIdFcltySvcPkgDtl());
                }
                matchFound = true;
            }
        }
        if (!matchFound) {
            //No FSP Detail record found for the SvcPkgCd.
            insertIntoFspDetail(fsp.getIdFcltySvcPkg(), svcPkgDtlDto, userId);
        }
    }

    /**
     * This method will set the status to "ACT" and update the FacilitySvcPkgDetail record
     * @param idFcltySvcPkgDtl
     */
    private void updateFspDetail(long idFcltySvcPkgDtl) {
        FacilityServicePackageDetail fspd = (FacilityServicePackageDetail) sessionFactory.getCurrentSession().
                get(FacilityServicePackageDetail.class, idFcltySvcPkgDtl);
        fspd.setCdStatus(ServiceConstants.CONTRACT_STATUS_ACTIVE);
        sessionFactory.getCurrentSession().update(fspd);
    }

    /**
     * This method will insert record into FacilitySvcPkg table.
     * @param capsResource
     * @param dtPlacementStart
     * @param dtEnd
     * @param userId
     * @return
     */
    private long insertIntoFsp(CapsResource capsResource, Date dtPlacementStart, Date dtEnd, Long userId) {
        FacilityServicePackage fspAdd = new FacilityServicePackage();
        fspAdd.setCapsResource(capsResource);
        fspAdd.setDtEffective(dtPlacementStart);
        fspAdd.setDtEnd(dtEnd);
        fspAdd.setDtLastUpdate(new Date());
        fspAdd.setIdLastUpdatePerson(userId);
        fspAdd.setIdCreatedPerson(userId);
        fspAdd.setDtCreated(new Date());
        long idFSP = (Long) sessionFactory.getCurrentSession().save(fspAdd);
        return idFSP;
    }


    /**
     * This method will insert record into FacilitySvcPkgDetail table.
     * @param idFsp
     * @param svcPkgDtlDto
     * @param userId
     */
    private void insertIntoFspDetail(long idFsp, ServicePackageDtlDto svcPkgDtlDto, Long userId) {
        FacilityServicePackageDetail fspd = new FacilityServicePackageDetail();
        fspd.setIdFcltySvcPkg(idFsp);
        fspd.setCdStatus(ServiceConstants.CONTRACT_STATUS_ACTIVE);
        fspd.setCdFcltySvc(svcPkgDtlDto.getSvcPkgCd());
        fspd.setIdFcltySvcPkg(idFsp);
        fspd.setIdLastUpdatePerson(userId);
        fspd.setIdCreatedPerson(userId);
        fspd.setDtCreated(new Date());
        fspd.setDtLastUpdate(new Date());
        sessionFactory.getCurrentSession().save(fspd);
    }

    /**
     * This method will get all the FacilitySvcPkg records for the given resource sorted by Effective Dt ascending.
     * @param resourceId
     * @return
     */
    private List<FacilityServicePackageDto> fetchAllFacilitySvcPackages(Long resourceId) {
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityServicePackageSql)
                .addScalar("idFcltySvcPkg", StandardBasicTypes.LONG)
                .addScalar("dtEffective", StandardBasicTypes.TIMESTAMP)
                .addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
                .setResultTransformer(Transformers.aliasToBean(FacilityServicePackageDto.class)));
        sQLQuery.setParameter("idResource", resourceId);
        List<FacilityServicePackageDto> allFcltySvcPkgDtoList = (List<FacilityServicePackageDto>) sQLQuery.list();
        return allFcltySvcPkgDtoList;
    }

    /**
     * This method will get all the FacilitySvcPkgDetail records for the given idFcltySvcPkg.
     * @param idFcltySvcPkg
     * @return
     */
    private List<FacilityServicePackageDetailDto> fetchFacilitySvcPackageDetails(Long idFcltySvcPkg) {
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFacilityServicePackageDetailSql)
                .addScalar("idFcltySvcPkgDtl", StandardBasicTypes.LONG)
                .addScalar("cdFcltySvc", StandardBasicTypes.STRING)
                .addScalar("cdStatus", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(FacilityServicePackageDetailDto.class)));
        sQLQuery.setParameter("idFcltySvcPkg", idFcltySvcPkg);
        List<FacilityServicePackageDetailDto> facilityServicePackageDetailDtos = (List<FacilityServicePackageDetailDto>) sQLQuery.list();
        return facilityServicePackageDetailDtos;
    }
}
