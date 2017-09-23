package org.coursera.cybersecurity.capstone.group8.internal.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/*
 * This is a direct reflection of what is stored in the database, 
 * hence message is an encrypted byte array.
 */
@Entity
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String fromUserId;
	private String toUserId;
	private long timestamp;
	private byte[] encryptedMessage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getFromUserId() {
		return fromUserId;
	}
	
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	
	public String getToUserId() {
		return toUserId;
	}
	
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	
	public byte[] getEncryptedMessage() {
		return encryptedMessage;
	}
	
	public void setEncryptedMessage(byte[] encryptedMessage) {
		this.encryptedMessage = encryptedMessage;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
