package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.framework.mybatisplus.toolkit.PageUtil;
import cn.kuma.blog.main.domain.VO.FileUploadResponse;
import cn.kuma.blog.main.domain.VO.MusicQueryVO;
import cn.kuma.blog.main.domain.VO.MusicVO;
import cn.kuma.blog.main.domain.entity.Music;
import cn.kuma.blog.main.mapper.MusicMapper;
import cn.kuma.blog.main.service.FileService;
import cn.kuma.blog.main.service.MusicService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 音乐服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class MusicServiceImpl implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private FileService fileService;

    @Value("${music.upload.base-dir:uploads/music}")
    private String musicBaseDir;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadMusic(MultipartFile file, Music music, Long userId) {
        // 上传文件
        FileUploadResponse uploadResponse = fileService.uploadFile(musicBaseDir, file);

        // 设置音乐信息
        LocalDateTime now = LocalDateTime.now();
        music.setFilePath(uploadResponse.getFilePath());
        music.setFileUrl(uploadResponse.getFileUrl());
        music.setFileSize(uploadResponse.getFileSize());
        music.setFileType(uploadResponse.getContentType());
        music.setUploadUserId(userId);
        music.setCreateTime(now);
        music.setUpdateTime(now);

        // 初始化统计数据
        if (music.getPlayCount() == null) {
            music.setPlayCount(0);
        }
        if (music.getLikeCount() == null) {
            music.setLikeCount(0);
        }
        if (music.getStatus() == null) {
            music.setStatus(1); // 默认已发布
        }
        if (music.getIsRecommend() == null) {
            music.setIsRecommend(0);
        }

        // 如果未设置名称，使用文件名
        if (StringUtils.isEmpty(music.getName())) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String nameWithoutExt = originalFilename.substring(0, 
                    originalFilename.lastIndexOf('.') > 0 ? originalFilename.lastIndexOf('.') : originalFilename.length());
                music.setName(nameWithoutExt);
            }
        }

        musicMapper.insert(music);
        return music.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMusic(Music music) {
        music.setUpdateTime(LocalDateTime.now());
        return musicMapper.updateById(music) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMusic(Long id) {
        Music music = new Music();
        music.setId(id);
        music.setStatus(2); // 逻辑删除
        music.setUpdateTime(LocalDateTime.now());
        return musicMapper.updateById(music) > 0;
    }

    @Override
    public MusicVO getMusicById(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null || music.getStatus() == 2) {
            return null;
        }

        MusicVO musicVO = new MusicVO();
        BeanUtils.copyProperties(music, musicVO);
        return musicVO;
    }

    @Override
    public PageResult<MusicVO> getMusicList(PageParam pageParam, MusicQueryVO queryVO) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
        if (queryVO == null) {
            queryVO = new MusicQueryVO();
        }

        // 排除已删除的音乐
        queryWrapper.ne("status", 2);

        // 名称模糊查询
        Optional.ofNullable(queryVO.getName())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(name -> queryWrapper.like("name", name));

        // 艺术家模糊查询
        Optional.ofNullable(queryVO.getArtist())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(artist -> queryWrapper.like("artist", artist));

        // 专辑模糊查询
        Optional.ofNullable(queryVO.getAlbum())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(album -> queryWrapper.like("album", album));

        // 分类查询
        Optional.ofNullable(queryVO.getGenre())
                .filter(StringUtils::isNotEmpty)
                .ifPresent(genre -> queryWrapper.eq("genre", genre));

        // 状态查询
        Optional.ofNullable(queryVO.getStatus())
                .ifPresent(status -> queryWrapper.eq("status", status));

        // 是否推荐
        Optional.ofNullable(queryVO.getIsRecommend())
                .ifPresent(isRecommend -> queryWrapper.eq("is_recommend", isRecommend));

        // 排序
        String orderBy = queryVO.getOrderBy();
        String orderDirection = queryVO.getOrderDirection();
        if (StringUtils.isNotEmpty(orderBy)) {
            boolean isAsc = "asc".equalsIgnoreCase(orderDirection);
            switch (orderBy.toLowerCase()) {
                case "playcount":
                    queryWrapper.orderBy(true, isAsc, "play_count");
                    break;
                case "likecount":
                    queryWrapper.orderBy(true, isAsc, "like_count");
                    break;
                case "createtime":
                    queryWrapper.orderBy(true, isAsc, "create_time");
                    break;
                default:
                    queryWrapper.orderByDesc("create_time");
            }
        } else {
            queryWrapper.orderByDesc("create_time");
        }

        // 分页查询
        IPage<Music> musicPage = musicMapper.selectPage(PageUtil.prodPage(pageParam), queryWrapper);

        // 转换为 VO
        List<MusicVO> musicVOList = musicPage.getRecords().stream()
                .map(music -> {
                    MusicVO musicVO = new MusicVO();
                    BeanUtils.copyProperties(music, musicVO);
                    return musicVO;
                })
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<MusicVO> pageResult = new PageResult<>();
        pageResult.setRecords(musicVOList);
        pageResult.setTotal(musicPage.getTotal());

        return pageResult;
    }

    @Override
    public List<MusicVO> getRecommendMusicList(Integer limit) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_recommend", 1)
                    .eq("status", 1)
                    .orderByDesc("create_time")
                    .last("LIMIT " + (limit != null && limit > 0 ? limit : 10));

        List<Music> musicList = musicMapper.selectList(queryWrapper);
        return musicList.stream()
                .map(music -> {
                    MusicVO musicVO = new MusicVO();
                    BeanUtils.copyProperties(music, musicVO);
                    return musicVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MusicVO> getHotMusicList(Integer limit) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .orderByDesc("play_count")
                    .orderByDesc("create_time")
                    .last("LIMIT " + (limit != null && limit > 0 ? limit : 10));

        List<Music> musicList = musicMapper.selectList(queryWrapper);
        return musicList.stream()
                .map(music -> {
                    MusicVO musicVO = new MusicVO();
                    BeanUtils.copyProperties(music, musicVO);
                    return musicVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementPlayCount(Long id) {
        return musicMapper.incrementPlayCount(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLikeCount(Long id) {
        return musicMapper.incrementLikeCount(id) > 0;
    }

    @Override
    public Resource getMusicFile(Long id) {
        Music music = musicMapper.selectById(id);
        if (music == null || music.getStatus() == 2) {
            return null;
        }

        File file = new File(music.getFilePath());
        if (!file.exists()) {
            return null;
        }

        return new FileSystemResource(file);
    }

    @Override
    public List<MusicVO> searchMusic(String keyword, Integer limit) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .and(wrapper -> wrapper.like("name", keyword)
                                          .or()
                                          .like("artist", keyword)
                                          .or()
                                          .like("album", keyword))
                    .orderByDesc("play_count")
                    .orderByDesc("create_time")
                    .last("LIMIT " + (limit != null && limit > 0 ? limit : 20));

        List<Music> musicList = musicMapper.selectList(queryWrapper);
        return musicList.stream()
                .map(music -> {
                    MusicVO musicVO = new MusicVO();
                    BeanUtils.copyProperties(music, musicVO);
                    return musicVO;
                })
                .collect(Collectors.toList());
    }
}
