import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Editeur } from '../model/editeur';

@Injectable({
  providedIn: 'root',
})
export class EditeurService {

  constructor(private http: HttpClient) { }

  public findAllEditeurs() {
      return this.http.get<Editeur[]>("/editeur");
    }

    public findEditeurById(id: number | undefined) {
      return this.http.get<Editeur>(`/editeur/${id}`);
    }

    public addEditeur(editeur: Editeur) {
      return this.http.post<Editeur>("/editeur", editeur);
    }

    public deleteEditeurById(id: number | undefined) {
      return this.http.delete(`/editeur/${id}`);
    }

    public updateEditeur(editeur: Editeur) {
      return this.http.put(`/editeur/${editeur.id}`, editeur);
    }
}
