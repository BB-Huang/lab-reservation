package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.CreateReservationRequest;
import asia.bbtlry.lab.dto.ReviewRequest;
import asia.bbtlry.lab.service.ReservationService;
import asia.bbtlry.lab.vo.PageResult;
import asia.bbtlry.lab.vo.ReservationVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Result<Long> create(@RequestBody @Valid CreateReservationRequest request, @AuthenticationPrincipal Long userId) {
        return Result.success(reservationService.create(request, userId));
    }

    @GetMapping("/mine")
    public Result<PageResult<ReservationVO>> mine(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(reservationService.myReservations(userId, page,
                size, status));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        reservationService.cancel(id, userId);
        return Result.success();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<PageResult<ReservationVO>> pending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(reservationService.pending(page, size));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Void> approve(@PathVariable Long id,
                                @AuthenticationPrincipal Long reviewerId) {
        reservationService.approve(id, reviewerId);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<Void> reject(@PathVariable Long id,
                               @AuthenticationPrincipal Long reviewerId,
                               @RequestBody(required = false) ReviewRequest request) {
        reservationService.reject(id, reviewerId, request == null ? null : request.getComment());
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ReservationVO> detail(@PathVariable Long id,
                                        @AuthenticationPrincipal Long userId,
                                        Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(reservationService.getDetail(id, userId, isAdmin));
    }
}
