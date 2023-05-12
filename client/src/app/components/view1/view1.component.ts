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

  @ViewChild('file',{static:true}) zipFileElem!: ElementRef;

  constructor(private apiSvc:ApiService, private fb: FormBuilder, private router: Router){}

  ngOnInit(){
    this.form = this.fb.group({
      name : this.fb.control<string>('mando',[Validators.required],),
      title : this.fb.control<string>('delorean',[Validators.required],),
      comments : this.fb.control<string>('this is a comment'),
      zipFile : this.fb.control('',[Validators.required],)
    })
  }

  upload(){
    console.log(this.form,this.zipFileElem)
    this.apiSvc.upload(this.form,this.zipFileElem);
  }
  isVaild(){
    return this.form.valid;
  }
  isUploaded(){
    return true;
  }


}
