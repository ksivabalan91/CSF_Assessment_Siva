import { HttpClient } from '@angular/common/http';
import { ElementRef, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { lastValueFrom } from 'rxjs';
import { Bundle } from '../models/Bundle';

@Injectable({
  providedIn: 'root'
})
export class ApiService {


  constructor(private http: HttpClient) { }

  upload(form: FormGroup, zipFileElem: ElementRef) {
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
    // console.log(formdata);

    return this.http.post<any>("/upload",formdata)
  }

  getBundle(bundleId: string){
    return this.http.get<Bundle>('/bundle/'+bundleId)
  }

  getBundles(){
    return this.http.get<Bundle[]>('/bundles')
  }
  getDate(date:string){
    const date_obj = new Date(date);
    const formattedDate = date_obj.toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });

    return formattedDate;
  }
}
