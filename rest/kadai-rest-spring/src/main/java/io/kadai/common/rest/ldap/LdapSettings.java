package io.kadai.common.rest.ldap;

import org.springframework.core.env.Environment;

/** Required settings to run ldap. */
enum LdapSettings {
  KADAI_LDAP_USER_SEARCH_BASE("kadai.ldap.userSearchBase"),
  KADAI_LDAP_USER_SEARCH_FILTER_NAME("kadai.ldap.userSearchFilterName"),
  KADAI_LDAP_USER_SEARCH_FILTER_VALUE("kadai.ldap.userSearchFilterValue"),
  KADAI_LDAP_USER_FIRSTNAME_ATTRIBUTE("kadai.ldap.userFirstnameAttribute"),
  KADAI_LDAP_USER_LASTNAME_ATTRIBUTE("kadai.ldap.userLastnameAttribute"),
  KADAI_LDAP_USER_FULLNAME_ATTRIBUTE("kadai.ldap.userFullnameAttribute"),
  KADAI_LDAP_USER_PHONE_ATTRIBUTE("kadai.ldap.userPhoneAttribute"),
  KADAI_LDAP_USER_MOBILE_PHONE_ATTRIBUTE("kadai.ldap.userMobilePhoneAttribute"),
  KADAI_LDAP_USER_EMAIL_ATTRIBUTE("kadai.ldap.userEmailAttribute"),
  KADAI_LDAP_USER_ID_ATTRIBUTE("kadai.ldap.userIdAttribute"),
  KADAI_LDAP_USER_ORG_LEVEL_1_ATTRIBUTE("kadai.ldap.userOrglevel1Attribute"),
  KADAI_LDAP_USER_ORG_LEVEL_2_ATTRIBUTE("kadai.ldap.userOrglevel2Attribute"),
  KADAI_LDAP_USER_ORG_LEVEL_3_ATTRIBUTE("kadai.ldap.userOrglevel3Attribute"),
  KADAI_LDAP_USER_ORG_LEVEL_4_ATTRIBUTE("kadai.ldap.userOrglevel4Attribute"),
  KADAI_LDAP_USER_MEMBER_OF_GROUP_ATTRIBUTE("kadai.ldap.userMemberOfGroupAttribute"),
  KADAI_LDAP_USER_PERMISSIONS_ATTRIBUTE("kadai.ldap.userPermissionsAttribute"),
  KADAI_LDAP_GROUP_SEARCH_BASE("kadai.ldap.groupSearchBase"),
  KADAI_LDAP_BASE_DN("kadai.ldap.baseDn"),
  KADAI_LDAP_GROUP_SEARCH_FILTER_NAME("kadai.ldap.groupSearchFilterName"),
  KADAI_LDAP_GROUP_SEARCH_FILTER_VALUE("kadai.ldap.groupSearchFilterValue"),
  KADAI_LDAP_GROUP_NAME_ATTRIBUTE("kadai.ldap.groupNameAttribute"),
  KADAI_LDAP_MIN_SEARCH_FOR_LENGTH("kadai.ldap.minSearchForLength"),
  KADAI_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS("kadai.ldap.maxNumberOfReturnedAccessIds"),
  KADAI_LDAP_GROUPS_OF_USER("kadai.ldap.groupsOfUser"),
  KADAI_LDAP_GROUPS_OF_USER_NAME("kadai.ldap.groupsOfUser.name"),
  KADAI_LDAP_GROUPS_OF_USER_TYPE("kadai.ldap.groupsOfUser.type");

  private final String key;

  LdapSettings(String key) {
    this.key = key;
  }

  String getKey() {
    return key;
  }

  String getValueFromEnv(Environment env) {
    if (env == null) {
      return null;
    }
    return env.getProperty(key);
  }

  @Override
  public String toString() {
    return key;
  }
}
