import { FormGroup } from '@angular/forms';

export class ReconcileTransactionValidators{
    static validateAccountNo(){
        return (group: FormGroup):{[key:string]:any}=>{
        const accountNo = group.controls.accountNo;
        if(!accountNo.value){
            group.controls.accountNo.setErrors({accountNumber:{accountNo:true }});
        }
            return null;
        }
    }

   

}