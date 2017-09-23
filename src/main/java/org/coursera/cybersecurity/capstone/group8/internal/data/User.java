package org.coursera.cybersecurity.capstone.group8.internal.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	@Id
	private String id;
	
	private String saltedPasswordHash;
	private String realName;
	
	public User() {
		
	}

	public User(String userId, String saltedPasswordHash, String realName) {
		this.id = userId;
		this.saltedPasswordHash = saltedPasswordHash;
		this.realName = realName;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSaltedPasswordHash() {
		return saltedPasswordHash;
	}
	
	public void setSaltedPasswordHash(String saltedPasswordHash) {
		this.saltedPasswordHash = saltedPasswordHash;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
}
