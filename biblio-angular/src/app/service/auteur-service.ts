import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Auteur } from '../model/auteur';

@Injectable({
  providedIn: 'root',
})
export class AuteurService {
    constructor(private http: HttpClient) { }

  public findAll(): Observable<Auteur[]> {
    return this.http.get<Auteur[]>("/auteur");
  }

  public add(auteur: Auteur): Observable<Auteur> {
    return this.http.post<Auteur>("/auteur", auteur);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/auteur/${ id }`);
  }
}
}
