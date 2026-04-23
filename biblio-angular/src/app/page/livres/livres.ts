import { Component } from '@angular/core';

@Component({
  selector: 'app-livre',
  imports: [],
  templateUrl: './livre.html',
  styleUrl: './livre.css',
})
export class Livres {

  private titleService: Title = inject(Title);
}
