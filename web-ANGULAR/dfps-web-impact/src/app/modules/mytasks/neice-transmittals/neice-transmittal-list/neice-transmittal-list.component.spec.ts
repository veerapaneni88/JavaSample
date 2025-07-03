import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NeiceTransmittalListComponent } from './neice-transmittal-list.component';

describe('NeiceTransmittalListComponent', () => {
  let component: NeiceTransmittalListComponent;
  let fixture: ComponentFixture<NeiceTransmittalListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NeiceTransmittalListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NeiceTransmittalListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
