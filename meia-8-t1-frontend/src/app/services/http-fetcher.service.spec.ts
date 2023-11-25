import { TestBed } from '@angular/core/testing';

import { HttpFetcherService } from './http-fetcher.service';

describe('HttpFetcherService', () => {
  let service: HttpFetcherService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HttpFetcherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
