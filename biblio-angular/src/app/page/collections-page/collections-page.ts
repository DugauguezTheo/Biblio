import { Collection } from './../../model/collection';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { CollectionService } from '../../service/collection-service';

@Component({
  selector: 'app-collections-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './collections-page.html',
  styleUrl: './collections-page.css',
})
export class CollectionsPage implements OnInit {
  private titleService: Title = inject(Title);
  private collectionService: CollectionService = inject(CollectionService);
  private formBuilder: FormBuilder = inject(FormBuilder);

  private collection: Collection = {nom: ''};

  protected editingCollection ?: Collection | null;
  protected collections$!: Observable<Collection[]>;
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

  public deleteCollection(collection: Collection) {
    if (collection.id!=undefined) {
        this.collectionService.deleteCollectionById(collection.id).subscribe(() => this.reloadCollections());

    }
  }

  public ajouterOuModifier(){
    const collection : Collection = {
      nom: this.formNomCtrl.value,
      id: this.editingCollection?.id
    }

    if(this.editingCollection){
      this.collectionService.updateCollection(collection).subscribe(() => {
        this.editingCollection = null;
        this.formCollection.reset();
        this.reloadCollections();
      })
    }
    else{
      this.collectionService.addCollection(collection).subscribe(() => this.reloadCollections());
    }
  }

  public editer(collection: Collection) {
    this.editingCollection = collection;
    this.formNomCtrl.setValue(collection.nom);
    this.reloadCollections();
  }
}
