package com.bili.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bili.api.dto.VideoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.bili.api.entity.Video;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 分页查询视频列表（连表获取作者信息）
     *
     * @param page       分页参数
     * @param categoryId 分区 ID，null 表示全部
     * @param keyword    关键词，null 表示不过滤
     * @return 视频 VO 分页结果
     */
    IPage<VideoVO> pageVideoVO(
            Page<VideoVO> page,
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword
    );
}
