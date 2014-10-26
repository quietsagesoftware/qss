package com.quietsage.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.quietsage.QueryCriteria;
import com.quietsage.Server;
import com.quietsage.TableName;
import com.quietsage.db.ConnectionManager;

@Path("/books")
public class Books extends ResourceBase {

	@Inject
	Books(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * This method returns all of the fields for the specified book except for
	 * the database '_id' field and the 'image' field.
	 * @param dbBookId the database id of the document, not to be confused with the manufacturer id
	 * @return a String containing the books fields formatted as JSON
	 */
	@GET
	@Path("/{id:[a-f0-9\\-]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAsJSON(@PathParam("id") final String dbBookId) {
		return getDetail(dbBookId);
	}

	/**
	 * This method returns the number of books that a user has
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getCount() {
		DB db = connectionManager.getConnection();
		DBCollection coll = db.getCollection(TableName.BOOKS);
		DBCursor cursor = coll.find(QueryCriteria.getByUser(getUserUuid()));
		
		return "{count: " + Integer.valueOf(cursor.count()).toString() + "}";
	}
	
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String postbook() {
		return "{\"message\": \"an book Detail was posted to thriftstore.\"}\n";
	}
	
	/**
	 * This method searches the book collection for the specified dbbookId
	 * @param dbBookId the bookId of the book to retrieve
	 * @return the json representation of the document
	 */
	private String getDetail(final String dbBookId) {
		DB db = connectionManager.getConnection();
		DBCollection coll = db.getCollection(TableName.BOOKS);
		DBObject obj = coll.findOne(QueryCriteria.getById(dbBookId));
		
		obj.put("href", Server.getBaseUrl() +"/books/" + dbBookId);
		replaceCollectionUuidsWObjRefs(obj);

		return obj != null ? obj.toString() : "{'notFound':'" + dbBookId + "'}";
	}
}
