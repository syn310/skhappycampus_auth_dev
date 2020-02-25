package com.auth.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auth.model.embeddedid.AuthResultKey;
import com.auth.model.entity.AuthResult;

@Repository
public interface AuthResultRepository extends JpaRepository<AuthResult, AuthResultKey> {

	@Query(value = "SELECT count(1) FROM sv_nice_auths WHERE dup_info = :dupInfo AND serial_number = :serialNumber", nativeQuery = true)
	int findByDupInfoAndSerialNumber(@Param("dupInfo")String dupInfo, @Param("serialNumber")String serialNumber);
}
