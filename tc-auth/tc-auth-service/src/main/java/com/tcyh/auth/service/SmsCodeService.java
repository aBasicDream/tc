package com.tcyh.auth.service;

import com.tcyh.auth.entity.SmsCode;
import com.tcyh.auth.repository.SmsCodeRepository;
import com.tcyh.common.constant.ErrorCodeEnum;
import com.tcyh.common.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * 短信验证码服务
 * 
 * @author fp
 * @since 2025-09-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsCodeService {

    private final SmsCodeRepository smsCodeRepository;

    /**
     * 发送短信验证码
     */
    @Transactional
    public void sendSmsCode(String phone, SmsCode.CodeType codeType, String deviceInfo) {
        log.info("Sending SMS code to phone: {}, type: {}", phone, codeType);

        // 检查是否在60秒内已发送过验证码
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        if (smsCodeRepository.existsByPhoneAndTypeSince(phone, codeType, oneMinuteAgo)) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "验证码发送过于频繁，请稍后再试");
        }

        // 生成6位数字验证码
        String code = generateCode();

        // 创建验证码记录
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setCodeType(codeType);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟过期
        smsCode.setDeviceInfo(deviceInfo);

        smsCodeRepository.save(smsCode);

        // 这里应该调用实际的短信服务发送验证码
        // 为了演示，我们只在日志中输出验证码
        log.info("SMS code for phone {}: {}", phone, code);

        // TODO: 集成实际的短信服务
        // smsProvider.sendSms(phone, "您的验证码是：" + code + "，5分钟内有效。");
    }

    /**
     * 验证短信验证码
     */
    @Transactional
    public boolean verifySmsCode(String phone, String code, SmsCode.CodeType codeType) {
        log.info("Verifying SMS code for phone: {}, code: {}, type: {}", phone, code, codeType);

        Optional<SmsCode> smsCodeOpt = smsCodeRepository.findValidCode(phone, code, codeType, LocalDateTime.now());
        
        if (smsCodeOpt.isEmpty()) {
            log.warn("Invalid SMS code for phone: {}", phone);
            return false;
        }

        SmsCode smsCode = smsCodeOpt.get();
        smsCode.setUsed(true);
        smsCode.setUsedTime(LocalDateTime.now());
        smsCodeRepository.save(smsCode);

        log.info("SMS code verified successfully for phone: {}", phone);
        return true;
    }

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 检查验证码是否有效
     */
    public boolean isValidCode(String phone, String code, SmsCode.CodeType codeType) {
        return smsCodeRepository.findValidCode(phone, code, codeType, LocalDateTime.now()).isPresent();
    }
}
