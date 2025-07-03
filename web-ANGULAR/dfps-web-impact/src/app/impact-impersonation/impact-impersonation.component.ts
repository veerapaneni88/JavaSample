import { Component, OnInit } from '@angular/core';
import { ADAuthService } from 'app/auth-service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { MsalService } from '@azure/msal-angular';
import {
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils
} from 'dfps-web-lib';
import { ImpersonationValidators } from './impact-impersonation.validator';

@Component({
  selector: 'impact-impersonation',
  templateUrl: './impact-impersonation.component.html',
  styleUrls: ['./impact-impersonation.css']
})
export class ImpactImpersonationComponent extends DfpsFormValidationDirective implements OnInit {

  userForm: FormGroup;
  onPremId: any;
  errorMessage: string;

  constructor(
    private formBuilder: FormBuilder,
    private msalAuthService: MsalService,
    private authService: ADAuthService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) { super(store) }

  ngOnInit(): void {
    this.getUserInfo();
    this.createForm();
    FormUtils.disableFormControlStatus(this.userForm, ['userName']);

  }

  createForm() {
    this.userForm = this.formBuilder.group({
      userName: [this.onPremId],
      impersonationId: ['']
    }, {
      validators: [
        ImpersonationValidators.impersonationValidation,
      ]
    });
  }

  getUserInfo() {
    if (this.msalAuthService.instance) {
      const profile_data = this.msalAuthService.instance.getAllAccounts()[0];
      this.onPremId = profile_data?.idTokenClaims?.onPremisesSamAccountName;
    }
  }

  impersonateUser() {
    if (this.validateFormGroup(this.userForm)) {
      this.authService.generateDfpsOauth2Token(this.onPremId, this.userForm.get('impersonationId').value);
    }
  }
}
