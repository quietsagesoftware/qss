package com.quietsageguice;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.quietsage.TableName;
import com.quietsageguice.GuiceJunitRunner.GuiceModules;

@RunWith(GuiceJunitRunner.class) 
@GuiceModules({ MainModule.class })
public class GuiceTest {
	public static final String DATABASE_URI = "mongodb://bworm:ouat@ds033828.mongolab.com:33828/glitretind";
	public static final String DATABASE_NAME = "glitretind";

	@Inject private ConnectionManager connectionManager;

	@Before
    public void setUp() {
		DB _db = connectionManager.getConnection();
		
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
			
    }

   @Test
   public void testAsdf() {
	   System.out.println("hello");
   }
		
}
