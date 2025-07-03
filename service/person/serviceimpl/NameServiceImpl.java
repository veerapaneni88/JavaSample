package us.tx.state.dfps.service.person.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.person.service.NameService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN04S Class
 * Description:Operations for Name Apr 14, 2017 - 11:16:39 AM
 */
@Service
@Transactional
public class NameServiceImpl implements NameService {

	/**
	 * 
	 * Method Description:getEmpNameDto
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public EmpNameDto getEmpNameDto(Name name) {
		EmpNameDto empDto = new EmpNameDto();
		if (name != null) {
			if (name.getCdNameSuffix() != null) {
				empDto.setCdNameSuffix(name.getCdNameSuffix());
			}
			if (name.getDtNameEndDate() != null) {
				empDto.setDtNameEnd(name.getDtNameEndDate());
			}
			if (name.getDtNameStartDate() != null) {
				empDto.setDtNameStart(name.getDtNameStartDate());
			}
			if (name.getIdName() != null) {
				empDto.setIdName(name.getIdName());
			}
			if (name.getPerson() != null) {
				if (name.getPerson().getIdPerson() != null) {
					empDto.setIdPerson(name.getPerson().getIdPerson());
				}
			}
			if (name.getIndNameInvalid() != null) {
				empDto.setIndNameInvalid(name.getIndNameInvalid());
			}
			if (name.getIndNamePrimary() != null) {
				empDto.setIndNamePrimary(name.getIndNamePrimary());
			}
			if (name.getNmNameFirst() != null) {
				empDto.setNmNameFirst(name.getNmNameFirst());
			}
			if (name.getNmNameLast() != null) {
				empDto.setNmNameLast(name.getNmNameLast());
			}
			if (name.getNmNameMiddle() != null) {
				empDto.setNmNameMiddle(name.getNmNameMiddle());
			}
			if (name.getDtLastUpdate() != null) {
				empDto.setTsLastUpdate(name.getDtLastUpdate());
			}
		}
		return empDto;
	}
}
