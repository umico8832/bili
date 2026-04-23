package com.bili.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bili.api.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
