package com.bili.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bili.api.dto.VideoVO;
import com.bili.api.entity.Video;
import com.bili.api.mapper.VideoMapper;
import com.bili.api.service.VideoService;
import org.springframework.stereotype.Service;

/**
 * 视频合集 Service 实现
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Override
    public IPage<VideoVO> pageVideoVO(int page, int size, Integer categoryId, String keyword) {
        // 构造 MyBatis-Plus 分页对象（页码从 1 开始）
        Page<VideoVO> pageParam = new Page<>(page, size);
        return baseMapper.pageVideoVO(pageParam, categoryId, keyword);
    }
}
