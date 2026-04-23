import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Auteur } from '../model/auteur';

@Injectable({
  providedIn: 'root',
})
export class AuteurService {
    constructor(private http: HttpClient) { }

  public findAuteurById(id: number) {
      return this.http.get<Auteur>(`/auteur/${id}`);
    }

  public findAllAuteur(): Observable<Auteur[]> {
    return this.http.get<Auteur[]>("/auteur");
  }

  public addAuteur(auteur: Auteur): Observable<Auteur> {
    return this.http.post<Auteur>("/auteur", auteur);
  }

  public updateCollection(auteur: Auteur) {
      return this.http.put(`/auteur/${auteur.id}`, auteur);
    }

  public deleteAuteurById(id: number): Observable<void> {
    return this.http.delete<void>(`/auteur/${ id }`);
  }
}

