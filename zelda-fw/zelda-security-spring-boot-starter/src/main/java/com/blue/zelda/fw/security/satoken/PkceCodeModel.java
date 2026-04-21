package com.blue.zelda.fw.security.satoken;

import cn.dev33.satoken.oauth2.data.model.CodeModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PkceCodeModel extends CodeModel {
    /**
     * PKCE code_challenge
     */
    private String codeChallenge;

    /**
     * PKCE code_challenge_method (plain 或 S256)
     */
    private String codeChallengeMethod;
}
