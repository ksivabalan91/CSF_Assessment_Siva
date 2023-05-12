import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from 'src/app/services/api.service';

@Component({
  selector: 'app-view1',
  templateUrl: './view1.component.html',
  styleUrls: ['./view1.component.css']
})
export class View1Component implements OnInit {
  form!: FormGroup;
  bundleId = ''

  @ViewChild('file',{static:true}) zipFileElem!: ElementRef;

  constructor(private apiSvc:ApiService, private fb: FormBuilder, private router: Router){}

  isLoading = false;

  ngOnInit(){
    this.form = this.fb.group({
      name : this.fb.control<string>('',[Validators.required],),
      title : this.fb.control<string>('',[Validators.required],),
      comments : this.fb.control<string>(''),
      zipFile : this.fb.control('',[Validators.required],)
    })
  }

  upload(){
    console.log(this.form,this.zipFileElem)
    this.isLoading=true;
    this.apiSvc.upload(this.form,this.zipFileElem).subscribe(
      data => {
        console.log(data['bundleId']);
        this.isLoading=false;
        this.bundleId = data['bundleId'];
        this.router.navigate(['view',this.bundleId])
      },
      error => {
        return "error";
      }
    )

  }

}
