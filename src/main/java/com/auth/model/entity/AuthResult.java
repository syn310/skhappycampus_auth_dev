package com.auth.model.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.auth.model.embeddedid.AuthResultKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Entity
@Table(name="sv_nice_auths")
public class AuthResult {

	public AuthResult() {}
	
	@EmbeddedId
	@NotNull
	AuthResultKey authResultKey;
	
	@Column(name = "cipher_time")
	private String cipherTime;
	
	@Column(name = "request_number")
	private String requestNumber;
	
	@Column(name = "response_number")
	private String responseNumber;
	
	@Column(name = "auth_type")
	private String authType;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "dup_info")
	private String dupInfo;
	
	@Column(name = "birth_date")
	private String birthDate;
	
	@Column(name = "gender")
	private String gender;
	
	@Column(name = "national_info")
	private String nationalInfo;
	
	@Column(name = "dup_yn")
	private String dupYn;
}
