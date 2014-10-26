package com.mongodbuniversity;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.quietsage.QueryCriteria;

public class M101jTest {
	public static final String DATABASE_URI = "localhost";
	public static final String DATABASE_NAME = "asdf";

	private static MongoClient _mongoClient = null;
	private static DB _db = null;

	/**
		db.albums.ensureIndex({images:1})
		> db.images.find({tags:"sunrises"}).count()
		49887
		> db.images.find({tags:"sunrises"}).count()
		44787
		> db.images.count()
		89737
	 */
	@Test
	public void final7() {
		if (_mongoClient == null) {
			try {
				_mongoClient = new MongoClient(DATABASE_URI);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_db = _mongoClient.getDB(DATABASE_NAME);
		}
		
        DBCollection images = _db.getCollection("images");
        DBCollection albums = _db.getCollection("albums");
        
		DBCursor cursor = images.find();
		List<DBObject> results = cursor.toArray();
		Iterator<DBObject>i = results.iterator();
//		HashMap<String, DBObject> hm = new HashMap<String, DBObject>();
		
		while (i.hasNext()) {
			DBObject o = i.next();
			
			BasicDBObject albumQueryCriteria = new BasicDBObject("images", o.get("_id"));
			int imageCount = albums.find(albumQueryCriteria).count();
			if (imageCount == 0)
				images.remove(new BasicDBObject("_id", o.get("_id")));
		}

	}
	
	
	//  x = {"animal":"giraffe"}
	@Test
	public void testFinal8() {
		try {
			if (_mongoClient == null) {
				_mongoClient = new MongoClient(DATABASE_URI);
				_db = _mongoClient.getDB(DATABASE_NAME);
			}
		
            DBCollection animals = _db.getCollection("animals");

            BasicDBObject animal = new BasicDBObject("animal", "monkey");

            animals.insert(animal);
            animal.removeField("animal");
            animal.append("animal", "cat");
            animals.insert(animal);
            animal.removeField("animal");
            animal.append("animal", "lion");
            animals.insert(animal);			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
