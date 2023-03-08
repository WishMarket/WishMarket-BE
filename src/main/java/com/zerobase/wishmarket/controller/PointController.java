package com.zerobase.wishmarket.controller;

import com.zerobase.wishmarket.domain.point.service.PointService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("포인트 충전 API")
@RequiredArgsConstructor
@RestController
public class PointController {

    private final PointService pointService;

    @PutMapping("/api/point/increase") //1만포인트를 일괄적으로 증가시키도록 설정함
    public ResponseEntity<?> addPoint(@AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok().body(pointService.chargePoint(userId));
    }
}
