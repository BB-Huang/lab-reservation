package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.CreateReservationRequest;
import asia.bbtlry.lab.service.ReservationService;
import asia.bbtlry.lab.vo.PageResult;
import asia.bbtlry.lab.vo.ReservationVO;
import jakarta.validation.Valid;
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
}
