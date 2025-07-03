import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaseService } from '../service/case.service';

@Component({
  selector: 'app-aps-sp-action-buttons',
  templateUrl: './aps-sp-action-buttons.component.html'
})
export class ApsSpActionButtonsComponent extends DfpsFormValidationDirective implements OnInit {

  constructor(private modalService: BsModalService, private rootFormGroup: FormGroupDirective,
    private caseService: CaseService,
    private router: Router,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }
  @Input() enableII: boolean;
  @Input() actionBtnClickedFrom: string;
  apsRootFormDetails: FormGroup;
  hideAllBtns: boolean;
  ngOnInit(): void {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.apsRootFormDetails = this.rootFormGroup.control;
  }

  pageMode() {
    if (this.apsRootFormDetails.get('pageMode').value === 'VIEW') {
      this.hideAllBtns = true;
    }
  }

  showErrorPopup(errorMessage: string) {
    const initialState = {
      title: this.apsRootFormDetails.get('pageTitle').value,
      message: errorMessage,
      showCancel: false
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
    });
  }

  addPrblm() {
    this.apsRootFormDetails.get('apsServicePlanDto').patchValue({
      actionBtnClickedFromSection: this.actionBtnClickedFrom
    });
    if (this.apsRootFormDetails.get('apsServicePlanDto.actionBtnClickedFromSection').value === 'II') {
      const selectedSvcPrblmsInII = this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources').
        value.some((val) => val.selected === true);
      if (selectedSvcPrblmsInII) {
        this.addNewPrblmToSrc();
      } else {
        this.showErrorPopup('Select the problem origin to add a problem.')
      }
    }
    if (this.apsRootFormDetails.get('apsServicePlanDto.actionBtnClickedFromSection').value === 'ANE') {
      const selectedSvcPrblmsInAllegData = this.apsRootFormDetails.get('apsServicePlanDto.allegData').
        value.some(val => val.selected === true);
      if (selectedSvcPrblmsInAllegData) {
        this.addNewPrblmToSrc();
      } else {
        this.showErrorPopup('Select the problem origin to add a problem.')
      }
    }
    if (this.apsRootFormDetails.get('apsServicePlanDto.actionBtnClickedFromSection').value === 'SNA_ICS') {
      const selectedSvcPrblmsInSNAICSData = this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData').
        value.some((val) => val.selected === true);
      if (selectedSvcPrblmsInSNAICSData) {
        this.addNewPrblmToSrc();
      } else {
        this.showErrorPopup('Select the problem origin to add a problem.')
      }
    }
  }

  addNewPrblmToSrc() {
    this.caseService.addPrblm(this.apsRootFormDetails.get('apsServicePlanDto').value).subscribe((res) => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      if (res) {
        this.reloadCurrentPage();
      }
    })
  }

  removePrblm() {
    this.apsRootFormDetails.get('apsServicePlanDto').patchValue({
      actionBtnClickedFromSection: this.actionBtnClickedFrom
    });
    const selectedSvcPrblmsInII = this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources').
      value.some(item => (
        item.serviceProblems.some((val) => val.selected === true)
      ));
    const selectedSvcPrblmsInAllegData = this.apsRootFormDetails.get('apsServicePlanDto.allegData').value.some(item =>
      (item.serviceProblems.some((val) => val.selected === true)
      ));
    const selectedSvcPrblmsInSNAICSData = this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData').value.some(item =>
      (item.serviceProblems.some((val) => val.selected === true)
      ));
    if (selectedSvcPrblmsInII || selectedSvcPrblmsInAllegData || selectedSvcPrblmsInSNAICSData) {
      this.caseService.removePrblm(this.apsRootFormDetails.get('apsServicePlanDto').value).subscribe((res) => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (res) {
          this.reloadCurrentPage();
        }
      })
    } else {
      this.showErrorPopup('Select the problem(s) that need to be removed.')
    }
  }

  addAction() {
    this.apsRootFormDetails.get('apsServicePlanDto').patchValue({
      actionBtnClickedFromSection: this.actionBtnClickedFrom
    });
    let listOfSelectedSvcPrblms = [];
    this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources').
      value.find((item: any) => {
        item.serviceProblems.filter((val: any) => {
          if (val.selected === true) {
            listOfSelectedSvcPrblms.push(val)
          }
        });
      });
    this.apsRootFormDetails.get('apsServicePlanDto.allegData').
      value.find((item: any) => {
        item.serviceProblems.filter((val: any) => {
          if (val.selected === true) {
            listOfSelectedSvcPrblms.push(val)
          }
        });
      });
    this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData').
      value.find((item: any) => {
        item.serviceProblems.filter((val: any) => {
          if (val.selected === true) {
            listOfSelectedSvcPrblms.push(val)
          }
        });
      });
    if (listOfSelectedSvcPrblms?.length === 1) {
      this.caseService.addActn(this.apsRootFormDetails.get('apsServicePlanDto').value).subscribe((res) => {
        if (res) {
          this.reloadCurrentPage();
        }
      })
    }
    else if (listOfSelectedSvcPrblms?.length > 1) {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      if(this.router.url.includes('?')){
        this.caseService.setReturnUrl(this.router.url.slice(0, this.router.url.indexOf('?')));
      }else{
        this.caseService.setReturnUrl(this.router.url);
      }
      this.router.navigate(['case/add-actions-to-multiProblems']);
      this.caseService.setFormData(this.apsRootFormDetails);
    }
    else {
      this.showErrorPopup('Select Problem(s) to add an Action')
    }
  }

  removeAction() {
    this.apsRootFormDetails.get('apsServicePlanDto').patchValue({
      actionBtnClickedFromSection: this.actionBtnClickedFrom
    });
    const selectedActnID = this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources').value.some(item => (
      item.serviceProblems.some((val: any) => (val.actions.some((el: any) => el.selected === true)))
    ));
    const selectedActnsInAllegData = this.apsRootFormDetails.get('apsServicePlanDto.allegData').value.some(item =>
      (item.serviceProblems.some((val: any) => (val.actions.some((el: any) => el.selected === true)))
      ));
    const selectedActnsInSNAICSData = this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData').value.some(item =>
      (item.serviceProblems.some((val: any) => (val.actions.some((el: any) => el.selected === true)))
      ));
    if (selectedActnID || selectedActnsInAllegData || selectedActnsInSNAICSData) {
      this.caseService.removeActn(this.apsRootFormDetails.get('apsServicePlanDto').value).subscribe((res) => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (res) {
          this.reloadCurrentPage();
        }
      })
    } else {
      this.showErrorPopup('Select Action(s) to be removed')
    }
  }

  reloadCurrentPage() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    if (this.router.url.includes('?')) {
      let currentUrl = this.router.url.slice(0, this.router.url.indexOf('?'));
      this.router.navigateByUrl('/',
        { skipLocationChange: true }).then(() => {
          this.router.navigate([currentUrl]);
        });
    } else {
      let currentUrl = this.router.url;
      this.router.navigateByUrl('/',
        { skipLocationChange: true }).then(() => {
          this.router.navigate([currentUrl]);
        });
    }
  }

  save() {
    // if (this.validateFormGroup(this.apsRootFormDetails)) {
    if (this.apsRootFormDetails.get('pageTitle').value === 'Service Plan') {
      this.apsRootFormDetails.patchValue({
        isSaveClicked: true
      });
      this.caseService.saveAPSServiceplanDetails(this.apsRootFormDetails.value).subscribe(res => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (res) {
          this.reloadCurrentPage();
        }
      });
    } else {
      this.caseService.getApsSafetyAssmntDetails(this.apsRootFormDetails.value.eventId).subscribe(val => {
        if (val) {
          const payload = Object.assign(val, this.apsRootFormDetails.getRawValue());
          this.caseService.saveSafetyAssmntDetails(payload).subscribe(res => {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            if (res) {
              this.reloadCurrentPage();
            }
          });
        }
      })
    }
  }
  // }
}
