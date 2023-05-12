package csf.week2.server.repositories;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class ImageRepository {
	
	@Value("${DO_STORAGE_ENDPOINT}")
	private String endpoint;

	@Value("${DO_STORAGE_BUCKETNAME}")
	private String bucketName;
	@Autowired
	private AmazonS3 s3Client;

	//TODO: Task 3
	// You are free to change the parameter and the return type
	// Do not change the method's name
	public String upload( 
		FileInputStream fileInputStream,
		long fileSize,
		String contentType,
        String fileName,
        String name,
        String title,
        String comments
		) throws IOException {

            //! Create a map to pass in metadata of the object later
            Map<String,String> userDataMap = new HashMap<>();
            userDataMap.put("name",name);
            userDataMap.put("title",title);
            userDataMap.put("uploadDateTime", LocalDateTime.now().toString());
            userDataMap.put("originalFilename", fileName);
            userDataMap.put("comments", comments);

            //! Set METADATA
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(contentType);
            metaData.setContentLength(fileSize);
            metaData.setUserMetadata(userDataMap);

            //! Create putobject Request and configure with public access
            PutObjectRequest putReq = new PutObjectRequest(bucketName, fileName, fileInputStream, metaData);
            putReq.withCannedAcl(CannedAccessControlList.PublicRead);

            //! Execute the request
            s3Client.putObject(putReq);
			System.out.println("uploaded:"+fileName);
			
			String uri = UriComponentsBuilder
			.fromUriString("https://"+bucketName+"."+endpoint)
			.path(fileName)
			.toUriString();
			
			System.out.println("URI:"+uri);

            return uri;
		}
}
