package csf.week2.server.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import csf.week2.server.repositories.ImageRepository;
import csf.week2.server.services.S3Service;
import jakarta.json.JsonArray;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private S3Service s3Svc;

    @Autowired
    private ImageRepository imgRepo;
    
    @PostMapping(
        path="/upload", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    public ResponseEntity<String> post(
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

            System.out.println(urlList);

            //! generate unique ID
            String bundleId = UUID.randomUUID().toString().substring(0, 8);
            String uploadDate = LocalDateTime.now().toString();            

            JsonArray json = Utils.toJsonArray(urlList);
            
            return ResponseEntity.ok().body(json.toString());
            }

    @GetMapping(
        path = "/comment/{postId}",
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> get(@PathVariable String postId){
        return s3Svc.download(postId);
    }
    
    @GetMapping(
        path = "/comments",
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    @CrossOrigin(origins = "https://day37-workshop-upload-s3.vercel.app")
    public ResponseEntity<String> listAll() throws JsonProcessingException{
        return s3Svc.listAll();
    }
    
    
}
