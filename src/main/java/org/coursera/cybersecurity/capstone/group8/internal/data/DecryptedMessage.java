package org.coursera.cybersecurity.capstone.group8.internal.data;

import org.coursera.cybersecurity.capstone.group8.internal.CryptoEngine;

/**
 * @see Message
 * @see CryptoEngine 
 */
public class DecryptedMessage {
	private long id;
	private String fromUserId;
	private String toUserId;
	private long timestamp;
	private String plainTextMessage;
	
	public DecryptedMessage() {

	}

	public DecryptedMessage(Message encryptedMsg, String plainText) {
		this.id = encryptedMsg.getId();
		this.fromUserId = encryptedMsg.getFromUserId();
		this.toUserId = encryptedMsg.getToUserId();
		this.timestamp = encryptedMsg.getTimestamp();
		this.plainTextMessage = plainText;
	}

	public DecryptedMessage(User user, String recipientId, String message) {
		this.fromUserId = user.getId();
		this.toUserId = recipientId;
		this.timestamp = System.currentTimeMillis();
		this.plainTextMessage = message;
	}

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
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getPlainTextMessage() {
		return plainTextMessage;
	}
	
	public void setPlainTextMessage(String plainTextMessage) {
		this.plainTextMessage = plainTextMessage;
	}
}
