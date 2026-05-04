package com.example.public_transportation_project.Controllers;

import com.example.public_transportation_project.Models.Driver;
import com.example.public_transportation_project.Services.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.public_transportation_project.DTOs.DriverRequest;
import jakarta.validation.Valid;
import com.fasterxml.jackson.annotation.JsonView;
import com.example.public_transportation_project.Config.Views;
import com.example.public_transportation_project.DTOs.DriverUpdate;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Driver management", description = "פעולות להוספה, צפייה ומחיקת נהגים")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(summary = "הצגת כל הנהגים", description = "מחזיר רשימה של כל הנהגים הפעילים בלבד")
    public List<Driver> getAll() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/admin/all")
    @JsonView(Views.Admin.class)
    
    @Operation(summary = "ניהול נהגים - תצוגה מלאה", description = "מאפשר למנהל לראות את כל הפרטים הרגישים של כל הנהגים")
    public List<Driver> getAllForAdmin() {
        return driverService.getAllDrivers();
    }

    @PostMapping
    @JsonView(Views.Admin.class)
    @Operation(summary = "רישום נהג חדש", description = "יוצר נהג חדש. השדות הנדרשים הם שם, טלפון, אימייל ורישיון בלבד.")
    public Driver create(@Valid @RequestBody DriverRequest driverDto) {
        return driverService.createDriver(driverDto);
    }

    @PatchMapping("/{id}")
    @JsonView(Views.Admin.class)
    @Operation(summary = "עדכון חלקי של נהג", description = "ניתן לשלוח רק את השדות שרוצים לשנות. שדות שלא יישלחו יישארו ללא שינוי.")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Driver patchUpdate(@PathVariable Long id, @Valid @RequestBody DriverUpdate updateDto) {
        return driverService.patchDriver(id, updateDto);
    }

    @PostMapping("/admin/create")
    @JsonView(Views.Admin.class)
    @Operation(summary = "יצירת מנהל מערכת חדש", description = "מאפשר למנהל קיים להוסיף משתמש חדש עם הרשאות ניהול מלאות. המערכת מגדירה אוטומטית את התפקיד כ-ADMIN.")
    
    public Driver createAdmin(@Valid @RequestBody DriverRequest newAdmin) {
        Driver createdDriver = driverService.createDriver(newAdmin);

        return driverService.updateDriverRoleToAdmin(createdDriver.getId());
    }

    @DeleteMapping("/{id}")
    @JsonView(Views.Public.class)
    @Operation(summary = "מחיקה לוגית (ארכיון)", description = "מעביר נהג למצב לא פעיל. מנהל לא יכול למחוק את עצמו אם הוא האחרון.")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public void delete(@PathVariable Long id, Principal principal) {
        driverService.deleteDriver(id, principal.getName());
    }

    @PutMapping("/admin/restore/{id}")
    @JsonView(Views.Admin.class)
    @Operation(summary = "שחזור נהג", description = "מחזיר נהג מהארכיון למצב פעיל (למנהלים בלבד)")
    
    public void restore(@PathVariable Long id) {
        driverService.restoreDriver(id);
    }

    @GetMapping("/admin/deletion-requests")
    @JsonView(Views.Admin.class)
    @Operation(summary = "הצגת בקשות למחיקה לצמיתות", description = "שולף רשימה של כל הנהגים שסימנו כי הם מעוניינים שהחשבון שלהם יימחק סופית ממסד הנתונים.")
    
    public List<Driver> getRequests() {
        return driverService.getDriversRequestingDeletion();
    }

    @DeleteMapping("/admin/force-delete/{id}")
    @JsonView(Views.Admin.class)
    @Operation(summary = "מחיקה סופית", description = "מוחק את הנהג לצמיתות ממסד הנתונים (למנהלים בלבד)")
    
    public void forceDelete(@PathVariable Long id) {
        driverService.forcePermanentDelete(id);
    }

    @PutMapping("/{id}/request-deletion")
    @JsonView(Views.Public.class)
    @Operation(summary = "בקשת מחיקה לצמיתות", description = "מאפשר לנהג לסמן למנהל שהוא מעוניין שחשבונו יימחק לצמיתות.")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public void requestDeletion(@PathVariable Long id) {
        driverService.requestPermanentDeletion(id);
    }

    @GetMapping("/top-rated")
    @JsonView(Views.Admin.class)
    @Operation(summary = "נהגים מצטיינים", description = "מחזיר רשימה של כל הנהגים שקיבלו דירוג 5")
    public List<Driver> getTopDrivers() {
        return driverService.getTopRatedDrivers();
    }

    @PatchMapping("/{id}/rating")
    @JsonView(Views.Admin.class)
    @Operation(summary = "עדכון דירוג נהג", description = "מאפשר למנהל לעדכן דירוג של נהג ספציפי (1-5)")
    
    public void updateRating(@PathVariable Long id, @RequestParam int newRating) {
        driverService.updateDriverRating(id, newRating);
    }

    @PostMapping("/{id}/rate")
    @JsonView(Views.Public.class)
    @Operation(summary = "דירוג נהג ע\"י נוסע", description = "מאפשר לשלוח דירוג (1-5) שמשתקלל לתוך הממוצע של הנהג")
    public ResponseEntity<String> rateDriver(@PathVariable Long id, @RequestParam int stars) {
        driverService.rateDriver(id, stars);
        return ResponseEntity.ok("תודה! הדירוג התקבל במערכת.");
    }

    @GetMapping("/exemplary")
    @JsonView(Views.Admin.class)
    @Operation(summary = "נהגים מצטיינים", description = "שליפת כל הנהגים עם דירוג 4 ומעלה")
    public List<Driver> getExemplary() {
        return driverService.getExemplaryDrivers();
    }
}
