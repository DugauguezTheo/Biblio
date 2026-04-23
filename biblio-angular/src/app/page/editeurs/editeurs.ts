import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { Editeur } from '../../model/editeur';
import { EditeurService } from '../../service/editeur-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-editeurs',
  imports: [
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './editeurs.html',
  styleUrl: './editeurs.css',
})
export class Editeurs implements OnInit {
  private titleService: Title = inject(Title);
  private editeurService: EditeurService = inject(EditeurService);
  private formBuilder: FormBuilder = inject(FormBuilder);

  protected editingEditeur?: Editeur | null;
  protected editeurs$!: Observable<Editeur[]>;
  private refresh$: Subject<void> = new Subject<void>();

  protected formEditeur !: FormGroup;
  protected formNomCtrl !: FormControl;
  protected formPaysCtrl !: FormControl;

  ngOnInit(): void {
    this.titleService.setTitle("Liste des éditeurs");

    this.editeurs$ = this.refresh$.pipe(
      startWith(0), // Initialisation => forcer le chargement une première fois
      switchMap(() => this.editeurService.findAllEditeurs()) // Transformer au moment du next()
    );

    // Fabrication du formulaire avec le FormBuilder
    this.formNomCtrl = this.formBuilder.control("", Validators.required);
    this.formPaysCtrl = this.formBuilder.control("", Validators.required);
    this.formEditeur = this.formBuilder.group({
      // Decription des contrôles du formulaire
      nom: this.formNomCtrl,
      pays: this.formPaysCtrl
    });
  }

  private reload() {
    this.refresh$.next();

    // Sinon, rechargement de la page
    // this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
    //   this.router.navigate([ '/editeur' ]);
    // });
  }

  public deleteEditeur(editeur: Editeur) {
    if (editeur.id != undefined) {
      this.editeurService.deleteEditeurById(editeur.id).subscribe(() => this.reload());
    }
  }

  public ajouterOuModifierEditeur() {
    const editeur: Editeur = {
      nom: this.formNomCtrl.value,
      pays: this.formPaysCtrl.value,
      id: this.editingEditeur?.id
    }

    if (this.editingEditeur) {
      this.editeurService.updateEditeur(editeur).subscribe(() => {
        this.editingEditeur = null;
        this.formEditeur.reset();
        this.reload();
      })
    }
    else {
      this.editeurService.addEditeur(editeur).subscribe(() => this.reload());
    }
  }

  public editerEditeur(editeur: Editeur) {
    this.editingEditeur = editeur;
    this.formNomCtrl.setValue(editeur.nom);
    this.formPaysCtrl.setValue(editeur.pays);
    this.reload();
  }


}
