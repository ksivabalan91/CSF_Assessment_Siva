package csf.week2.server.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class S3Service {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;

    public String upload(
        MultipartFile file,
         String comments
         ) throws IOException{

            //! display file data
            System.out.println("getName: " + file.getName()+"\n");
            System.out.println("getOriginalFilename: " + file.getOriginalFilename()+"\n");
            System.out.println("getContentType: " + file.getContentType()+"\n");
            System.out.println("getSize: " + file.getSize()+"\n");
            System.out.println("getClass: " + file.getClass()+"\n");
            System.out.println("getBytes: " + file.getBytes()+"\n");
            System.out.println("getInputStream: " + file.getInputStream()+"\n");
            //! Create a map to pass in metadata of the object later
            Map<String,String> userDataMap = new HashMap<>();
            userDataMap.put("user","Siva");
            userDataMap.put("uploadDateTime", LocalDateTime.now().toString());
            userDataMap.put("originalFilename", file.getOriginalFilename());
            userDataMap.put("comments", comments);

            //! Set METADATA
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(file.getContentType());
            metaData.setContentLength(file.getSize());
            metaData.setUserMetadata(userDataMap);

            //! generate unique ID
            String id = UUID.randomUUID().toString().substring(0, 8);

            //! Create putobject Request and configure with public access
            PutObjectRequest putReq = new PutObjectRequest(bucketName, id, file.getInputStream(), metaData);
            putReq.withCannedAcl(CannedAccessControlList.PublicRead);

            //! Execute the request
            s3Client.putObject(putReq);

            return id;
         }
    public ResponseEntity<String> download(String id){
        try{
            //! Create Get requet and execte
            GetObjectRequest getReq =  new GetObjectRequest(bucketName, id);
            S3Object result = s3Client.getObject(getReq);

            //! get object metadata
            ObjectMetadata metaData = result.getObjectMetadata();
            Map<String,String> userData = metaData.getUserMetadata();
            
            //! get object content as byte[]
            try(S3ObjectInputStream s3is = result.getObjectContent()){
                byte[] buffer = s3is.readAllBytes();
                String encodedString = Base64.getEncoder().encodeToString(buffer);

                JsonObject payload = Json.createObjectBuilder()
                    .add("comments", userData.get("comments"))
                    .add("user", userData.get("user"))
                    .add("uploadDateTime", userData.get("uploadDateTime"))
                    .add("originalFilename", userData.get("originalFilename"))
                    .add("image", "data:"+metaData.getContentType()+";base64,"+ encodedString)
                    .build();

                return ResponseEntity
                            .status(HttpStatus.OK)
                            // .contentLength(metaData.getContentLength())
                            // .contentType(MediaType.parseMediaType(metaData.getContentType()))
                            // .header("X-comments", userData.get("comments"))
                            // .header("X-user", userData.get("user"))
                            // .header("X-uploadDateTime", userData.get("uploadDateTime"))
                            // .header("X-originalFilename", userData.get("originalFilename"))
                            .body(payload.toString());
            }
        }catch(AmazonS3Exception ex){
            // object not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{'error':'Object not found'}");
        }catch(Exception ex){
            // For S3ObjectInputStream
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{'error':'Inputstream error'}");
        }
    }

    //! list all items in your bucket
    public ResponseEntity<String> listAll() throws JsonProcessingException{
        ListObjectsRequest listReq = new ListObjectsRequest().withBucketName(bucketName).withPrefix("");
        ObjectListing objList = s3Client.listObjects(listReq);

        List<S3ObjectSummary> objSummary = new ArrayList<>();

        
        while (true) {
            objSummary.addAll(objList.getObjectSummaries());
            if (objList.isTruncated()) {
                objList = s3Client.listNextBatchOfObjects(objList);
            } else {
                break;
            }
        }

        List<String> allItems = objSummary.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(allItems);

        return ResponseEntity.status(HttpStatus.OK).body(jsonStr);
    }
        
}
