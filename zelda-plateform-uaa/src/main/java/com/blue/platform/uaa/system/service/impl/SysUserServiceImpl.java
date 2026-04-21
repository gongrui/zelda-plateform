package com.blue.platform.uaa.system.service.impl;

import com.blue.platform.uaa.system.entity.SysUser;
import com.blue.platform.uaa.system.mapper.SysUserMapper;
import com.blue.platform.uaa.system.service.SysUserService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务实现
 *
 * @author gongrui
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOneByQuery(
                QueryWrapper.create()
                        .eq("username", username)
                        .eq("deleted", false)
        );
    }
}
