package us.tx.state.dfps.service.fce.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.response.DependentCareReadRes;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.fce.DepCareDeductionDtlDto;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.dao.DepCareDeductDao;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.service.DepCareDeductionService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implement
 * CRUD operation with DAO Layer about Deduction for Dependent Care Cost Detail
 * Feb 26, 2018- 1:09:27 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
public class DepCareDeductionServiceImpl implements DepCareDeductionService {

	@Autowired
	DepCareDeductDao depCareDeductDao;

	@Autowired
	EventService eventService;

	private static final long MAX_ELIGIBLE_AGE = 18;
	private static final long DEDUCTION_AGE_LIMIT = 2;
	private static final double MAX_DEDUCTION_AMT_UNDER_AGE_LIMIT = 200.00;
	private static final double MAX_DEDUCTION_AMT_ABOVE_AGE_LIMIT = 175.00;
	private static final String APPROVED_EVENT = "APRV";
	private static final String COMPLETE_EVENT = "COMP";
	private static final String PENDING_EVENT = "PEND";

	/**
	 * Method Name:read Method Description: Implement read operation for
	 * Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DependentCareReadRes readDepCare(DependentCareReadReq dependentCareReadReq) {
		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		DepCareDeductionDtlDto depCareDeductionDtlDto = new DepCareDeductionDtlDto();
		// set Adult Drop down Option Values
		depCareDeductionDtlDto.setValidPersonsAsAdultOptions(getValidPersonsAsAdultOptions(dependentCareReadReq));
		// set Dependent Drop down Values
		depCareDeductionDtlDto
				.setValidPersonsAsDependentOptions(getValidPersonsAsDependentOptions(dependentCareReadReq));
		// get the Fce Dependent Care Deduct record / value bean
		depCareDeductionDtlDto.setFceDepCareDeductDto(getFceDepCareDeduct(dependentCareReadReq));
		dependentCareReadRes.setDepCareDeductionDtlDto(depCareDeductionDtlDto);
		return dependentCareReadRes;
	}

	/**
	 * Method Name:save Method Description: Implement save operation for
	 * Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DependentCareReadRes save(DependentCareReadReq dependentCareReadReq) {

		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		FceDepCareDeductDto fceDepCareDeductDto = dependentCareReadReq.getFceDepCareDeductDto();
		if (!ObjectUtils.isEmpty(fceDepCareDeductDto)) {
			Long idFceDepCareDeduct = fceDepCareDeductDto.getIdFceDepCareDeduct();
			if (null == idFceDepCareDeduct || (long) idFceDepCareDeduct == 0L) {
				insertFceDepCareDeduct(dependentCareReadReq);
			} else {
				updateFceDepCareDeductInvalid(dependentCareReadReq);
			}
		}
		dependentCareReadRes.setTaskCompleted(Boolean.TRUE);
		return dependentCareReadRes;
	}

	/**
	 * Method Name: insertFceDepCareDeduct Method Description: Implement add
	 * operation for Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @
	 */
	private void insertFceDepCareDeduct(DependentCareReadReq dependentCareReadReq) {
		List<FcePersonDto> fcePrincipalAge = (List<FcePersonDto>) depCareDeductDao
				.getFcePrincipalsAge(dependentCareReadReq);

		for (FcePersonDto fcePrincipal : fcePrincipalAge) {
			Long idFcePerson = fcePrincipal.getIdFcePerson();
			Long idFceDependentPerson = dependentCareReadReq.getFceDepCareDeductDto().getIdFceDependentPerson();
			if (null != idFcePerson && null != idFceDependentPerson) {
				if (idFcePerson.equals(idFceDependentPerson)) {
					dependentCareReadReq.getFceDepCareDeductDto().setNbrDependentAge(fcePrincipal.getNbrAge());
				}
			}
		}

		// evaluate the Dependent Age and Deduction amount for the FCE record
		Double allotedDeductions = depCareDeductDao.getDepDeductionSum(dependentCareReadReq);
		FceDepCareDeductDto fceDepCareDeductDto = dependentCareReadReq.getFceDepCareDeductDto();
		fceDepCareDeductDto = evalDepDeductionAmt(fceDepCareDeductDto, allotedDeductions);
		// call the Dao method to insert the record
		depCareDeductDao.insertFceDepCareDeduct(dependentCareReadReq.getFceDepCareDeductDto());
	}

	/**
	 * This method calculates the deduction that can be allocated to a
	 * dependent's deduction record
	 * 
	 * @param fceDepCareDeductDto
	 * @param allotedDeductions
	 * @return fceDepCareDeductDto
	 */
	private FceDepCareDeductDto evalDepDeductionAmt(FceDepCareDeductDto fceDepCareDeductDto, Double allotedDeductions) {
		Double remainingDeduction = 0.0;
		Double amtDeduction = 0.0;

		// if above 2yr a max of 175 can be allocated otherwise 200
		Short nbrDependentAge = fceDepCareDeductDto.getNbrDependentAge();
		short dependentAge = 0;
		if (null != nbrDependentAge) {
			dependentAge = nbrDependentAge;
		}
		remainingDeduction = dependentAge < DEDUCTION_AGE_LIMIT
				? MAX_DEDUCTION_AMT_UNDER_AGE_LIMIT - (double) allotedDeductions
				: MAX_DEDUCTION_AMT_ABOVE_AGE_LIMIT - (double) allotedDeductions;
		if ((double) remainingDeduction < 0.0) {
			remainingDeduction = 0.0;
		}

		// if record actual cost > remaining cost then assign remaining
		// deduction
		amtDeduction = fceDepCareDeductDto.getAmtActCost().doubleValue() > (double) remainingDeduction
				? remainingDeduction : fceDepCareDeductDto.getAmtActCost().doubleValue();
		fceDepCareDeductDto.setAmtDeduction(new BigDecimal(amtDeduction));
		return fceDepCareDeductDto;
	}

	/**
	 * Method Name: updateFceDepCareDeductInvalid Method Description: Implement
	 * update operation for Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @
	 */
	private void updateFceDepCareDeductInvalid(DependentCareReadReq dependentCareReadReq) {
		depCareDeductDao.updateFceDepCareDeductInvalid(dependentCareReadReq.getFceDepCareDeductDto());
		if (dependentCareReadReq.getFceDepCareDeductDto().getIndInvalid()) {
			reAllocateDeductionAmt(dependentCareReadReq);
		}
	}

	/**
	 * Method Name: reAllocateDeductionAmt Method Description: Reallocate
	 * Deduction Amount for Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @
	 */
	private void reAllocateDeductionAmt(DependentCareReadReq dependentCareReadReq) {
		DependentCareReadRes dependentCareReadRes = depCareDeductDao.getValidDependents(
				dependentCareReadReq.getIdFceEligiblity(), dependentCareReadReq.getIdFceDependentPerson());
		FceDepCareDeductDto fceDepCareDeductDto = null;
		double allotedDeductions = 0.00;
		for (int i = 0; i < dependentCareReadRes.getFceDepCrDeductDto().size(); i++) {
			fceDepCareDeductDto = dependentCareReadRes.getFceDepCrDeductDto().get(i);
			fceDepCareDeductDto = evalDepDeductionAmt(fceDepCareDeductDto, allotedDeductions);
			depCareDeductDao.updateInvalidDependentInfo(fceDepCareDeductDto);
			allotedDeductions += fceDepCareDeductDto.getAmtDeduction().doubleValue();
		}
	}

	/**
	 * Method Name: getValidAdultDependent Method Description: Get valid
	 * Adult/Dependent information for Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DependentCareReadRes getValidAdultDependent(DependentCareReadReq dependentCareReadReq) {

		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		FceDepCareDeductDto fceDepCareDeductDto = new FceDepCareDeductDto();
		List<FceDepCareDeductDto> fceDepCareDeductDtoList = new ArrayList<FceDepCareDeductDto>();
		fceDepCareDeductDtoList = depCareDeductDao.getValidAdultDependent(dependentCareReadReq);
		Iterator<FceDepCareDeductDto> iterator = fceDepCareDeductDtoList.iterator();
		while (iterator.hasNext()) {
			fceDepCareDeductDto = iterator.next();
		}
		dependentCareReadRes.setFceDepCareDeductDto(fceDepCareDeductDto);
		return dependentCareReadRes;
	}

	/**
	 * Method Name: syncDepCareDeductions Method Description: Synchronize the
	 * information for Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DependentCareReadRes syncDepCareDeductions(DependentCareReadReq dependentCareReadReq) {

		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		String eventStatus = eventService.getEvent(dependentCareReadReq.getIdEvent()).getCdEventStatus();
		if (APPROVED_EVENT.equals(eventStatus)) {
			return dependentCareReadRes;
		}

		Boolean eligSpecMode = Boolean.FALSE;
		if (PENDING_EVENT.equals(eventStatus) || COMPLETE_EVENT.equals(eventStatus)) {
			eligSpecMode = Boolean.TRUE;
		}

		dependentCareReadReq.setEligSpecMode(eligSpecMode);
		List<FcePersonDto> fcePersonDtoList = depCareDeductDao.getFcePrincipalsAge(dependentCareReadReq);
		HashMap<Long, Long> fcePrincipalsAgeMap = new HashMap<Long, Long>();
		for (FcePersonDto fcePersonDto : fcePersonDtoList) {
			fcePrincipalsAgeMap.put(fcePersonDto.getIdFcePerson(),
					(ObjectUtils.isEmpty(fcePersonDto.getNbrAge()) ? 0L : Long.valueOf(fcePersonDto.getNbrAge())));
		}

		// if principals should be considered for re calculation and none exist
		// return
		if (!ObjectUtils.isEmpty(dependentCareReadReq.getInvalidateWithPrincipals())
				&& dependentCareReadReq.getInvalidateWithPrincipals() && (fcePrincipalsAgeMap.size() <= 0)) {
			return dependentCareReadRes;
		}

		// make a call to the DAO to get valid deductions
		List<FceDepCareDeductDto> validDeductions = depCareDeductDao
				.findFceDepCareDeduct(dependentCareReadReq.getIdFceEligiblity(), Boolean.TRUE);

		// List of deduction records that need to be updated
		List<FceDepCareDeductDto> updateDeductions = new ArrayList<FceDepCareDeductDto>();

		// List of dependents whose deductions need to be reallocated
		HashSet<Long> depMarkedForReAlloc = new HashSet<>();
		for (FceDepCareDeductDto fceDepCareDeductDto : validDeductions) {
			// if Adult Person and Dependent Person do not exist on List of
			// Principals mark the deduction as Invalid
			if (fcePrincipalsAgeMap.containsKey(fceDepCareDeductDto.getIdFceAdultPerson())
					&& fcePrincipalsAgeMap.containsKey(fceDepCareDeductDto.getIdFceDependentPerson()))
				fceDepCareDeductDto.setIndInvalid(Boolean.FALSE);
			else
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if an Adult person on the deduction , has his or her age less
			// than 18 mark the deduction as invalid
			if (fcePrincipalsAgeMap.containsKey(fceDepCareDeductDto.getIdFceAdultPerson())
					&& ((Long) fcePrincipalsAgeMap.get(fceDepCareDeductDto.getIdFceAdultPerson()))
							.longValue() < MAX_ELIGIBLE_AGE)
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if both adult and dependent are same caused by a person merge
			if (fceDepCareDeductDto.getIdFceAdultPerson().equals(fceDepCareDeductDto.getIdFceDependentPerson()))
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if more than one record exists for the same adult and dependent ,
			// mark as invalid, this only
			// happens due to person merge
			if (depCareDeductDao.isAdultDependentDup(dependentCareReadReq.getIdFceEligiblity(),
					fceDepCareDeductDto.getIdFceAdultPerson(), fceDepCareDeductDto.getIdFceDependentPerson()))
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if adult exist as dependent in any records , mark as invalid,
			// this only happens due to person merge
			if (depCareDeductDao.isAdultInDependentColumn(dependentCareReadReq.getIdFceEligiblity(),
					fceDepCareDeductDto.getIdFceAdultPerson()))
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if dependent exist as adult in any records , mark as invalid
			// ,this only happens due to person merge
			if (depCareDeductDao.isDependentInAdultColumn(dependentCareReadReq.getIdFceEligiblity(),
					fceDepCareDeductDto.getIdFceDependentPerson()))
				fceDepCareDeductDto.setIndInvalid(Boolean.TRUE);

			// if records is invalid add the dependent for reallocation
			if (fceDepCareDeductDto.getIndInvalid())
				depMarkedForReAlloc.add(fceDepCareDeductDto.getIdFceDependentPerson());

			// if the record is valid but age changed such that it went above or
			// below the AFDC Age Limits
			// add dependent for deduction reallocation
			if (!fceDepCareDeductDto.getIndInvalid()
					&& fcePrincipalsAgeMap.containsKey(fceDepCareDeductDto.getIdFceDependentPerson())) {
				long newDepAge = ((Long) fcePrincipalsAgeMap.get(fceDepCareDeductDto.getIdFceDependentPerson()))
						.longValue();
				long oldDepAge = fceDepCareDeductDto.getNbrDependentAge();
				if (newDepAge != oldDepAge)
					fceDepCareDeductDto.setNbrDependentAge((short) newDepAge);

				if ((newDepAge >= DEDUCTION_AGE_LIMIT) && (oldDepAge < DEDUCTION_AGE_LIMIT))
					depMarkedForReAlloc.add(fceDepCareDeductDto.getIdFceDependentPerson());

				if ((newDepAge < DEDUCTION_AGE_LIMIT) && (oldDepAge >= DEDUCTION_AGE_LIMIT))
					depMarkedForReAlloc.add(fceDepCareDeductDto.getIdFceDependentPerson());
			}
			updateDeductions.add(fceDepCareDeductDto);
			for (int i = 0; i < updateDeductions.size(); i++) {
				// call Dao to update valid/invalid deduction,Dependent Age and
				// Deduction Amount
				depCareDeductDao.updateInvalidDependentInfo((FceDepCareDeductDto) updateDeductions.get(i));
			}

			DependentCareReadReq requestForReAllocate = new DependentCareReadReq();
			requestForReAllocate.setIdFceEligiblity(dependentCareReadReq.getIdFceEligiblity());
			for (Iterator<?> depMarkedForReAllocIterator = depMarkedForReAlloc.iterator(); depMarkedForReAllocIterator
					.hasNext();) {
				requestForReAllocate.setIdFceDependentPerson((Long) depMarkedForReAllocIterator.next());
				reAllocateDeductionAmt(requestForReAllocate);
			}
		}
		dependentCareReadRes.setTaskCompleted(Boolean.TRUE);
		return dependentCareReadRes;
	}

	/**
	 * Method Name: getValidPersonsAsAdultOptions Method Description: get adult
	 * list as options
	 * 
	 * @param dependentCareReadReq
	 * @return fcePersonDtoList @
	 */
	public List<FcePersonDto> getValidPersonsAsAdultOptions(DependentCareReadReq dependentCareReadReq) {

		List<FcePersonDto> fcePersonDtoList = new ArrayList<FcePersonDto>();
		List<FcePersonDto> validPersonsAsAdults = depCareDeductDao.getValidPersonsAsAdults(dependentCareReadReq);

		for (FcePersonDto validPersonsAdults : validPersonsAsAdults) {
			// add to List if adult only(age greater that 18)
			if (!ObjectUtils.isEmpty(validPersonsAdults.getNbrAge())
					&& (short) validPersonsAdults.getNbrAge() >= (short) MAX_ELIGIBLE_AGE) {
				fcePersonDtoList.add(validPersonsAdults);
			}
		}
		return fcePersonDtoList;
	}

	/**
	 * Method Name: getValidPersonsAsDependentOptions Method Description: get
	 * dependent list as options
	 * 
	 * @param dependentCareReadReq
	 * @return validPersonsAsDependents @
	 */
	public List<FcePersonDto> getValidPersonsAsDependentOptions(DependentCareReadReq dependentCareReadReq) {

		List<FcePersonDto> validPersonsAsDependents = depCareDeductDao
				.getValidPersonsAsDependents(dependentCareReadReq);
		return validPersonsAsDependents;
	}

	/**
	 * Method Name: getFceDepCareDeduct Method Description: Get Dependent Care
	 * Deduction
	 * 
	 * @param dependentCareReadReq
	 * @return fceDepCareDeduct @
	 */
	private FceDepCareDeductDto getFceDepCareDeduct(DependentCareReadReq dependentCareReadReq) {
		FceDepCareDeductDto fceDepCareDeduct = depCareDeductDao.getFceDepCareDeduct(dependentCareReadReq);
		return fceDepCareDeduct;
	}
}
