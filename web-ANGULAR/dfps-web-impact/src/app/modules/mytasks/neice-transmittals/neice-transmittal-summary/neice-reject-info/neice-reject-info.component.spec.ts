import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NeiceRejectInfoComponent } from './neice-reject-info.component';

describe('NeiceRejectInfoComponent', () => {
  let component: NeiceRejectInfoComponent;
  let fixture: ComponentFixture<NeiceRejectInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NeiceRejectInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NeiceRejectInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
