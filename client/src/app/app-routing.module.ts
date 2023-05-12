import { Component, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { View1Component } from './components/view1/view1.component';
import { View0Component } from './components/view0/view0.component';

const routes: Routes = [
  {path:'',component: View0Component},
  {path:'uploadfile',component:View1Component},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
