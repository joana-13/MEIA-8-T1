import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewRequestHistoryComponent } from './view-request-history.component';

describe('ViewRequestHistoryComponent', () => {
  let component: ViewRequestHistoryComponent;
  let fixture: ComponentFixture<ViewRequestHistoryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewRequestHistoryComponent]
    });
    fixture = TestBed.createComponent(ViewRequestHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
