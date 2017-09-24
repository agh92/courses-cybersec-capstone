package org.coursera.cybersecurity.capstone.group8.internal.data;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.coursera.cybersecurity.capstone.group8.internal.UserManagement;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="users") // Because "user" is a keyword in SQL
public class User implements UserDetails {
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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(UserManagement.AUTHORITY_USER);
	}

	@Override
	public String getPassword() {
		return saltedPasswordHash;
	}

	@Override
	public String getUsername() {
		return id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
