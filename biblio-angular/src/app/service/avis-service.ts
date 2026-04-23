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

  public findById(id: number) {
      return this.http.get<Avis>(`/collection/${id}`);
  }

  public add(avis: Avis): Observable<Avis> {
    return this.http.post<Avis>("/avis", avis);
  }


  public update(avis: Avis) {
      return this.http.put(`/collection/${avis.id}`, avis);
  }


  public deleteById(id: number| undefined): Observable<void> {
    return this.http.delete<void>(`/avis/${ id }`);
  }
}
