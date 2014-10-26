package com.quietsage;

import org.junit.*;

import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.quietsage.QueryCriteria;
import com.quietsage.TableName;

public class BookListTest {
	private static String USER_PAULA = "Paula";

	private static MongoClient _mongoClient = null;
	private static DB _db = null;
	
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code   
    	System.out.println("@BeforeClass - oneTimeSetUp");
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    	System.out.println("@AfterClass - oneTimeTearDown");
    }
 
    @Before
    public void setUp() {
    	// Setup database connection for tests
		if (_mongoClient == null) {
			try {
				_mongoClient = new MongoClient(new MongoClientURI(BookListData.DATABASE_URI));
				_db = _mongoClient.getDB(BookListData.DATABASE_NAME);

				// Setup all data needed for tests in this class/file
		    	BookListData.create();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("ItemListTest.setUp() has an exception executing MongoClient.getDB()");
			}
		}
    	
    }
 
    @After
    public void tearDown() {
        System.out.println("@After - tearDown");
    }
 
    @Test
    public void testUsersTable() {
		DBCollection coll = _db.getCollection(TableName.USERS);
		assertTrue(coll.count() == 1);
		
		// Check that both Mark and Paula appear in the data
		DBObject obj = coll.findOne(QueryCriteria.getByName(USER_PAULA));
		assertTrue(USER_PAULA.equalsIgnoreCase(obj.get("name").toString()));
    }
    
    @Test
    public void testCollectionsTable() {
		DBCollection coll = _db.getCollection(TableName.COLLECTIONS);
		assertTrue(coll.count() == 2);
		
		// Check that both 'In Death' and 'Once Upon A' appear in the data
		DBObject obj = coll.findOne(QueryCriteria.getByName("In Death"));
		assertTrue(getUserUUID(USER_PAULA).equalsIgnoreCase(obj.get("user").toString()));
		obj = coll.findOne(QueryCriteria.getByName("Once Upon A"));
		assertTrue(getUserUUID(USER_PAULA).equalsIgnoreCase(obj.get("user").toString()));
    }
    
    /**
     * When the counts for each collection come back correctly, not only are
     * the counts verified, but also this implies that data integrity with the
     * documents in the Users and Collections tables is in place.  This is
     * true because the counts of the Items are based on UUID references found
     * in both the Users and Collections tables.
     */
   @Test
   public void testItemsTable() {
		DBCollection coll = _db.getCollection(TableName.BOOKS);
		assertTrue(coll.count() == 15);
	   
		DBCursor cursor = coll.find(QueryCriteria.getByUserCollection(getUserUUID(USER_PAULA), getCollectionUUID("In Death")));
		assertTrue(cursor.count() == 9);

		cursor = coll.find(QueryCriteria.getByUserCollection(getUserUUID(USER_PAULA), getCollectionUUID("Once Upon A")));
		assertTrue(cursor.count() == 6);
}

   /**
    * This method is used to find the UUID of a specific Users document.
    * @return
    */
	private String getUserUUID(String strUserName) {
		return getUUID(TableName.USERS, QueryCriteria.getByName(strUserName), "_id");
	}
	
   /**
    * This method is used to find the UUID of a specific Collections document.
    * @return
    */
	private String getCollectionUUID(String strCollectionName) {
		return getUUID(TableName.COLLECTIONS, QueryCriteria.getByName(strCollectionName), "_id");
	}

	private String getUUID(String strTablename, DBObject queryCriteria, String strFieldToReturn) {
		DBCollection coll = _db.getCollection(strTablename);
		DBObject obj = coll.findOne(queryCriteria);
		
		return obj.get(strFieldToReturn).toString();
		
	}
	
}