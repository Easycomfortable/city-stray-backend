package com.citystray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citystray.entity.SysNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    @Select("SELECT COUNT(*) FROM sys_notification WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    int countUnread(@Param("userId") Long userId);

    @Update("UPDATE sys_notification SET is_read = 1, read_time = NOW() WHERE user_id = #{userId} AND is_read = 0 AND deleted = 0")
    int markAllRead(@Param("userId") Long userId);
}
