package com.example.public_transportation_project.Controllers;

import com.example.public_transportation_project.Models.Travel;
import com.example.public_transportation_project.Services.TravelService;
import com.example.public_transportation_project.DTOs.TravelRequest;
import com.example.public_transportation_project.DTOs.TravelResponseDTO;
import com.example.public_transportation_project.Models.ArchiveMode;
import com.example.public_transportation_project.Models.TravelStatus;
import com.example.public_transportation_project.Mappers.TravelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/travels")
@Tag(name = "Travel Management", description = "ניהול לוחות זמנים, שיבוץ נהגים ואוטובוסים")
public class TravelController {

    @Autowired
    private TravelService travelService;

    @PostMapping
    
    @Operation(summary = "שיבוץ נסיעה חדשה", description = "יוצר נסיעה תוך בדיקת זמינות נהג, אוטובוס וחישוב זמן הגעה דינמי")
    public ResponseEntity<TravelResponseDTO> schedule(@Valid @RequestBody TravelRequest dto) {
        Travel newTravel = travelService.scheduleTravel(dto);
        TravelResponseDTO response = TravelMapper.toResponseDTO(newTravel);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/line-status/{lineId}")
    @Operation(summary = "מיקומי אוטובוסים בזמן אמת", description = "מציג היכן כל אוטובוס נמצא כרגע על ציר הקו")
    public ResponseEntity<List<String>> getBusLocations(@PathVariable Long lineId) {
        return ResponseEntity.ok(travelService.getBusLocationsOnLine(lineId));
    }

    @PatchMapping("/{id}")
    
    @Operation(summary = "עדכון נסיעה")
    public ResponseEntity<TravelResponseDTO> update(@PathVariable Long id, @RequestBody TravelRequest dto) {
        return ResponseEntity.ok(travelService.updateTravel(id, dto));
    }

    @DeleteMapping("/{id}/archive")
    
    @Operation(summary = "מחיקה לארכיון")
    public ResponseEntity<Void> deleteToArchive(@PathVariable Long id) {
        travelService.deleteToArchive(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    
    @Operation(summary = "שחזור נסיעה מהארכיון")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        travelService.restoreFromArchive(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/hard-delete")
    
    @Operation(summary = "מחיקה סופית מהמערכת (למנהל)")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        travelService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/all")
    
    @Operation(summary = "שליפה מנהלית חכמה", description = "סינון משולב של מצב ארכיון וסטטוס נסיעה")
    public ResponseEntity<List<TravelResponseDTO>> getAllForAdmin(
            @RequestParam(defaultValue = "ACTIVE_ONLY") ArchiveMode archiveMode,
            @RequestParam(required = false) TravelStatus status) { // status הוא אופציונלי

        return ResponseEntity.ok(travelService.getFilteredTravelsForAdmin(archiveMode, status));
    }

    @GetMapping("/search")
    @Operation(summary = "חיפוש נסיעה לנוסע", description = "חיפוש לפי עיר מוצא ויעד עבור נסיעות עתידיות")
    public ResponseEntity<List<TravelResponseDTO>> search(@RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(travelService.searchTravels(from, to));
    }

    @GetMapping("/admin/stats/most-active-line")
    @Operation(summary = "סטטיסטיקה: הקו הכי פעיל", tags = {"Admin Operations"})
    public ResponseEntity<String> getMostActiveLine() {
        return ResponseEntity.ok(travelService.getMostActiveLine());
    }

    @GetMapping("/admin/stats/drivers")
    @Operation(summary = "סטטיסטיקת נסיעות לנהגים", tags = {"Admin Operations"})
    public ResponseEntity<List<String>> getDriverStats() {
        return ResponseEntity.ok(travelService.getDriverTravelStats());
    }

    @GetMapping("/line/{lineId}/arrival/{stationOrder}")
    @Operation(summary = "קל קו: מתי האוטובוס בתחנה?", description = "מחשב הגעה לפי דקה לכל תחנה")
    public ResponseEntity<String> getArrival(@PathVariable Long lineId, @PathVariable int stationOrder) {
        return ResponseEntity.ok(travelService.getArrivalTimeForStation(lineId, stationOrder));
    }

    @GetMapping("/line/{lineId}/stations-list")
    @Operation(summary = "קל קו: רשימת תחנות", description = "שמיעת/ראיית כל התחנות בקו")
    public ResponseEntity<List<String>> getStationsList(@PathVariable Long lineId) {
        return ResponseEntity.ok(travelService.getAllStationsOnLine(lineId));
    }

}
