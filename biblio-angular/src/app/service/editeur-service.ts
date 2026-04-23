import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Editeur } from '../model/editeur';

@Injectable({
  providedIn: 'root',
})
export class EditeurService {

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Editeur[]> {
    return this.http.get<Editeur[]>("/editeur");
  }

  public add(editeur: Editeur): Observable<Editeur> {
    return this.http.post<Editeur>("/editeur", editeur);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`/editeur/${ id }`);
  }
}
