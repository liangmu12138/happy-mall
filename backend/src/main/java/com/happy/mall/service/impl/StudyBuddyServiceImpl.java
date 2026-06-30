package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.StudyBuddy;
import com.happy.mall.entity.StudyBuddyJoin;
import com.happy.mall.entity.User;
import com.happy.mall.mapper.StudyBuddyJoinMapper;
import com.happy.mall.mapper.StudyBuddyMapper;
import com.happy.mall.mapper.UserMapper;
import com.happy.mall.service.StudyBuddyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习搭子 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class StudyBuddyServiceImpl extends ServiceImpl<StudyBuddyMapper, StudyBuddy> implements StudyBuddyService {

    private final StudyBuddyJoinMapper buddyJoinMapper;
    private final UserMapper userMapper;

    @Override
    public Page<Map<String, Object>> getBuddyPage(Integer pageNum, Integer pageSize, String buddyType, String keyword) {
        LambdaQueryWrapper<StudyBuddy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyBuddy::getStatus, 1);

        if (StringUtils.hasText(buddyType)) {
            wrapper.eq(StudyBuddy::getBuddyType, buddyType);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(StudyBuddy::getTitle, keyword)
                    .or().like(StudyBuddy::getDescription, keyword));
        }

        wrapper.orderByDesc(StudyBuddy::getCreateTime);
        Page<StudyBuddy> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        // 转换为 Map，包含用户信息
        Page<Map<String, Object>> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<Map<String, Object>> records = page.getRecords().stream().map(buddy -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", buddy.getId());
            map.put("buddyType", buddy.getBuddyType());
            map.put("title", buddy.getTitle());
            map.put("description", buddy.getDescription());
            map.put("target", buddy.getTarget());
            map.put("location", buddy.getLocation());
            map.put("timeInfo", buddy.getTimeInfo());
            map.put("maxMembers", buddy.getMaxMembers());
            map.put("currentMembers", buddy.getCurrentMembers());
            map.put("createTime", buddy.getCreateTime());

            // 获取用户信息
            User user = userMapper.selectById(buddy.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }

            return map;
        }).collect(Collectors.toList());
        result.setRecords(records);

        return result;
    }

    @Override
    public Map<String, Object> getBuddyDetail(Long id) {
        StudyBuddy buddy = this.getById(id);
        if (buddy == null || buddy.getStatus() != 1) {
            throw new BusinessException("搭子不存在或已关闭");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("buddy", buddy);

        // 获取发起人信息
        User user = userMapper.selectById(buddy.getUserId());
        if (user != null) {
            user.setPassword(null);
            result.put("user", user);
        }

        // 获取参与人列表
        List<StudyBuddyJoin> joins = buddyJoinMapper.selectList(
                new LambdaQueryWrapper<StudyBuddyJoin>()
                        .eq(StudyBuddyJoin::getBuddyId, id)
                        .eq(StudyBuddyJoin::getStatus, 1));

        List<Map<String, Object>> members = new ArrayList<>();
        for (StudyBuddyJoin join : joins) {
            User member = userMapper.selectById(join.getUserId());
            if (member != null) {
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("id", member.getId());
                memberInfo.put("username", member.getUsername());
                memberInfo.put("nickname", member.getNickname());
                memberInfo.put("avatar", member.getAvatar());
                members.add(memberInfo);
            }
        }
        result.put("members", members);

        return result;
    }

    @Override
    public void createBuddy(StudyBuddy buddy) {
        if (buddy.getMaxMembers() == null) buddy.setMaxMembers(1);
        if (buddy.getCurrentMembers() == null) buddy.setCurrentMembers(1);
        if (buddy.getStatus() == null) buddy.setStatus(1);
        this.save(buddy);
    }

    @Override
    public void updateBuddy(StudyBuddy buddy) {
        if (buddy.getId() == null) {
            throw new BusinessException("搭子ID不能为空");
        }
        this.updateById(buddy);
    }

    @Override
    public void deleteBuddy(Long id, Long userId) {
        StudyBuddy buddy = this.getById(id);
        if (buddy == null) {
            throw new BusinessException("搭子不存在");
        }
        if (!buddy.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的搭子");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void joinBuddy(Long buddyId, Long userId) {
        StudyBuddy buddy = this.getById(buddyId);
        if (buddy == null || buddy.getStatus() != 1) {
            throw new BusinessException("搭子不存在或已关闭");
        }

        // 检查是否已满
        if (buddy.getCurrentMembers() >= buddy.getMaxMembers()) {
            throw new BusinessException("人数已满");
        }

        // 检查是否已加入
        Long count = buddyJoinMapper.selectCount(
                new LambdaQueryWrapper<StudyBuddyJoin>()
                        .eq(StudyBuddyJoin::getBuddyId, buddyId)
                        .eq(StudyBuddyJoin::getUserId, userId));
        if (count > 0) {
            throw new BusinessException("你已经加入了");
        }

        // 添加参与记录
        StudyBuddyJoin join = new StudyBuddyJoin();
        join.setBuddyId(buddyId);
        join.setUserId(userId);
        join.setStatus(1); // 直接确认
        buddyJoinMapper.insert(join);

        // 更新人数
        buddy.setCurrentMembers(buddy.getCurrentMembers() + 1);
        this.updateById(buddy);
    }

    @Override
    @Transactional
    public void leaveBuddy(Long buddyId, Long userId) {
        StudyBuddy buddy = this.getById(buddyId);
        if (buddy == null) {
            throw new BusinessException("搭子不存在");
        }

        // 不能退出自己发布的搭子
        if (buddy.getUserId().equals(userId)) {
            throw new BusinessException("不能退出自己发布的搭子");
        }

        // 删除参与记录
        buddyJoinMapper.delete(
                new LambdaQueryWrapper<StudyBuddyJoin>()
                        .eq(StudyBuddyJoin::getBuddyId, buddyId)
                        .eq(StudyBuddyJoin::getUserId, userId));

        // 更新人数
        if (buddy.getCurrentMembers() > 1) {
            buddy.setCurrentMembers(buddy.getCurrentMembers() - 1);
            this.updateById(buddy);
        }
    }
}
