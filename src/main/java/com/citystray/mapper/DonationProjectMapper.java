package com.citystray.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citystray.entity.DonationProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DonationProjectMapper extends BaseMapper<DonationProject> {

    @Update("UPDATE donation_project SET raised_amount = raised_amount + #{amount}, donor_count = donor_count + 1 WHERE id = #{projectId} AND deleted = 0")
    int addDonation(@Param("projectId") Long projectId, @Param("amount") java.math.BigDecimal amount);
}
