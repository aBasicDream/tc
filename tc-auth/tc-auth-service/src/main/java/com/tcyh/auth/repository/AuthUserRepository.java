package com.tcyh.auth.repository;

import com.tcyh.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户Repository接口
 * 
 * @author fp
 * @since 2025-09-18
 */
@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<AuthUser> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<AuthUser> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<AuthUser> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据用户名或邮箱查找用户
     */
    @Query("SELECT u FROM AuthUser u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<AuthUser> findByUsernameOrEmail(@Param("identifier") String identifier);
}
