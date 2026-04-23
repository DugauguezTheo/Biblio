import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { Auteur } from '../../model/auteur';
import { AuteurService } from '../../service/auteur-service';

@Component({
  selector: 'app-auteur',
  imports: [ CommonModule, ReactiveFormsModule ],
  templateUrl: './auteur-page.html',
  styleUrl: './auteur-page.css',
})
export class AuteurPage {
  private titleService: Title = inject(Title);
  private auteurService: AuteurService = inject(AuteurService);
  private formBuilder: FormBuilder = inject(FormBuilder);

  protected auteurs$!: Observable<Auteur[]>;
  private refresh$: Subject<void> = new Subject<void>();

  protected formAuteur!: FormGroup;
  protected formNomCtrl!: FormControl;
  protected formPrenomCtrl!: FormControl;
  protected formNationaliteCtrl!: FormControl;

  ngOnInit(): void {
    this.titleService.setTitle("Liste des auteurs");

    this.auteurs$ = this.refresh$.pipe(
      startWith(0), // Initialisation => forcer le chargement une première fois
      switchMap(() => this.auteurService.findAllAuteur()) // Transformer au moment du next()
    );

    // Fabrication du formulaire avec le FormBuilder
    this.formNomCtrl = this.formBuilder.control("", Validators.required);
    this.formPrenomCtrl = this.formBuilder.control("", Validators.required);
    this.formNationaliteCtrl = this.formBuilder.control("", Validators.required);

    this.formAuteur = this.formBuilder.group({
      // Description des contrôles du formulaire
      nom: this.formNomCtrl,
      prenom: this.formPrenomCtrl,
      nationalite: this.formNationaliteCtrl
    });
  }

  private reload() {
    this.refresh$.next();

    // Sinon, rechargement de la page
    // this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
    //   this.router.navigate([ '/matiere' ]);
    // });
  }

  public addAuteur() {
    const auteur: Auteur = {
      nom: this.formNomCtrl.value,
      prenom: this.formPrenomCtrl.value,
      nationalite: this.formNationaliteCtrl.value,
  };

  this.auteurService.addAuteur(auteur).subscribe(() => this.reload());
  }

  public deleteAuteur(auteur: Auteur) {
    this.auteurService.deleteAuteurById(auteur.id).subscribe(() => this.reload());
  }
}


