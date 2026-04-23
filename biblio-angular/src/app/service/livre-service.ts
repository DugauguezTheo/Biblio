import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Livre } from '../model/livre';

@Injectable({
  providedIn: 'root',
})
export class LivreService {
  constructor(private http: HttpClient) { }

  public findAllLivres() {
      return this.http.get<Livre[]>("/livre");
    }

    public findLivreById(id: number | undefined) {
      return this.http.get<Livre>(`/livre/${id}`);
    }

    public addLivre(livre: Livre) {
      return this.http.post<Livre>("/livre", livre);
    }

    public deleteLivreById(id: number | undefined) {
      return this.http.delete(`/livre/${id}`);
    }

    public updateLivre(livre: Livre) {
      return this.http.put(`/livre/${livre.id}`, livre);
    }
}
