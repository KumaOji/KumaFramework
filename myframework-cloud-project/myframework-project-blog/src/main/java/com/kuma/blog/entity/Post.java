package com.kuma.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
}
