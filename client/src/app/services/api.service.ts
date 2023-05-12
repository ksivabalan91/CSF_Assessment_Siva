import { HttpClient } from '@angular/common/http';
import { ElementRef, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {


  constructor(private http: HttpClient) { }

  upload(form: FormGroup, zipFileElem: ElementRef){
    // console.log("upload called");
    // console.log("name: "+form.get('name')?.value);
    // console.log("title: "+form.get('title')?.value);
    // console.log("comments: "+form.get('title')?.value);
    // console.log("zipFile: "+zipFileElem.nativeElement.files[0]);

    const formdata = new FormData();
    formdata.set("name",form.get('name')?.value);
    formdata.set("title",form.get('title')?.value);
    formdata.set("comments",form.get('comments')?.value);
    formdata.set("zipFile",zipFileElem.nativeElement.files[0]);
    console.log(formdata);

    return lastValueFrom(this.http.post("/upload",formdata));
  }
}
