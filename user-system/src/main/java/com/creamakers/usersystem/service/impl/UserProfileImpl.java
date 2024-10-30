package com.creamakers.usersystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creamakers.usersystem.consts.HttpCode;
import com.creamakers.usersystem.consts.SuccessMessage;
import com.creamakers.usersystem.dto.request.UserProfileRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.mapper.UserProfileMapper;
import com.creamakers.usersystem.po.User;
import com.creamakers.usersystem.po.UserProfile;
import com.creamakers.usersystem.service.UserProfileService;
import com.creamakers.usersystem.service.UserService;
import com.creamakers.usersystem.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserProfileImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @Override
    public GeneralResponse getProfile(String accessToken) {
        String username = jwtUtil.getUserNameFromToken(accessToken);

        User user = userService.getUserByUsername(username);

        LambdaQueryWrapper<UserProfile> lambdaQueryWrapper = Wrappers.lambdaQuery(UserProfile.class)
                .eq(UserProfile::getUserId, user.getUserId())
                .eq(UserProfile::getIsDeleted, 0);
        UserProfile userProfile = baseMapper.selectOne(lambdaQueryWrapper);
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.DATA_RETRIEVED)
                .data(userProfile)
                .build();
    }

    /**
     * 根据ID来获取用户展示信息
     */
    public GeneralResponse getProfileByID(String userID) {
        LambdaQueryWrapper<UserProfile> queryWrapper = Wrappers.lambdaQuery(UserProfile.class)
                .eq(UserProfile::getUserId, userID);
        UserProfile userProfile = baseMapper.selectOne(queryWrapper);
        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.DATA_RETRIEVED)
                .data(userProfile)
                .build();
    }

    @Override
    public GeneralResponse updateInfo(UserProfileRequest request,String accessToken) {
        String userNameFromToken = jwtUtil.getUserNameFromToken(accessToken);
        User user = userService.getUserByUsername(userNameFromToken);
        user.getUserId();
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(request, userProfile);

        lambdaUpdate()
                .eq(UserProfile::getUserId, user.getUserId())
                .eq(UserProfile::getIsDeleted, 0)
                .update(userProfile);

        return GeneralResponse.builder()
                .code(HttpCode.OK)
                .msg(SuccessMessage.USER_UPDATED)
                .data(true)
                .build();
    }


    @Override
    public Boolean initializeUserProfile(Integer userId) {
        UserProfile userProfile = UserProfile.builder()
                .userId(userId)
                .avatarUrl("https://pic.imgdb.cn/item/671e5e17d29ded1a8c5e0dbe.jpg")
                .bio("这个人很懒，没有写任何描述")
                .userLevel(1)
                .gender(2)
                .grade("大学一年级")
                .location("中国")
                .birthDate(new Date())
                .build();
       return save(userProfile);
    }
}
