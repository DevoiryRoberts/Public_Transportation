package com.example.public_transportation_project.Controllers;

import com.example.public_transportation_project.Models.Station;
import com.example.public_transportation_project.DTOs.StationRequest;
import com.example.public_transportation_project.Services.StationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.public_transportation_project.Models.ArchiveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Tag(name = "Station management", description = "פעולות עבור תחנות האוטובוס במערכת")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping
    @Operation(summary = "שליפת תחנות", description = "ניתן לבחור האם לראות תחנות מהארכיון")
    public List<Station> getAll(@RequestParam(defaultValue = "ACTIVE_ONLY") ArchiveMode archiveMode) {
        return stationService.getStationsAdvanced(archiveMode);
    }

    @PostMapping
    
    @Operation(summary = "יצירת תחנה חדשה", description = "מנהל בלבד יכול להוסיף תחנה חדשה למערכת")
    public Station create(@Valid @RequestBody StationRequest dto) {
        return stationService.createStation(dto);
    }

    @PutMapping("/{id}")
    
    @Operation(summary = "עדכון פרטי תחנה", description = "עדכון שם או כתובת של תחנה קיימת")
    public Station update(@PathVariable Long id, @Valid @RequestBody StationRequest dto) {
        return stationService.updateStation(id, dto);
    }

    @DeleteMapping("/{id}")
    
    @Operation(summary = "מחיקת תחנה (ארכיון)", description = "ביצוע מחיקה רכה לתחנה שאינה בשימוש")
    public void delete(@PathVariable Long id) {
        stationService.deleteStation(id);
    }

    @PutMapping("/{id}/restore")
    
    @Operation(summary = "שחזור תחנה", description = "החזרת תחנה מהארכיון למצב פעיל")
    public void restore(@PathVariable Long id) {
        stationService.restoreStation(id);
    }

    @DeleteMapping("/{id}/force")
    
    @Operation(summary = "מחיקה סופית", description = "מחיקה פיזית מהמסד. לשימוש רק במקרים שהתחנה הוקמה בטעות מוחלטת")
    public void forceDelete(@PathVariable Long id) {
        stationService.forceHardDelete(id);
    }
}
