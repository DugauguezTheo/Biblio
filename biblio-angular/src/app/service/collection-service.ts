import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Collection } from '../model/collection';

@Injectable({
  providedIn: 'root',
})
export class CollectionService {
  constructor(private http: HttpClient) {}

  public findAllCollections() {
    return this.http.get<Collection[]>("/collection");
  }

  public findCollectionById(id: number) {
    return this.http.get<Collection>(`/collection/${id}`);
  }

  public addCollection(collection: Collection) {
    return this.http.post<Collection>("/collection", collection);
  }

  public deleteCollectionById(id: number) {
    return this.http.delete(`/collection/${id}`);
  }

  public updateCollection(collection: Collection) {
    return this.http.put(`/collection/${collection.id}`, collection);
  }
}
