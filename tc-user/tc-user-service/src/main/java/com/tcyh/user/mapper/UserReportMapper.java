package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户举报表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserReportMapper extends BaseMapper<UserReport> {

    /**
     * 查询待处理的举报列表
     * 
     * @param status 处理状态
     * @return 举报列表
     */
    List<UserReport> selectReportsByStatus(@Param("status") Integer status);

    /**
     * 查询用户提交的举报记录
     * 
     * @param reporterId 举报人ID
     * @return 举报记录列表
     */
    List<UserReport> selectReportsByReporterId(@Param("reporterId") Long reporterId);

    /**
     * 统计指定内容的举报数量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 举报数量
     */
    Long countReportsByContent(@Param("contentId") Long contentId, @Param("contentType") Integer contentType);
}
