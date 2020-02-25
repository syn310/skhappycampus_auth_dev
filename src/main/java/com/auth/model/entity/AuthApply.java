package com.auth.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.auth.model.embeddedid.AuthApplyKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Entity
@Table(name="sv_applies")
public class AuthApply {

	public AuthApply() {}
	
	@EmbeddedId
	@NotNull
	AuthApplyKey authApplyKey;
	
	@Column(name = "apply_name")
	private String applyName;
	
	@Column(name = "apply_nationality")
	private String applyNationality;
	
	@Column(name = "apply_birth")
	private String applyBirth;
	
	@Column(name = "apply_gender")
	private String applyGender;
	
	@Column(name = "apply_phone")
	private String applyPhone;
	
	@Column(name = "apply_address")
	private String applyAddress;
	
	@Column(name = "disability_yn")
	private String disabilityYn;
	
	@Column(name = "military_yn")
	private String militaryYn;
	
	@Column(name = "veterans_yn")
	private String veteransYn;
	
	@Column(name = "apply_status")
	private String applyStatus;
	
	@Column(name = "cover_letter")
	private String coverLetter;
	
	@Column(name = "create_datetime")
	private Date createDatetime;
	
	@Column(name = "create_user_id")
	private String createUserId;
	
	@Column(name = "update_datetime")
	private Date updateDatetime;
	
	@Column(name = "update_user_id")
	private String updateUserId;
	
}
