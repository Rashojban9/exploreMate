package com.exploreMate.auth_service.kafka;

import lombok.Builder;

@Builder
public record KafkaResDto(String email,String subject,String body) {

}
