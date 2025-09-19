package com.tcyh.auth.repository;

import com.tcyh.auth.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 短信验证码Repository接口
 * 
 * @author fp
 * @since 2025-09-18
 */
@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    /**
     * 根据手机号和验证码查找有效的验证码
     */
    @Query("SELECT s FROM SmsCode s WHERE s.phone = :phone AND s.code = :code AND s.codeType = :codeType AND s.used = false AND s.expireTime > :now ORDER BY s.createTime DESC")
    Optional<SmsCode> findValidCode(@Param("phone") String phone, 
                                   @Param("code") String code, 
                                   @Param("codeType") SmsCode.CodeType codeType, 
                                   @Param("now") LocalDateTime now);

    /**
     * 根据手机号查找最新的验证码
     */
    @Query("SELECT s FROM SmsCode s WHERE s.phone = :phone AND s.codeType = :codeType ORDER BY s.createTime DESC")
    Optional<SmsCode> findLatestByPhoneAndType(@Param("phone") String phone, 
                                              @Param("codeType") SmsCode.CodeType codeType);

    /**
     * 检查手机号在指定时间内是否已发送过验证码
     */
    @Query("SELECT COUNT(s) > 0 FROM SmsCode s WHERE s.phone = :phone AND s.codeType = :codeType AND s.createTime > :since")
    boolean existsByPhoneAndTypeSince(@Param("phone") String phone, 
                                     @Param("codeType") SmsCode.CodeType codeType, 
                                     @Param("since") LocalDateTime since);
}
