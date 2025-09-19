package com.tcyh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcyh.user.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签表 Mapper 接口
 * 
 * @author fp
 * @since 2025-09-17
 */
@Mapper
public interface UserTagMapper extends BaseMapper<UserTag> {

    /**
     * 查询用户的标签列表
     * 
     * @param userId 用户ID
     * @return 标签列表
     */
    List<UserTag> selectTagsByUserId(@Param("userId") Long userId);

    /**
     * 根据标签类型查询用户标签
     * 
     * @param userId 用户ID
     * @param tagType 标签类型
     * @return 标签列表
     */
    List<UserTag> selectTagsByUserIdAndType(@Param("userId") Long userId, @Param("tagType") Integer tagType);
}
