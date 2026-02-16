package cn.kuma.blog.main.service;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.main.domain.VO.MusicQueryVO;
import cn.kuma.blog.main.domain.VO.MusicVO;
import cn.kuma.blog.main.domain.entity.Music;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 音乐服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface MusicService {

    /**
     * 上传音乐文件
     *
     * @param file 音乐文件
     * @param music 音乐信息
     * @param userId 上传用户ID
     * @return 音乐ID
     */
    Long uploadMusic(MultipartFile file, Music music, Long userId);

    /**
     * 更新音乐信息
     *
     * @param music 音乐实体
     * @return 是否成功
     */
    boolean updateMusic(Music music);

    /**
     * 根据ID删除音乐（逻辑删除）
     *
     * @param id 音乐ID
     * @return 是否成功
     */
    boolean deleteMusic(Long id);

    /**
     * 根据ID查询音乐
     *
     * @param id 音乐ID
     * @return 音乐视图对象
     */
    MusicVO getMusicById(Long id);

    /**
     * 分页查询音乐列表
     *
     * @param pageParam 分页参数
     * @param queryVO 查询条件
     * @return 分页结果
     */
    PageResult<MusicVO> getMusicList(PageParam pageParam, MusicQueryVO queryVO);

    /**
     * 获取推荐音乐列表
     *
     * @param limit 数量限制
     * @return 音乐列表
     */
    List<MusicVO> getRecommendMusicList(Integer limit);

    /**
     * 获取热门音乐列表
     *
     * @param limit 数量限制
     * @return 音乐列表
     */
    List<MusicVO> getHotMusicList(Integer limit);

    /**
     * 增加播放次数
     *
     * @param id 音乐ID
     * @return 是否成功
     */
    boolean incrementPlayCount(Long id);

    /**
     * 增加点赞数
     *
     * @param id 音乐ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long id);

    /**
     * 获取音乐文件资源（支持Range请求）
     *
     * @param id 音乐ID
     * @return 文件资源
     */
    Resource getMusicFile(Long id);

    /**
     * 搜索音乐
     *
     * @param keyword 关键词
     * @param limit 数量限制
     * @return 音乐列表
     */
    List<MusicVO> searchMusic(String keyword, Integer limit);
}
