package com.quietsage;

import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.quietsage.TableName;

public class BookListData {
	public static final String DATABASE_URI = "mongodb://bworm:ouat@ds033828.mongolab.com:33828/glitretind";
	public static final String DATABASE_NAME = "glitretind";

	private static MongoClient _mongoClient = null;
	private static DB _db = null;

public static void create() {
		cleanUp();
		DBCollection coll = _db.getCollection(TableName.BOOKS);
		populateBooks(coll);

	}
	
	private static void cleanUp() {
		try {
			if (_mongoClient == null) {
				_mongoClient = new MongoClient(new MongoClientURI(DATABASE_URI));
				_db = _mongoClient.getDB(DATABASE_NAME);
			}
			
			Set<String> colls = _db.getCollectionNames();
			System.out.print("Mongo Collections: ");
			for (String s : colls) {
			    System.out.print(s + ", ");
			}
			System.out.println("");

			// Remove Users
			DBCollection coll = _db.getCollection(TableName.USERS);
			if (coll.count() > 0)
				coll.drop();

			// Remove Collections
			coll = _db.getCollection(TableName.COLLECTIONS);
			if (coll.count() > 0)
				coll.drop();
			
			// Remove Items
			coll = _db.getCollection(TableName.BOOKS);
			if (coll.count() > 0)
				coll.drop();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * WARNING!!! If you add or change this data, you need to check the tests in ItemListTest.java
	 * @param coll
	 */
    private static void populateBooks(DBCollection coll) {
    	String strUserUuid = addToUsers("Paula");
    	
    	// Collection - One Upon A
    	String strCollectionUuid = addToCollections(strUserUuid, TableName.COLLECTIONS, "Once Upon A");
    	
		// Owned: series A
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon a Kiss", "0515133868", "own", "", strUserUuid, "Nora Roberts", strCollectionUuid, "2002", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon a Dream", "051512947X", "own", "", strUserUuid, "Nora Roberts", strCollectionUuid, "2000", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon a Star", "0515127000", "own", "", strUserUuid, "Nora Roberts", strCollectionUuid, "1999", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon a Castle", "0515122416", "own", "", strUserUuid, "Nora Roberts", strCollectionUuid, "1998", "public", "Jove", "pocket"));

		// Wanted: series A
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon A Midnight", "0515136190", "want", "false", strUserUuid, "Nora Roberts", strCollectionUuid, "2003", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Once Upon A Rose", "0515131660", "want", "false", strUserUuid, "Nora Roberts", strCollectionUuid, "2001", "public", "Jove", "pocket"));

    	// Collection - In Death
    	strCollectionUuid = addToCollections(strUserUuid, TableName.COLLECTIONS, "In Death");

		// Owned: series B
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Silent Night", "0515123854", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "1998", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Out of this World", "0515131091", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "2001", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Bump in the Night", "0515141178", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "2006", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Suite 606", "978-0425224441", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "2008", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "The Other Side", "978-0515148671", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "2010", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "The Unquiet", "978-0515149982", "own", "", strUserUuid, "JD Robb", strCollectionUuid, "2011", "public", "Jove", "pocket"));

		// Wanted: series B
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Dead of Night", "978-0515143676", "want", "false", strUserUuid, "JD Robb", strCollectionUuid, "2007", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "The Lost", "978-0515147186", "want", "false", strUserUuid, "JD Robb", strCollectionUuid, "2009", "public", "Jove", "pocket"));
		coll.insert(populateBookDoc(UUID.randomUUID().toString(), "Mirror Mirror", "978-0515154078", "want", "false", strUserUuid, "JD Robb", strCollectionUuid, "2013", "public", "Jove", "pocket"));
    	
    }
    
    private static BasicDBObject populateBookDoc(String id, String name, String itemId, 
    		String state, String found,
    		String user, String author, String collection, String sequence,
    		String privacy, String manufacturer, String format) {
    	// Make the Collections value an array
    	BasicDBList collectionsField = new BasicDBList();
    	collectionsField.add(collection);
    	
		BasicDBObject doc = new BasicDBObject("name", name)
				.append("itemId", itemId)
				.append("state", state)
				.append("found", found)
				.append("user", user)
				.append("author", author)
				.append("collections", collectionsField)
				.append("sequence", sequence)
				.append("privacy", privacy)
				.append("manufacturer", manufacturer)
				.append("format", format)
				.append("_id", id);
    	
		return doc;
    }
    
    /**
     * @param strCollectionName
     * @return
     */
    private static String addToUsers(String strCollectionName){
		// Create Items
		String strUuid = UUID.randomUUID().toString();
		BasicDBObject doc = new BasicDBObject("name", strCollectionName).
				append("_id", strUuid);
		_db.getCollection(TableName.USERS).insert(doc);

    	return strUuid;
    }

    private static String addToCollections(String strUserUuid, String strTableName, String strCollectionName){
		// Create Items
		String strUuid = UUID.randomUUID().toString();
		BasicDBObject doc = new BasicDBObject("name", strCollectionName).
				append("_id", strUuid).append("user", strUserUuid);
		_db.getCollection(strTableName).insert(doc);

    	return strUuid;
    }
 	
}
