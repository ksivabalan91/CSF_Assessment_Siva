import { Component, OnInit } from '@angular/core';
import { Bundle } from 'src/app/models/Bundle';
import { ApiService } from 'src/app/services/api.service';

@Component({
  selector: 'app-view0',
  templateUrl: './view0.component.html',
  styleUrls: ['./view0.component.css']
})
export class View0Component implements OnInit{

  allBundles!: Bundle[];

  constructor(private apiSvc:ApiService){}

  ngOnInit(){
    this.apiSvc.getBundles().subscribe(
      data => this.allBundles = data
    )

  }

  getDate(date:string){
    return this.apiSvc.getDate(date);
  }

}
