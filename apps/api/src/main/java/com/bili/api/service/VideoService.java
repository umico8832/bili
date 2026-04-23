package com.bili.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bili.api.dto.VideoVO;
import com.bili.api.entity.Video;

/**
 * 视频合集 Service 接口
 */
public interface VideoService extends IService<Video> {

    /**
     * 分页查询视频列表（含作者信息）
     *
     * @param page       当前页（从 1 开始）
     * @param size       每页条数
     * @param categoryId 分区 ID，null 表示全部
     * @param keyword    搜索关键词，null 表示不过滤
     * @return 分页结果
     */
    IPage<VideoVO> pageVideoVO(int page, int size, Integer categoryId, String keyword);
}
