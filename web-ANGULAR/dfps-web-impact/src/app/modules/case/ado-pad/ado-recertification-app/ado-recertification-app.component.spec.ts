import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdoRecertificationAppComponent } from './ado-recertification-app.component';

describe('AdoRecertificationAppComponent', () => {
  let component: AdoRecertificationAppComponent;
  let fixture: ComponentFixture<AdoRecertificationAppComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdoRecertificationAppComponent]
    });
    fixture = TestBed.createComponent(AdoRecertificationAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
