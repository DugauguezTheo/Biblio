import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Editeurs } from './editeurs';

describe('Editeurs', () => {
  let component: Editeurs;
  let fixture: ComponentFixture<Editeurs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Editeurs],
    }).compileComponents();

    fixture = TestBed.createComponent(Editeurs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
