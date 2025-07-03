package us.tx.state.dfps.service.legalnotice.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.common.request.LegalNoticeFormReq;
import us.tx.state.dfps.service.common.request.LegalNoticeReq;
import us.tx.state.dfps.service.common.request.MailDateSaveReq;
import us.tx.state.dfps.service.common.response.LegalNoticeListRes;
import us.tx.state.dfps.service.common.response.LegalNoticeRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LegalNoticePrefillData;
import us.tx.state.dfps.service.legal.dto.LegalNoticeDtlDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeFormDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeRecpntDto;
import us.tx.state.dfps.service.legalnotice.dao.LegalNoticeDao;
import us.tx.state.dfps.service.legalnotice.service.LegalNoticeService;
import us.tx.state.dfps.service.person.dao.PersonDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation for LegalNoticeService June 07, 2018- 01:34:03 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class LegalNoticeServiceImpl implements LegalNoticeService {

	@Autowired
	LegalNoticeDao legalNoticeDao;
	/**
	 * This method to get Legal notice list
	 */
	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	LegalNoticePrefillData legalNoticePrefillData;

	@Override
	public LegalNoticeListRes getLegalNoticeList(LegalNoticeReq legalNoticeReq) {
		List<LegalNoticeDtlDto> legalNoticeList = legalNoticeDao.getLegalNoticeList(legalNoticeReq.getIdCase(),
				legalNoticeReq.getIdStage());		
		LegalNoticeDtlDto noticeDtlDto = legalNoticeDao.getLegalStatusDtl(legalNoticeReq.getIdCase(),
				legalNoticeReq.getIdStage(),new LegalNoticeDtlDto());
		// when no legal status record found then pass the indicator to hide Add
		// Notice button
		boolean indHideAddBtn = true;
		//#10390 - As per ADS , if there is no Legal Action also , the user can add a legal notice
		if(!ObjectUtils.isEmpty(noticeDtlDto.getLegalStatEventId())){
			indHideAddBtn = false;
		}

		LegalNoticeListRes res = new LegalNoticeListRes();
		res.setLegalNoticeList(legalNoticeList);
		res.setIndHideAddBtn(indHideAddBtn);
		return res;
	}

	/**
	 * This method to save mail date from Legal notice List page
	 */
	@Override
	public ServiceResHeaderDto saveMailedDate(MailDateSaveReq mailDateSaveReq) {
		return legalNoticeDao.saveLegalNoticeRecpnt(mailDateSaveReq.getRecepnt(),
				Long.parseLong(mailDateSaveReq.getUserId()));
	}

	@Override
	public PreFillDataServiceDto getLegalNoticeForm(LegalNoticeFormReq legalNoticeFormReq) {

		// creating the Legal Notice Prefill Dto
		LegalNoticeFormDto legalNoticeFormDto = new LegalNoticeFormDto();

		// Retrive legal notice detail dto
		LegalNoticeDtlDto legalNoticeDtlDtoFetched = new LegalNoticeDtlDto();

		// Retrieving the Legal Notice List by passing the Case Id
		LegalNoticeReq legalNoticeReq = new LegalNoticeReq();

		List<LegalNoticeDtlDto> legalNoticeList = legalNoticeDao.getLegalNoticeList(legalNoticeFormReq.getIdCase(),
				legalNoticeFormReq.getIdStage());

		// Iterating the legal notice List and assign the LegalNoticeDtl for the
		// idLegalNoticeDtl
		if (legalNoticeList.size() > 0) {
			for (LegalNoticeDtlDto legalNoticeDtlDto : legalNoticeList) {
				if (legalNoticeDtlDto.getIdLegalNoticeDtl().equals(legalNoticeFormReq.getIdLegalNoticeDtl())) {
					legalNoticeFormDto.setLegalNoticeDtlDto(legalNoticeDtlDto);
					List<LegalNoticeRecpntDto> legalNoticeRecpntDtolist = legalNoticeDtlDto.getRecipients();
					for (LegalNoticeRecpntDto legalNoticeRecpntDto : legalNoticeRecpntDtolist) {
						if (legalNoticeRecpntDto.getIdPerson()
								.equals(legalNoticeFormReq.getIdLegalNoticeRecpnt())) {
							legalNoticeFormDto.setLegalNoticeRecpntDto(legalNoticeRecpntDto);
						}

					}
				}
			}
		}

		EmployeeDto employeeDto;
		Person person;
		// Assigning Case Worker Details
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeFormDto.getLegalNoticeDtlDto().getIdCreatedPerson())) {
			employeeDto = employeeDao
					.getEmployeeByIdUser(legalNoticeFormDto.getLegalNoticeDtlDto().getIdCreatedPerson());
			legalNoticeFormDto.setEmployeeDto(employeeDto);

			// Retrieve the Phone Number

			person = personDao.getPersonByPersonId(legalNoticeFormDto.getLegalNoticeDtlDto().getIdCreatedPerson());
			legalNoticeFormDto.setPerson(person);
		}
		legalNoticeReq.setIdCase(legalNoticeFormReq.getIdCase());
		legalNoticeReq.setIdEvent(legalNoticeFormReq.getIdEvent());
		legalNoticeReq.setIdStage(legalNoticeFormReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeReq)) {
			// Call fetch function of dao layer
			legalNoticeDtlDtoFetched = legalNoticeDao.fetchLegalNoticeDtl(legalNoticeReq.getIdCase(),
					legalNoticeReq.getIdStage());
		}

		if (!TypeConvUtil.isNullOrEmpty(legalNoticeDtlDtoFetched)) {
			legalNoticeFormDto.setLglStatusCauseNum(legalNoticeDtlDtoFetched.getLegalStatusCauseNum());
		}

		return legalNoticePrefillData.returnPrefillData(legalNoticeFormDto);

	}

	/**
	 * 
	 * Method Name: fetchLegalNoticeDtl Method Description: This method will
	 * fetch legal notice detail
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	@Override
	public LegalNoticeRes fetchLegalNoticeDtl(LegalNoticeReq legalNoticeReq) {
		LegalNoticeRes legalNoticeRes = new LegalNoticeRes();
		LegalNoticeDtlDto legalNoticeDtlDto = new LegalNoticeDtlDto();
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeReq)) {
			// Call fetch function of dao layer
			legalNoticeDtlDto = legalNoticeDao.fetchLegalNoticeDtl(legalNoticeReq.getIdCase(),
					legalNoticeReq.getIdStage());
		}
		legalNoticeRes.setLegalNoticeDtlDto(legalNoticeDtlDto);
		return legalNoticeRes;
	}

	/**
	 * 
	 * Method Name: saveLegalNoticeDtl Method Description: This method will save
	 * legal notice detail
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceResHeaderDto saveLegalNoticeDtl(LegalNoticeReq legalNoticeReq){
		ServiceResHeaderDto serviceResHeaderDto = new ServiceResHeaderDto();
		if (!TypeConvUtil.isNullOrEmpty(legalNoticeReq)) {
			LegalNoticeDtlDto legalNoticeDtlDto = legalNoticeReq.getLegalNoticeDtlDto();
			serviceResHeaderDto = legalNoticeDao.saveLegalNoticeDtl(legalNoticeDtlDto, legalNoticeReq.getIdUser(),
					legalNoticeReq.isGenerateNotice());
		}

		return serviceResHeaderDto;
	}

}
