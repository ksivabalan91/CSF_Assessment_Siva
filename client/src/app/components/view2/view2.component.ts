import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Bundle } from 'src/app/models/Bundle';
import { ApiService } from 'src/app/services/api.service';

@Component({
  selector: 'app-view2',
  templateUrl: './view2.component.html',
  styleUrls: ['./view2.component.css']
})
export class View2Component implements OnInit{

  bundleinfo!:Bundle;

  constructor(private apiSvc: ApiService, private actRoute: ActivatedRoute){

  }

  ngOnInit(){
    this.actRoute.params.subscribe(
      params => {
        this.apiSvc.getBundle(params['bundleId']).subscribe(
          data => this.bundleinfo = data
        )
      }
    )
  }
  
  getDate(date:string){
    return this.apiSvc.getDate(date);
  }



}
