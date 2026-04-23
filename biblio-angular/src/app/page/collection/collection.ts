import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';

@Component({
  selector: 'app-collection',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './collection.html',
  styleUrl: './collection.css',
})
export class Collection implements OnInit {
  private titleService: Title = inject(Title);
  private collectionService: CollectionService = inject(CollectionService);
  private formBuilder: FormBuilder = inject(FormBuilder);

  private collection: Collection = {nom: ''};
  
  protected collections$: Observable<Collection[]>;
  private refresh$: Subject<void> = new Subject<void>();
  
  protected formCollection!: FormGroup;
  protected formNomCtrl!: FormControl;

  ngOnInit(): void {
    this.titleService.setTitle('Collections');

    this.collections$ = this.refresh$.pipe(
      startWith(0),
      switchMap(() => this.collectionService.findAllCollections())
    );

    this.formNomCtrl = this.formBuilder.control("", Validators.required);
    this.formCollection = this.formBuilder.group({
      nom: this.formNomCtrl
    });
  }

  private reloadCollections() {
    this.refresh$.next();
  }

  public addCollection() {
    const collection: Collection = {
      id: 1
      nom: this.formNomCtrl.value
    };
    this.collectionService.addCollection(collection).subscribe(() => this.reloadCollections());
  }

  public deleteCollection(collection: Collection) {
    this.collectionService.deleteCollectionById(collection.id).subscribe(() => this.reloadCollections());
  }

  public updateCollection(collection: Collection) {
    this.collectionService.updateCollection(collection).subscribe(() => {
      this.collection = {nom: ''};
    });
    this.reloadCollections();
  }
}
