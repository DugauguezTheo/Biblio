import { Routes } from '@angular/router';
import { AccueilPage } from './page/accueil-page/accueil-page';
import { CollectionsPage } from './page/collections-page/collections-page';
import { Livres } from './page/livres/livres';
import { authGuard } from './guard/auth-guard';

export const routes: Routes = [

    { path: 'home', component: AccueilPage, canActivate: [authGuard] },
    { path: 'livre', component: Livres, canActivate: [authGuard] },
    { path: 'auteur', component: AuteurPage, canActivate: [authGuard] },
    { path: 'collection', component: CollectionsPage, canActivate: [authGuard] },
    { path: 'editeur', component: Editeurs, canActivate: [authGuard] },

    //{ path: 'login', component: LoginPage },
    { path: '', redirectTo: 'home', pathMatch: 'full' }
];
