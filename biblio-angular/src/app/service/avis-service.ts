import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Avis } from '../model/avis';

@Injectable({
  providedIn: 'root',
})
export class AvisService {

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Avis[]> {
    return this.http.get<Avis[]>("/avis");
  }

  public add(avis: Avis): Observable<Avis> {
    return this.http.post<Avis>("/avis", avis);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/avis/${ id }`);
  }
}
