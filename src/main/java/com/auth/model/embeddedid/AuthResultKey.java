package com.auth.model.embeddedid;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class AuthResultKey implements Serializable {

	private static final long serialVersionUID = -7114584653374743960L;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "apply_user_id")
	private String applyUserId;

	public AuthResultKey() {}
}
