import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { Livre } from '../../model/livre';
import { LivreService } from '../../service/livre-service';
import { CommonModule } from '@angular/common';
import { EditeurService } from '../../service/editeur-service';
import { AuteurService } from '../../service/auteur-service';
import { CollectionService } from '../../service/collection-service';
import { Editeur } from '../../model/editeur';
import { Auteur } from '../../model/auteur';
import { Collection } from '../../model/collection';

@Component({
  selector: 'app-livre',
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './livres.html',
  styleUrl: './livres.css',
})
export class Livres implements OnInit{

  private titleService: Title = inject(Title);
  private livreService: LivreService = inject(LivreService);
  protected editeurService: EditeurService = inject(EditeurService);
  protected auteurService: AuteurService = inject(AuteurService);
  protected collectionService: CollectionService = inject(CollectionService);
  private formBuilder: FormBuilder = inject(FormBuilder);

  protected livres$!: Observable<Livre[]>;
  protected editeurs!: Observable<Editeur[]>;
  protected auteurs!: Observable<Auteur[]>;
  protected collections!: Observable<Collection[]>;
  private refresh$: Subject<void> = new Subject<void>();

  protected formLivre !: FormGroup;
  protected formTitreCtrl !: FormControl;
  protected formResumeCtrl !: FormControl;
  protected formAnneeCtrl !: FormControl;
  protected formAuteurCtrl !: FormControl;
  protected formEditeurCtrl !: FormControl;
  protected formCollectionCtrl !: FormControl;

  ngOnInit(): void {
    this.titleService.setTitle("Liste des livres");

    this.livres$ = this.refresh$.pipe(
      startWith(0), // Initialisation => forcer le chargement une première fois
      switchMap(() => this.livreService.findAll()) // Transformer au moment du next()
    );

    this.editeurs = this.editeurService.findAll();
    this.auteurs = this.auteurService.findAllAuteur();
    this.collections = this.collectionService.findAllCollections();

    // Fabrication du formulaire avec le FormBuilder
    this.formTitreCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    this.formResumeCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    this.formAnneeCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    this.formAuteurCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    this.formEditeurCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    //this.formCollectionCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);

    this.formLivre = this.formBuilder.group({
      // Decription des contrôles du formulaire
      libelle: this.formTitreCtrl,
      resume: this.formResumeCtrl,
      annee: this.formAnneeCtrl,
      auteur: this.formAuteurCtrl,
      editeur: this.formEditeurCtrl,
      collection: this.formCollectionCtrl
    });
  }

  private reload() {
    this.refresh$.next();
  }

  public addLivre() {
    const livre: Livre = {
      id: 0,
      titre: this.formTitreCtrl.value,
      resume: this.formResumeCtrl.value,
      annee: this.formAnneeCtrl.value,
      auteur: this.formAuteurCtrl.value,
      editeur: this.formEditeurCtrl.value,
      collection: this.formCollectionCtrl.value
    };

    this.livreService.add(livre).subscribe(() => this.reload());
  }

  public deleteLivre(livre: Livre) {
    this.livreService.deleteById(livre.id).subscribe(() => this.reload());
  }
}
