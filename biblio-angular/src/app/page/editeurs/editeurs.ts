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
export class Editeurs implements OnInit{

  private titleService: Title = inject(Title);
  private editeurService: EditeurService = inject(EditeurService);
  private formBuilder: FormBuilder = inject(FormBuilder);
  // private router: Router = inject(Router);

  protected editeurs$!: Observable<Editeur[]>;
  private refresh$: Subject<void> = new Subject<void>();

  protected formEditeur !: FormGroup;
  protected formNomCtrl !: FormControl;
  protected formPaysCtrl !: FormControl;

  ngOnInit(): void {
    this.titleService.setTitle("Liste des matières");

    this.editeurs$ = this.refresh$.pipe(
      startWith(0), // Initialisation => forcer le chargement une première fois
      switchMap(() => this.editeurService.findAllEditeurs()) // Transformer au moment du next()
    );

    // Fabrication du formulaire avec le FormBuilder
    this.formNomCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
    this.formPaysCtrl = this.formBuilder.control("Valeur par défaut", Validators.required);
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

  public addEditeur() {
    const editeur: Editeur = {
      nom: this.formNomCtrl.value,
      pays: this.formPaysCtrl.value
    };

    this.editeurService.addEditeur(editeur).subscribe(() => this.reload());
  }

  public deleteEditeur(editeur: Editeur) {
    this.editeurService.deleteEditeurById(editeur.id).subscribe(() => this.reload());
  }
}
