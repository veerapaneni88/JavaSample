package us.tx.state.dfps.service.utility.aspect;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * common-exception- IMPACT PHASE 2 MODERNIZATION Class Description:Validator class added to validate the duplicate bookmark.Fixed as part of warranty defect#12004
 * Aug 21, 2017- 1:21:43 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@EnableAspectJAutoProxy
@Aspect
@Component
public class FormBookMarkValidator {

	/**
	 * 
	 * Method Name: formBMValidator Method Description: Validate the duplicate
	 * book mark identified in the prefill
	 * 
	 * @param joinPoint
	 * @throws ServiceLayerException
	 */

	@AfterReturning(pointcut = "execution(* us.tx.state.dfps.service.forms.util.*.*(..))", returning = "prefillDto")
	public void formBMValidator(Object prefillDto) {
		if (!ObjectUtils.isEmpty(prefillDto)) {
			PreFillDataServiceDto preFillDataServiceDto = (PreFillDataServiceDto) prefillDto;
			
			if (!ObjectUtils.isEmpty(preFillDataServiceDto.getBookmarkDtoList()) && preFillDataServiceDto.getBookmarkDtoList().stream()
					.collect(Collectors.groupingBy(BookmarkDto::getBookmarkName)).entrySet().stream()
					.filter(e -> e.getValue().size() > 999999).findAny().isPresent()) {
				throw new ServiceLayerException("ARC_DOCS_ERR_ENGINE_ADD_NODELIST_TO_DICTIONARY");
			}
		}
	}
	

}
