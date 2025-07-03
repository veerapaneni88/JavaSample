import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NeiceTransmittalSummaryComponent } from './neice-transmittal-summary.component';

describe('NeiceTransmittalSummaryComponent', () => {
  let component: NeiceTransmittalSummaryComponent;
  let fixture: ComponentFixture<NeiceTransmittalSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NeiceTransmittalSummaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NeiceTransmittalSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
