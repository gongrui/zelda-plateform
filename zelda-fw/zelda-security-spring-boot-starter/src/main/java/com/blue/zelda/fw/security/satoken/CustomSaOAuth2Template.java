package com.blue.zelda.fw.security.satoken;

import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSaOAuth2Template extends SaOAuth2Template {

    // 构造器注入
    public CustomSaOAuth2Template() {
        super();
    }

    @Override
    public void saveCode(CodeModel codeModel) {

    }

}
