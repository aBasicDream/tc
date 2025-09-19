package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserBrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户浏览历史表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserBrowseHistoryMapper extends BaseMapper<UserBrowseHistory> {

    /**
     * 查询用户的浏览历史
     * 
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param limit 限制条数
     * @return 浏览历史列表
     */
    List<UserBrowseHistory> selectBrowseHistoryByUserId(@Param("userId") Long userId, 
                                                       @Param("contentType") Integer contentType, 
                                                       @Param("limit") Integer limit);

    /**
     * 删除用户指定时间之前的浏览历史
     * 
     * @param userId 用户ID
     * @param days 保留天数
     * @return 影响行数
     */
    int deleteOldBrowseHistory(@Param("userId") Long userId, @Param("days") Integer days);
}
