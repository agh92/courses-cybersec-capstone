package org.coursera.cybersecurity.capstone.group8.internal.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users") // Because "user" is a keyword in SQL
public class User {
	@Id
	private String id;
	
	private String saltedPasswordHash;
	private String realName;
	private byte[] salt;
	
	public User() {
		
	}

	public User(String userId, String saltedPasswordHash, String realName, byte[] salt) {
		this.id = userId;
		this.saltedPasswordHash = saltedPasswordHash;
		this.realName = realName;
		this.salt = salt;
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
	
	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	@Override
	public String toString() {
		return "User[" + id + "]";
	}
}
