package com.partyquest.backend.service.impl.oauth2.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {
    long id;
    String connected_at;
    KakaoAccount kakao_account;
    Properties properties;

    @Data
    public class KakaoAccount {
        private boolean profile_nickname_needs_agreement;
        private boolean profile_image_needs_agreement;
        private Profile profile;
        private boolean has_email;
        private boolean email_needs_agreement;
        private boolean is_email_valid;
        private boolean is_email_verified;
        private String email;

        @Data
        public class Profile {
            private String nickname;
            private String profile_image_url;
            private String thumbnail_image_url;
            private boolean is_default_image;
        }
    }

    @Data
    public class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }
}
