package asia.bbtlry.lab.controller;

import asia.bbtlry.lab.common.Result;
import asia.bbtlry.lab.dto.CreateReservationRequest;
import asia.bbtlry.lab.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
