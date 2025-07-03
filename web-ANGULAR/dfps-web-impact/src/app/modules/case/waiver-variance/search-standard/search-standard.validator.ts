import { FormGroup } from '@angular/forms';

export class SearchStandardValidator {
    static validateForEmptySearchParam = (group: FormGroup): { [key: string]: any } => {
        if (!group.controls.keyword.value && !group.controls.category.value) {
            group.controls.msgErrDisplay.setErrors({ msg_One_Param_Req: { msg_One_Param_Req: true, hideFieldName: true } });
        } else {
            group.controls.msgErrDisplay.setErrors({});
        }
        group.controls.keyword.updateValueAndValidity({ onlySelf: true });
        group.controls.category.updateValueAndValidity({ onlySelf: true });
        return null;
    };
}
