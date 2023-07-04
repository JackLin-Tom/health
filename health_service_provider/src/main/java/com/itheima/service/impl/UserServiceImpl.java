package com.itheima.service.impl;

import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;


    @Override
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username); // 查询用户基本信息，不包含用户角色
        if (user == null){
            return null;
        }

        Integer userId = user.getId();
        // 根据用户id查询对应的角色
        Set<Role> roles = roleDao.findByUserId(userId);
        for (Role role : roles){
            Integer roleId = role.getId();
            // 根据角色id查询关联权限
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            if(permissions != null && permissions.size() > 0){
                role.setPermissions(permissions);
            }
        }
        user.setRoles(roles);

        return user;
    }





}
