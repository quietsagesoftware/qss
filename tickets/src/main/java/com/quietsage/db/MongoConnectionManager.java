package com.quietsage.db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

class MongoConnectionManager implements ConnectionManager {
	public static final String DATABASE_NAME = "glitretind";
	public static final String DATABASE_URI = "mongodb://bworm:ouat@ds033828.mongolab.com:33828/glitretind";

	/** This member provides access to the mongoDB database */
	private static DB m_db;

	
	public DB getConnection() {
		if (m_db == null)
			m_db = init();
			
		return m_db;
	}

	public String getDatabaseUri() {
		return DATABASE_URI;
	}
	
	private DB init() {
		// TODO Auto-generated method stub
		
		MongoClient mongoClient = null;
    	MongoClientURI uri = new MongoClientURI(DATABASE_URI);
		try {
			mongoClient = new MongoClient(uri);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		System.out.println("ouch");
		}
    		
		DB db = null;
		if (mongoClient != null) {
			db = mongoClient.getDB(DATABASE_NAME);
		}
		
		return db;
	}
	
}



