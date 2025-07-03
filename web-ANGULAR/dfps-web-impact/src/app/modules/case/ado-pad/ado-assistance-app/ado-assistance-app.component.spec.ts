import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdoAssistanceAppComponent } from './ado-assistance-app.component';

describe('AdoAssistanceAppComponent', () => {
  let component: AdoAssistanceAppComponent;
  let fixture: ComponentFixture<AdoAssistanceAppComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdoAssistanceAppComponent]
    });
    fixture = TestBed.createComponent(AdoAssistanceAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
