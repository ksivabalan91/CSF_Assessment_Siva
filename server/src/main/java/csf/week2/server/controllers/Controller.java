package csf.week2.server.controllers;

import java.io.IOException;

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

import csf.week2.server.services.S3Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path="/api")
@CrossOrigin(origins = "*")
public class Controller {

    @Autowired
    private S3Service s3Svc;
    
    @PostMapping(
        path="/comment", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE
        )
    public ResponseEntity<String> post(
        @RequestPart MultipartFile picture, 
        @RequestPart String comments
        ) throws IOException {
            String response = s3Svc.upload(picture,comments);
            JsonObject json = Json.createObjectBuilder()
                .add("response", response)
                .build();
            
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
