import { Component, inject, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AvisService } from '../../service/avis-service';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Avis } from '../../model/avis';
import { CommonModule } from '@angular/common';
import { Livre } from '../../model/livre';
import { LivreService } from '../../service/livre-service';


@Component({
  selector: 'app-avis',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './avis-page.html',
  styleUrl: './avis-page.css',
})
export class AvisPage implements OnInit{

  //Services
  private titleService: Title = inject(Title);

  private avisService: AvisService = inject(AvisService); 
  private livreService: LivreService = inject(LivreService); 


  private formBuilder: FormBuilder = inject(FormBuilder);


  //Observables
  protected listeAvis$!: Observable<Avis[]>;
  private refresh$: Subject<void> = new Subject<void>();



  //Forms
  protected formAvis!: FormGroup;
  protected formNoteCtrl!: FormControl;
  protected formCommentaireCtrl!: FormControl;
  protected formDateCtrl!: FormControl;
  protected formLivreCtrl!: FormControl;


  //Variables
  protected notes: number[] = [1, 2, 3, 4, 5];
  protected livres!: Observable<Livre[]>;


  ngOnInit(): void {

    this.titleService.setTitle("Avis");   

    this.listeAvis$ = this.refresh$.pipe(
      startWith(0),
      switchMap(() => this.avisService.findAll())
    );
    
    //Forms

    this.formNoteCtrl = this.formBuilder.control(0, Validators.min(1));
    this.formCommentaireCtrl = this.formBuilder.control("", Validators.required);
    this.formDateCtrl = this.formBuilder.control('', Validators.required);
    this.formLivreCtrl = this.formBuilder.control('', Validators.required);

    this.formAvis = this.formBuilder.group({
      // Description des contrôles du formulaire
      note: this.formNoteCtrl,
      commentaire: this.formCommentaireCtrl,
      date: this.formDateCtrl,
      livre: this.formLivreCtrl
    });


    //FindAll()
    this.avisService.findAll();

    this.livres = this.livreService.findAllLivres();
  }

  //Gestion étoiles
  protected countStar(note: number){
    this.formNoteCtrl.setValue(note);
  }


  addAvis(){
    const avis: Avis = {
      id: 0,
      note: this.formNoteCtrl.value,
      commentaire: this.formCommentaireCtrl.value,
      date: this.formDateCtrl.value,
      livre: this.formLivreCtrl.value}
  
      this.avisService.add(avis);
    }

  deleteAvis(avis: Avis){
    this.avisService.deleteById(avis.id);

  }

}
