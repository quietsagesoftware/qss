package com.quietsage.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.quietsage.QueryCriteria;
import com.quietsage.Server;
import com.quietsage.TableName;
import com.quietsage.db.ConnectionManager;

@Path("/collections")
public class BookBags extends ResourceBase {
	
	@Inject
	BookBags(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * This method returns an array of collections for the specified user.  Each
	 * book in the array contains a document specifying the name of the collection
	 * and how many books are in it.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getListByUser() {
		AggregationOutput agg = getCollections(null);
		HashMap<String, DBObject> collectionsNames = getCollectionNames();
		
		String strDocs = "[";
		String strComma = "";

		for (DBObject result : agg.results()) {
			Object obj = result.get("_id");
			result.put("name", collectionsNames.get(obj.toString()).get("name"));
			result.put("href", Server.getBaseUrl() +"/collections/" + obj.toString());
			strDocs += strComma;
			strDocs += result;
			strComma = ",";
		}
		
		strDocs += "]";

		return strDocs;
	}

	/**
	 * This method returns the documents for the specified user
	 * and collection.  The number of books in the collection is a part of the
	 * response too.
	 * 
	 * @param strCollection a String containing the id of the collection to query
	 * @return a String in JSON format containing all of the documents to be returned
	 */
	@GET
	@Path("/{collectionuuid:[A-Za-z0-9\\-]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getListByUserCollection(@PathParam("collectionuuid") final String strCollectionUuid) {
		List<DBObject> queryResults = getBooks(QueryCriteria.getByUserCollection(getUserUuid(), strCollectionUuid));
		addHrefToBooks(queryResults);
		transformCollectionsField(queryResults);

		AggregationOutput agg = getCollections(strCollectionUuid);
		HashMap<String, DBObject> collectionsNames = getCollectionNames();
		
		String strDocs = "";
		String strComma = "";

		for (DBObject result : agg.results()) {
			result.put("name", collectionsNames.get(strCollectionUuid).get("name"));
			result.put("href", Server.getBaseUrl() +"/collections/" + strCollectionUuid);
			result.put("books", queryResults);
			strDocs += strComma;
			strDocs += result;
			strComma = ",";
		}
		
		return strDocs;
	}

	/**
	 * This method adds an href field to each 'book' which contains the URL for
	 * the individual book.
	 * @param coll
	 */
	private void addHrefToBooks(List<DBObject> coll) {
		Iterator<DBObject> i = coll.iterator();
		while (i.hasNext()) {
			DBObject obj = i.next();
			obj.put("href", Server.getBaseUrl() + "/books/" + obj.get("_id").toString());
		}
	}
	
	/**
	 * This method gets all of the Collections documents for a user
	 * and returns them.
	 * 
	 * @return List<DBObject> containing the collection documents for a specific user
	 */
	private HashMap<String, DBObject> getCollectionNames() {
		DB db = connectionManager.getConnection();
		DBCollection coll = db.getCollection(TableName.COLLECTIONS);
		DBCursor cursor = coll.find(QueryCriteria.getByUser(getUserUuid()));
		List<DBObject> results = cursor.toArray();
		Iterator<DBObject>i = results.iterator();
		HashMap<String, DBObject> hm = new HashMap<String, DBObject>();
		
		while (i.hasNext()) {
			DBObject o = i.next();
			hm.put(o.get("_id").toString(), o);
		}
		
		return hm;
	}
	
	/**
	 * This method performs an aggregation to find what collections a user has.
	 * With each collection, the number of books in each colleciton, 'numbooks', 
	 * is also determined.
	 * 
	 * Sample Syntax:
	 * db.books.aggregate([
	 * {"$unwind":"$collections"},
	 * {"$match":{"user":"2a7b4099-684e-4b6e-80b8-319772d99fff", "collections":"4c20201a-a237-4d2c-82df-c3188bcd2c5c"}},
	 * {"$group":{"_id":"$collections", "count":{"$sum":1}}}
	 * ])
	 *  
	 * @param strCollectionUuid if not specified, then get all collections
	 * @return
	 */
	private AggregationOutput getCollections(String strCollectionUuid) {
		DB db = connectionManager.getConnection();
		DBCollection coll = db.getCollection(TableName.BOOKS);
		
		// $unwind
		DBObject unwind = new BasicDBObject("$unwind", "$collections");
		
		// $match
		DBObject filter = new BasicDBObject("user", getUserUuid());
		if (strCollectionUuid != null)
			filter.put("collections", strCollectionUuid);
		DBObject match = new BasicDBObject("$match", filter);

		// $group
		BasicDBObject groupFields = new BasicDBObject( "_id", "$collections");
		groupFields.append("count", new BasicDBObject( "$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		
		// run the aggregation
		List<DBObject> pipeline = Arrays.asList(unwind, match, group);
		AggregationOutput agg = coll.aggregate(pipeline);

		return agg;
	}
	
	/**
	 * This method returns a list of 'book' objects in a List.
	 * @param queryCriteria
	 * @return a list of 'book' objects
	 */
	private List<DBObject> getBooks(DBObject queryCriteria) {
		DB db = connectionManager.getConnection();
		DBCollection coll = db.getCollection(TableName.BOOKS);
		DBCursor cursor = coll.find(queryCriteria);
		List<DBObject> queryResults = cursor.toArray();
		
		return queryResults;
	}
	
	/**
	 * This method traverses a list of books and transforms the content in the
	 * 'collections' field.
	 * @param bookList
	 */
	private void transformCollectionsField(List<DBObject> bookList) {
		Iterator<DBObject>i = bookList.iterator();
		
		while (i.hasNext()) {
			DBObject o = i.next();
			replaceCollectionUuidsWObjRefs(o);
		}
	}
	
}

