package com.example.public_transportation_project.Controllers;

import com.example.public_transportation_project.Models.Bus;
import com.example.public_transportation_project.Models.BusStatus;
import com.example.public_transportation_project.DTOs.BusRequest;
import com.example.public_transportation_project.DTOs.BusResponse;
import com.example.public_transportation_project.Services.BusService;
import com.example.public_transportation_project.Models.ArchiveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
@Tag(name = "Bus management", description = "פעולות לניהול צי הרכבים של החברה")
public class BusController {

    @Autowired
    private BusService busService;

    @GetMapping
    @Operation(summary = "שליפת נתונים עם סינון ארכיון")
    public List<Bus> getAll(
            @RequestParam(required = false) BusStatus status,
            @RequestParam(defaultValue = "ACTIVE_ONLY") ArchiveMode archiveMode) {
        return busService.getBusesAdvanced(status, archiveMode);
    }

    @GetMapping("/needing-test")
    @Operation(summary = "אוטובוסים שזקוקים לטסט", description = "שולף אוטובוסים שהטסט האחרון שלהם היה לפני יותר משנה")
    public List<Bus> getNeedingTest() {
        return busService.getBusesNeedingTest();
    }

    @PostMapping
    
    @Operation(summary = "רישום אוטובוס חדש", description = "מנהל בלבד, סטטוסים אפשריים: AVAILABLE, IN_SERVICE, UNDER_MAINTENANCE, OUT_OF_ORDER")
    public ResponseEntity<BusResponse> save(@Valid @RequestBody BusRequest dto) {
        BusResponse response = busService.saveBus(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    
    @Operation(summary = "עדכון פרטי אוטובוס",
            description = "סטטוסים אפשריים: AVAILABLE, IN_SERVICE, UNDER_MAINTENANCE, OUT_OF_ORDER")
    public Bus update(@PathVariable Long id, @Valid @RequestBody BusRequest dto) {
        return busService.updateBus(id, dto);
    }

    @DeleteMapping("/{id}")
    
    @Operation(summary = "מחיקה רכה של אוטובוס")
    public void delete(@PathVariable Long id) {
        busService.deleteBus(id);
    }

    @PutMapping("/{id}/restore")
    
    @Operation(summary = "שחזור אוטובוס מהארכיון")
    public void restore(@PathVariable Long id) {
        busService.restoreBus(id);
    }

    @DeleteMapping("/{id}/force")
    
    @Operation(summary = "מחיקה סופית מהמסד", description = "זהירות: פעולה זו אינה ניתנת לביטול")
    public void forceDelete(@PathVariable Long id) {
        busService.forceHardDelete(id);
    }
}
