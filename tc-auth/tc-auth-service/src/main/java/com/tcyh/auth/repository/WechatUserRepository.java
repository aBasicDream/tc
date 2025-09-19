package com.tcyh.auth.repository;

import com.tcyh.auth.entity.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 微信用户Repository接口
 * 
 * @author fp
 * @since 2025-09-18
 */
@Repository
public interface WechatUserRepository extends JpaRepository<WechatUser, Long> {

    /**
     * 根据OpenID查找微信用户
     */
    Optional<WechatUser> findByOpenId(String openId);

    /**
     * 根据UnionID查找微信用户
     */
    Optional<WechatUser> findByUnionId(String unionId);

    /**
     * 根据用户ID查找微信用户
     */
    Optional<WechatUser> findByUserId(Long userId);

    /**
     * 检查OpenID是否存在
     */
    boolean existsByOpenId(String openId);

    /**
     * 检查UnionID是否存在
     */
    boolean existsByUnionId(String unionId);
}
