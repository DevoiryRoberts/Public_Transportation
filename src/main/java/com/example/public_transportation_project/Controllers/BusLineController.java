package com.example.public_transportation_project.Controllers;

import com.example.public_transportation_project.Models.BusLine;
import com.example.public_transportation_project.Models.ArchiveMode;
import com.example.public_transportation_project.Services.BusLineService;
import com.example.public_transportation_project.DTOs.BusLineRequest;
import com.example.public_transportation_project.DTOs.BusLineResponse;
import com.example.public_transportation_project.DTOs.BusLineUpdate;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid; // חובה עבור הולידציה
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lines") 
@Tag(name = "Bus Line Management", description = "פעולות לניהול מסלולי קווים ותחנות באוטובוס")
public class BusLineController {

    @Autowired
    private BusLineService busLineService;

    @GetMapping 
    @Operation(summary = "שליפת כל קווי האוטובוס הפעילים")
    public ResponseEntity<List<BusLineResponse>> getAllLines() {
        return ResponseEntity.ok(busLineService.getLinesByArchiveMode(ArchiveMode.ACTIVE_ONLY));
    }

    @PostMapping
    @Operation(summary = "יצירת קו חדש או שחזור אוטומטי מהארכיון")
    public ResponseEntity<BusLineResponse> save(@Valid @RequestBody BusLineRequest dto) {
        return ResponseEntity.ok(busLineService.createBusLine(dto));
    }

    @PostMapping("/{lineId}/stations/{stationId}")
    @Operation(summary = "שדרוג חכם: הוספת תחנה לקו במיקום ספציפי ודחיפת השאר קדימה")
    public ResponseEntity<String> addStation(@PathVariable Long lineId, @PathVariable Long stationId, @RequestParam int order) {
        busLineService.addStationToLine(lineId, stationId, order);
        return ResponseEntity.ok("התחנה נוספה בהצלחה והמסלול עודכן.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "עדכון פרטי קו קיים")
    public ResponseEntity<BusLine> update(@PathVariable Long id, @RequestBody BusLineUpdate dto) {
        return ResponseEntity.ok(busLineService.updateBusLine(id, dto));
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    @Operation(summary = "מחיקת תחנה מקו מסוים")
    public ResponseEntity<Void> removeStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        busLineService.removeStationFromLine(lineId, stationId); // קריאה ללא return
        return ResponseEntity.noContent().build(); // החזרת תשובה ריקה (204)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "מחיקת קו (העברה לארכיון)")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        busLineService.deleteBusLine(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard-delete")
    @Operation(summary = "מחיקה לצמיתות מהמסד (למנהלים בלבד!)")
    public ResponseEntity<?> hardDelete(@PathVariable Long id) {
        try {
            busLineService.hardDeleteBusLine(id);
            return ResponseEntity.ok("הקו נמחק לצמיתות בהצלחה");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("שגיאה במחיקה: ודא שכל הנתונים המקושרים נמחקו קודם.");
        }
    }

    @GetMapping("/manage")
    @Operation(summary = "צפייה בקווים לפי מצב ארכיון (למנהלים)")
    public ResponseEntity<List<BusLineResponse>> getLinesByMode(@RequestParam ArchiveMode mode) {
        return ResponseEntity.ok(busLineService.getLinesByArchiveMode(mode));
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "שחזור קו: החזרת קו מהארכיון למצב פעיל")
    public ResponseEntity<BusLineResponse> restoreBusLine(@PathVariable Long id) {
        BusLineResponse response = busLineService.restoreBusLine(id);
        return ResponseEntity.ok(response);
    }
}
