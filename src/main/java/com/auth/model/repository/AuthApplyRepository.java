package com.auth.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.auth.model.embeddedid.AuthApplyKey;
import com.auth.model.entity.AuthApply;

@Repository
public interface AuthApplyRepository extends JpaRepository<AuthApply, AuthApplyKey> {
	
	@Query(value = "UPDATE sv_applies SET apply_name = :applyName, apply_birth = :applyBirth, apply_gender = :applyGender, apply_nationality = :applyNationality, update_user_id = :applyUserId, update_datetime = now() WHERE serial_number = :serialNumber AND apply_user_id = :applyUserId", nativeQuery = true)
	void updateApply(@Param("applyName")String applyName, @Param("applyBirth")String applyBirth, @Param("applyGender")String applyGender,@Param("applyNationality")String applyNationality, @Param("serialNumber")String serialNumber, @Param("applyUserId")String apply_user_id);
}
