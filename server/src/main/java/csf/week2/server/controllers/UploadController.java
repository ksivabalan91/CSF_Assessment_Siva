package csf.week2.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import csf.week2.server.Utils;
import csf.week2.server.models.Bundle;
import csf.week2.server.repositories.ArchiveRepository;
import csf.week2.server.repositories.ImageRepository;
import csf.week2.server.services.S3Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private S3Service s3Svc;

    @Autowired
    private ArchiveRepository arcRepo;
    
    @Autowired
    private ImageRepository imgRepo;
    
    @PostMapping(
        path="/upload", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    public ResponseEntity<String> upload(
        @RequestPart MultipartFile zipFile, 
        @RequestPart String name,
        @RequestPart String title,
        @RequestPart String comments
        ) throws IOException {

            System.out.println("zipfile:"+zipFile);
            System.out.println("name:"+name);
            System.out.println("title:"+title);
            System.out.println("comments:"+comments);

            //! UNZIP files
            File[] files = Utils.unzip(zipFile);
            List<String> urlList = new LinkedList<>();
            
            //! Upload files
            for(File file:files){                
                String[] fileExt =  file.getName().split("\\.");
                String fileName = file.getName();
                FileInputStream fileInputStream = new FileInputStream(file);
                long fileSize = file.length();
                String contentType = "image/"+fileExt[fileExt.length-1];
                String uri = imgRepo.upload(fileInputStream,fileSize,contentType,fileName, name, title, comments);
                urlList.add(uri);
            }

            // System.out.println(urlList);           
           //! insert Mongo Record
            String bundleId = arcRepo.recordBundle(title,name,comments,urlList);

            
            if(bundleId!=null){
                JsonObject resp = Json.createObjectBuilder()
                    .add("bundleId", bundleId)
                    .build();                    
                    return ResponseEntity.status(HttpStatus.CREATED).body(resp.toString());
                }
                else{
                    JsonObject error = Json.createObjectBuilder()
                    .add("error", "operation failed")
                    .build();                    
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
                }                    

            }

    @GetMapping(
        path = "/bundle/{bundleId}",
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> get(@PathVariable String bundleId){
        Bundle bundle = arcRepo.getBundleByBundleId(bundleId);

        String json = Utils.toJsonStr(bundle);

        return ResponseEntity.ok().body(json);
    }
    
    @GetMapping(
        path = "/bundles",
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> getAll(){
        List<Bundle> bundles = arcRepo.getBundles();
        String json = Utils.toJsonStr(bundles);
        return ResponseEntity.ok().body(json);        
    }
    
    
}
