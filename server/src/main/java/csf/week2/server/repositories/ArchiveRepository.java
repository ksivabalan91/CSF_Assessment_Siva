package csf.week2.server.repositories;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import csf.week2.server.Utils;
import csf.week2.server.models.Bundle;

@Repository
public class ArchiveRepository {

	@Autowired
	private MongoTemplate template;
	private final String COLLECTION = "archives";

	//TODO: Task 4
	public String recordBundle(String title,String name,String comments,List<String> urlList) {
		//! generate unique ID
		String bundleId = UUID.randomUUID().toString().substring(0, 8);
		String uploadDate = LocalDateTime.now().toString();            
		String urlArr = Utils.toJsonStr(urlList);
		System.out.println(urlArr);

		//! insert into archive
		Map<String,String> map = new HashMap<>();
		map.put("bundleId", bundleId);
		map.put("date", uploadDate);
		map.put("title", title);
		map.put("name", name);
		map.put("comments", comments);
		map.put("urls", urlArr);
		
		Document doc = new Document(map);
		template.insert(doc, COLLECTION).toJson();
		return bundleId;
		
	}

	//TODO: Task 5
	// db.archives.find({
	// 	bundleId:'bd84fb3a'
	// })
	public Bundle getBundleByBundleId(String bundleId) {		

		Query query = new Query(Criteria.where("bundleId").is(bundleId));
		List<Bundle> bundle = template.find(query,Bundle.class,COLLECTION);	

		return bundle.get(0);
	}

	//TODO: Task 6
	// db.archives.find({
	// })
	public List<Bundle> getBundles() {
		return template.findAll(Bundle.class,COLLECTION);
	}


}
