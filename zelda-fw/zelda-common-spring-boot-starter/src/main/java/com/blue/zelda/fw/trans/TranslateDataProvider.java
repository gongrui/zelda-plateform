package com.blue.zelda.fw.trans;


import java.util.Map;

public interface TranslateDataProvider {
    Map<String, Map<String, String>> getAllDict();
    Map<String, String> getAllUser();
    Map<String, String> getAllOrg();
    Map<String, String> getAllRole();
}
