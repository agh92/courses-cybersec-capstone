package org.coursera.cybersecurity.capstone.group8.internal.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.coursera.cybersecurity.capstone.group8.internal.CryptoEngine;

/**
 * This is a direct reflection of what is stored in the database, 
 * hence message is an encrypted byte array. Conversion to DecryptedMessage 
 * done by CryptoEngine.
 * 
 * @see DecryptedMessage
 * @see CryptoEngine
 */
@Entity
@Table(name="messages")
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String fromUserId;
	private String toUserId;
	private long timestamp;
	// There's no default length for underlying type 
	@Column(name="encrypted_message", length=1024) 
	private byte[] encryptedMessage;
	
	public Message() {
		
	}

	public Message(DecryptedMessage decryptedMsg, byte[] cipherText) {
		this.id = decryptedMsg.getId();
		this.fromUserId = decryptedMsg.getFromUserId();
		this.toUserId = decryptedMsg.getToUserId();
		this.timestamp = decryptedMsg.getTimestamp();
		this.encryptedMessage = cipherText;
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
