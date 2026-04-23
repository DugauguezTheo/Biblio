import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Livre } from '../model/livre';

@Injectable({
  providedIn: 'root',
})
export class LivreService {
  constructor(private http: HttpClient) { }

  public findAll(): Observable<Livre[]> {
    return this.http.get<Livre[]>("/livre");
  }

  public add(livre: Livre): Observable<Livre> {
    return this.http.post<Livre>("/livre", livre);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/livre/${ id }`);
  }
}
