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

  protected editingLivre ?: Livre | null;
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
      switchMap(() => this.livreService.findAllLivres()) // Transformer au moment du next()
    );

    this.editeurs = this.editeurService.findAllEditeurs();
    this.auteurs = this.auteurService.findAllAuteur();
    this.collections = this.collectionService.findAllCollections();

    // Fabrication du formulaire avec le FormBuilder
    this.formTitreCtrl = this.formBuilder.control("", Validators.required);
    this.formResumeCtrl = this.formBuilder.control("", Validators.required);
    this.formAnneeCtrl = this.formBuilder.control("", Validators.required);
    this.formAuteurCtrl = this.formBuilder.control(null, Validators.required);
    this.formEditeurCtrl = this.formBuilder.control(null, Validators.required);
    this.formCollectionCtrl = this.formBuilder.control(null, Validators.nullValidator);

    this.formLivre = this.formBuilder.group({
      // Decription des contrôles du formulaire
      titre: this.formTitreCtrl,
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

  public addOrUpdateLivre() {
    const livre: Livre = {
      titre: this.formTitreCtrl.value,
      resume: this.formResumeCtrl.value,
      annee: this.formAnneeCtrl.value,
      auteur: this.formAuteurCtrl.value,
      editeur: this.formEditeurCtrl.value,
      collection: this.formCollectionCtrl.value,
      id: this.editingLivre?.id
    };

    if(this.editingLivre){
      this.livreService.updateLivre(livre).subscribe(() => {
        this.editingLivre = null;
        this.formLivre.reset();
        this.reload();
      })
    }
    else{
      this.livreService.addLivre(livre).subscribe(() => {
        this.formLivre.reset();
        this.reload();
      })
    }
  }

  public editerLivre(livre : Livre){
    this.editingLivre = livre;
    this.formAnneeCtrl.setValue(livre.annee);
    this.formAuteurCtrl.setValue(livre.auteur);
    this.formCollectionCtrl.setValue(livre.collection);
    this.formEditeurCtrl.setValue(livre.editeur);
    this.formResumeCtrl.setValue(livre.resume);
    this.formTitreCtrl.setValue(livre.titre);
    this.reload();
  }

  public compareCollection(c1: Collection, c2: Collection): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  public compareAuteur(a1: Auteur, a2: Auteur): boolean {
    return a1 && a2 ? a1.id === a2.id : a1 === a2;
  }

  public compareEditeur(e1: Editeur, e2: Editeur): boolean {
    return e1 && e2 ? e1.id === e2.id : e1 === e2;
  }

  public deleteLivre(livre: Livre) {
    this.livreService.deleteLivreById(livre.id).subscribe(() => this.reload());
  }
}
